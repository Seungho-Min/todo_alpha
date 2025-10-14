package todo_alpha.todo_alpha.feignTest.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import todo_alpha.todo_alpha.feignTest.client.CulturalEventClient
import todo_alpha.todo_alpha.feignTest.domain.EventImage
import todo_alpha.todo_alpha.feignTest.domain.PublicEvent
import todo_alpha.todo_alpha.feignTest.domain.embeddable.AudienceMeta
import todo_alpha.todo_alpha.feignTest.domain.embeddable.PlaceInfo
import todo_alpha.todo_alpha.feignTest.domain.enums.EventCategory
import todo_alpha.todo_alpha.feignTest.domain.repository.EventImageRepository
import todo_alpha.todo_alpha.feignTest.domain.repository.PublicEventRepository
import todo_alpha.todo_alpha.feignTest.dto.CulturalEventInfoResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class CulturalEventService(
    private val culturalEventClient: CulturalEventClient,
    private val publicEventRepository: PublicEventRepository,
    private val eventImageRepository: EventImageRepository,
    private val objectMapper: ObjectMapper,
    @Value("\${seoul.api-key}") private val apiKey: String,
    @Value("\${seoul.type:json}") private val type: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun fetchAndSaveEvents(
        start: Int,
        end: Int,
        codename: String? = null,
        title: String? = null,
        date: String? = null
    ) {
        val response = culturalEventClient.getEvents(
            key = apiKey,
            type = type,
            startIndex = start,
            endIndex = end,
            codename = codename,
            title = title,
            date = date
        )

        response.culturalEventInfo?.row?.let { culturalEventRows ->
            val publicEvents = culturalEventRows.mapNotNull { row ->
                val sourceEventId = extractCultCodeFromUrl(row.HMPG_ADDR.toString()) ?: return@mapNotNull null

                if (publicEventRepository.existsBySourceAndSourceEventId("seoul-cultural-event", sourceEventId)) {
                    return@mapNotNull null
                }
                mapRowToPublicEvent(row, sourceEventId)
            }

            publicEventRepository.saveAll(publicEvents)

            val eventImages = publicEvents.mapNotNull { publicEvent ->
                publicEvent.meta.imageUrl?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                    EventImage(
                        event = publicEvent,
                        url = imageUrl,
                        ingestedAt = publicEvent.ingestedAt
                    )
                }
            }
            eventImageRepository.saveAll(eventImages)
        }
    }

    fun fetchAllEventsAndSave() {
        // api 최대 허용치 1000 설정
        val pageSize = 1000

        logger.info("Fetching total event count...")
        val initialResponse = culturalEventClient.getEvents(key = apiKey, type = type, startIndex = 1, endIndex = 1)
        val totalCount = initialResponse.culturalEventInfo?.list_total_count ?: 0

        if (totalCount == 0) {
            logger.info("No events to fetch")
            return
        }
        logger.info("Total event to fetch: $totalCount")

        var startIndex = 1
        while (startIndex <= totalCount) {
            val endIndex = (startIndex + pageSize-1).coerceAtMost(totalCount)
            logger.info("Fetching events from $startIndex to $endIndex...")

            try {
                // 기존 페이지 단위 조회 및 저장 로직 재사용.
                fetchAndSaveEvents(start = startIndex, end = endIndex)
            } catch (e: Exception) {
                // 특정 페이지에서 에러 발생 시 로그 남기고 다음 페이지로 넘어감.
                logger.error("Error while fetching events from $startIndex to $endIndex", e)
            }

            // 다음 페이지 시작 인덱스로 이동.
            startIndex += pageSize
        }
        logger.info("Finished fetching all events.")
    }

    private fun mapRowToPublicEvent(row: CulturalEventInfoResponse.Row, sourceEventId: String): PublicEvent {
        val now = LocalDateTime.now()
        val placeInfo = PlaceInfo(
            placeName = row.PLACE,
            placeAddress = row.GUNAME, // API 응답에 따라 상세 주소 필드로 변경
            placeCity = "서울특별시", // 서울 정보이기에 고정
            placeDistrict = row.GUNAME,
            placeLatitude = row.LAT?.toDoubleOrNull(),
            placeLongitude = row.LOT?.toDoubleOrNull(),
        )

        val audienceMeta = AudienceMeta(
            priceText = row.USE_FEE,
            audience = row.USE_TRGT,
            contact = row.PLAYER, // 담당자 또는 연락처 정보 필드로 변경
            url = row.ORG_LINK,
            imageUrl = row.MAIN_IMG
        )

        return PublicEvent(
            source = "seoul-cultural-event", // 데이터 출처
            sourceEventId = sourceEventId,   // API의 고유 ID
            title = row.TITLE!!,
            description = row.PROGRAM, // 'PROGRAM' 필드를 설명으로 사용
            category = mapToCategory(row.CODENAME),
            startAt = parseDateTime(row.STRTDATE),
            endAt = parseDateTime(row.END_DATE),
            place = placeInfo,
            meta = audienceMeta,
            rawPayload = objectMapper.writeValueAsString(row), // 원본 데이터를 JSON 문자열로 저장
            ingestedAt = now
        )
    }

    private fun mapToCategory(codename: String?): EventCategory {
        return when (codename?.trim()) {
            "뮤지컬/오페라" -> EventCategory.MUSICAL
            "연극" -> EventCategory.THEATER
            "영화" -> EventCategory.MOVIE
            "전시/미술" -> EventCategory.EXHIBITION

            "콘서트", "클래식", "국악", "무용" -> EventCategory.CONCERT

            "축제-문화/예술", "축제-전통/역사", "축제-기타", "축제" -> EventCategory.FESTIVAL

            "교육/체험" -> EventCategory.WORKSHOP
            "봉사" -> EventCategory.VOLUNTEER
            "독서" -> EventCategory.READING
            "스포츠" -> EventCategory.SPORTS
            "여행" -> EventCategory.TRAVEL
            "야외활동" -> EventCategory.OUTDOOR
            "취미" -> EventCategory.HOBBY
            "스터디" -> EventCategory.STUDY
            "네트워킹" -> EventCategory.NETWORKING

            else -> EventCategory.OTHER
        }
    }


    private fun parseDateTime(dateTimeStr: String?): LocalDateTime? {
        if (dateTimeStr.isNullOrBlank()) return null
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
            LocalDateTime.parse(dateTimeStr, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    // url에서 cultcode를 추출
    private fun extractCultCodeFromUrl(url: String): String? {
        if (url == null) return null
        val regex = "cultcode=([0-9]+)".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }
}
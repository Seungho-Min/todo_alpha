package todo_alpha.todo_alpha.feignTest.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CulturalEventInfoResponse (
    val culturalEventInfo: CulturalEventInfo?
) {
    data class CulturalEventInfo(
        val list_total_count: Int?,
        val result: Result?,
        val row: List<Row>?
    )

    data class Result(
        val CODE: String?,
        val MESSAGE: String?
    )

    data class Row(
        val CODENAME: String?,
        val GUNAME: String?,
        val TITLE: String?,
        val DATE: String?,
        val PLACE: String?,
        val ORG_NAME: String?,
        val USE_TRGT: String?,
        val USE_FEE: String?,
        val PLAYER: String?,
        val PROGRAM: String?,
        val ETC_DESC: String?,
        val ORG_LINK: String?,
        val MAIN_IMG: String?,
        val RGSTDATE: String?,
        val TICKET: String?,
        val STRTDATE: String?,
        val END_DATE: String?,
        val THEMECODE: String?,
        val LOT: String?,   // 위도(Y)
        val LAT: String?,   // 경도(X)
        val IS_FREE: String?,
        val HMPG_ADDR: String?
    )
}
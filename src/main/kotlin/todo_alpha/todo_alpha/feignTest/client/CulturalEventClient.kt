package todo_alpha.todo_alpha.feignTest.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import todo_alpha.todo_alpha.feignTest.dto.CulturalEventInfoResponse

@FeignClient(
    name = "CulturalEventClient",
    url = "\${seoul.base-url}"
)
interface CulturalEventClient {
    @GetMapping("/{key}/{type}/culturalEventInfo/{start}/{end}")
    fun getEvents(
        @PathVariable("key") key: String,
        @PathVariable("type") type: String = "json",
        @PathVariable("start") startIndex: Int,
        @PathVariable("end") endIndex: Int,
        // 선택 필터 (쿼리로 전달)
        @RequestParam("CODENAME", required = false) codename: String? = null,
        @RequestParam("TITLE", required = false) title: String? = null,
        @RequestParam("DATE", required = false) date: String? = null
    ): CulturalEventInfoResponse
}
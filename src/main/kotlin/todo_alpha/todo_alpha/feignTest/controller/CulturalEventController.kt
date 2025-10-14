package todo_alpha.todo_alpha.feignTest.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import todo_alpha.todo_alpha.feignTest.service.CulturalEventService

@RestController
class CulturalEventController(
    private val culturalEventService: CulturalEventService
) {
    @GetMapping("/api/seoul/cultural-events")
    fun events(
        @RequestParam start: Int,
        @RequestParam end: Int,
        @RequestParam(required = false) codename: String?,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) date: String?
    ) = culturalEventService.fetchAndSaveEvents(start, end, codename, title, date)

    @PostMapping("/api/seoul/cultural-events/ingest-all")
    fun ingestAllEvents(): ResponseEntity<String> {
        culturalEventService.fetchAllEventsAndSave()
        return ResponseEntity.ok("All cultural events ingestion has been started.")
    }
}
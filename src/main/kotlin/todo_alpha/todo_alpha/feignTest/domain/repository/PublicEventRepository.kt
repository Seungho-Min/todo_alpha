package todo_alpha.todo_alpha.feignTest.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import todo_alpha.todo_alpha.feignTest.domain.PublicEvent

interface PublicEventRepository : JpaRepository<PublicEvent, String> {
    fun findByIdAndDeletedAtIsNull(id: String): PublicEvent?
    fun existsBySourceAndSourceEventId(source: String, sourceEventId: String): Boolean
}

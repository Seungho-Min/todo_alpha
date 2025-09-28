package todo_alpha.todo_alpha.feignTest.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import todo_alpha.todo_alpha.feignTest.domain.EventImage

interface EvetImageRepository : JpaRepository<EventImage, String> {
}
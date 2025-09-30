package todo_alpha.todo_alpha.todo.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import todo_alpha.todo_alpha.todo.domain.Todo
import todo_alpha.todo_alpha.todo.domain.User
import todo_alpha.todo_alpha.todo.dto.TodoResponseDto

@Repository
interface TodoRepository : JpaRepository<Todo, Long>{

    // todo 전체 조회
    fun findByDeletedAtIsNull(): List<Todo>

    // todo id 상세조회
    fun findByIdAndDeletedAtIsNull(id: Long): Todo?

    // user별 todo 조회
    fun findByUserId(userId: String): List<Todo>

    // 삭제 확인용
    fun existsByIdAndDeletedAtIsNull(id: Long): Boolean

    // 페이징
    fun findByDeletedAtIsNull(pageable: Pageable): Page<Todo>
}
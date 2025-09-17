package todo_alpha.todo_alpha.todo.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import todo_alpha.todo_alpha.todo.domain.Todo

@Repository
interface TodoRepository : JpaRepository<Todo, Long>{

    // todo 전체 조회
    fun findByDeletedAtIsNull(): List<Todo>

    // todo id 상세조회
    fun findByIdAndDeletedAtIsNull(id: Long): Todo?

    // 삭제 확인용
    fun existsByIdAndDeletedAtIsNull(id: Long): Boolean
}
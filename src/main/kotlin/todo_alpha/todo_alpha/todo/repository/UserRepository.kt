package todo_alpha.todo_alpha.todo.repository

import org.springframework.data.jpa.repository.JpaRepository
import todo_alpha.todo_alpha.todo.domain.User

interface UserRepository: JpaRepository<User, Long>{
    fun findByUsername(username: String): User?
    fun findByIdOrNull(id: String): User?
}
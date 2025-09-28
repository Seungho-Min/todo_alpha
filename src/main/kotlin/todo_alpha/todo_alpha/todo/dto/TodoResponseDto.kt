package todo_alpha.todo_alpha.todo.dto

import todo_alpha.todo_alpha.todo.domain.Todo
import java.time.LocalDateTime
import kotlin.contracts.contract

data class TodoResponseDto (
    val id: Long,
    val title: String,
    val content: String,
    val dueDate: LocalDateTime?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime
) {
    constructor(todo: Todo) : this(
        id = todo.id!!,
        title = todo.title,
        content = todo.content,
        dueDate = todo.dueDate,
        isCompleted = todo.isCompleted,
        createdAt = todo.createdAt
    )
}
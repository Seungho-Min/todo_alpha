package todo_alpha.todo_alpha.todo.dto

import java.time.LocalDateTime

data class TodoResponseDto (
    val id: Long,
    val title: String,
    val content: String,
    val dueDate: LocalDateTime?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime
)
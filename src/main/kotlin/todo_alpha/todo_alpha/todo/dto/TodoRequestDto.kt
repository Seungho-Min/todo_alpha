package todo_alpha.todo_alpha.todo.dto

import java.time.LocalDateTime

data class TodoRequestDto (
    val title: String,
    val content: String?,
    val dueDate: LocalDateTime?
) {

}


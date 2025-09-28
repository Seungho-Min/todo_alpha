package todo_alpha.todo_alpha.todo.dto

data class TodoPageResponseDto(
    val content : List<TodoResponseDto>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
)

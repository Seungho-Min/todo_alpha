package todo_alpha.todo_alpha.todo.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import todo_alpha.todo_alpha.todo.domain.Todo
import todo_alpha.todo_alpha.todo.dto.TodoPageResponseDto
import todo_alpha.todo_alpha.todo.dto.TodoRequestDto
import todo_alpha.todo_alpha.todo.dto.TodoResponseDto
import todo_alpha.todo_alpha.todo.repository.TodoRepository
import todo_alpha.todo_alpha.todo.repository.UserRepository
import java.time.LocalDateTime

@Service
class TodoService (
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository
){

    // create
    fun createTodo(
        userId: String,
        todoRequestDto: TodoRequestDto
    ): TodoResponseDto {
        val user = userRepository.findByIdOrNull(userId) ?: throw IllegalArgumentException("User not found")
        val todo = Todo(
            title = todoRequestDto.title,
            content = todoRequestDto.content,
            dueDate = todoRequestDto.dueDate,
            isCompleted = false,
            createdAt = LocalDateTime.now(),
            user = user
        )
        val saveTodo = todoRepository.save(todo)
        return TodoResponseDto(saveTodo)
    }

    fun updateTodo(todoId: Long, userId: String, todoRequestDto: TodoRequestDto): TodoResponseDto {
        val todo = todoRepository.findByIdOrNull(todoId) ?: throw IllegalArgumentException("Todo not found")

        if (todo.user.id != userId) {
            throw IllegalArgumentException("수정 권한이 없습니다")
        }

        todo.update(
            todoRequestDto.title,
            todoRequestDto.content,
            todoRequestDto.dueDate,
        )

        return TodoResponseDto(todo)
    }

    // read
    fun getAllTodos(): List<TodoResponseDto> {
        return todoRepository.findByDeletedAtIsNull().map { todo ->
            TodoResponseDto(
                id = todo.id!!,
                title = todo.title,
                content = todo.content,
                dueDate = todo.dueDate,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt,
                authorName = todo.user.name,
            )
        }
    }

    fun getTodosWithPaging(page: Int, size: Int): TodoPageResponseDto {

        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val todoPage = todoRepository.findByDeletedAtIsNull(pageable)

        val todoResponseList = todoPage.content.map { todo ->
            TodoResponseDto(
                id = todo.id!!,
                title = todo.title,
                content = todo.content,
                dueDate = todo.dueDate,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt,
                authorName = todo.user.name,
                )
        }
        return TodoPageResponseDto(
            content = todoResponseList,
            totalElements = todoPage.totalElements,
            totalPages = todoPage.totalPages,
            currentPage = todoPage.number,
            size = todoPage.size,
            hasNextPage = todoPage.hasNext(),
            hasPreviousPage = todoPage.hasPrevious()
        )
    }

    // 상세
    fun getTodoById(id: Long): TodoResponseDto {
        val todo = todoRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw IllegalArgumentException("Todo not found: $id")
        return TodoResponseDto(todo)
    }

    // 유저로 todo 조회
    fun getTodoByUserId(userId: String): List<TodoResponseDto> {
        return todoRepository.findByUserId(userId).map { todo ->
            TodoResponseDto(
                id = todo.id!!,
                title = todo.title,
                content = todo.content,
                dueDate = todo.dueDate,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt,
                authorName = todo.user.name,
            )
        }
    }

    // delete
    fun deleteTodo(id: Long, userId: String) {
        val todo = todoRepository.findByIdOrNull(id) ?: throw java.lang.IllegalArgumentException("Todo not found")

        if (todo.user.id != userId) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }

        todoRepository.delete(todo)
    }
}
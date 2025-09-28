package todo_alpha.todo_alpha.todo.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import todo_alpha.todo_alpha.todo.domain.Todo
import todo_alpha.todo_alpha.todo.dto.TodoPageResponseDto
import todo_alpha.todo_alpha.todo.dto.TodoRequestDto
import todo_alpha.todo_alpha.todo.dto.TodoResponseDto
import todo_alpha.todo_alpha.todo.repository.TodoRepository
import java.time.LocalDateTime

@Service
class TodoService (
    private val todoRepository: TodoRepository
){

    // create
    fun createTodo(
        todoRequestDto: TodoRequestDto
    ): TodoResponseDto {
        val todo = Todo(
            title = todoRequestDto.title,
            content = todoRequestDto.content,
            dueDate = todoRequestDto.dueDate,
            isCompleted = false,
            createdAt = LocalDateTime.now(),
        )
        val saveTodo = todoRepository.save(todo)
        return TodoResponseDto(saveTodo)
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

    // delete
    fun deleteTodo(id: Long) {
        if (todoRepository.existsByIdAndDeletedAtIsNull(id)) {
            todoRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("Todo not found or already deleted: $id")
        }
    }
}
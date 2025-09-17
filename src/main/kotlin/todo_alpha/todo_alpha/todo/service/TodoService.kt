package todo_alpha.todo_alpha.todo.service

import org.springframework.stereotype.Service
import todo_alpha.todo_alpha.todo.domain.Todo
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
        request: TodoRequestDto
    ): Todo {
        // 검증 하는 IF문
        if (request.content == null) throw IllegalArgumentException("Content is required")
        val todo = Todo(
            title = request.title,
            content = request.content,
            dueDate = request.dueDate,
            isCompleted = false,
            createdAt = LocalDateTime.now(),
        )
        return todoRepository.save(todo)
    }

    // read
    fun getAllTodos(): List<TodoResponseDto> {
        val todos = todoRepository.findByDeletedAtIsNull()
    }

    // 상세
    fun getTodoById(id: Long): TodoResponseDto {
        val todo = todoRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw IllegalArgumentException("Todo not found: $id")
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
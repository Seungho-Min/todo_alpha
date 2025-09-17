package todo_alpha.todo_alpha.todo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import todo_alpha.todo_alpha.todo.dto.TodoRequestDto
import todo_alpha.todo_alpha.todo.dto.TodoResponseDto
import todo_alpha.todo_alpha.todo.service.TodoService


@RestController
@RequestMapping("/todo")
class TodoController (
    private val todoService: TodoService
){
    @GetMapping
    fun getAllTodos(): ResponseEntity<List<TodoResponseDto>> {
        val todos = todoService.getAllTodos()
        val todoResponse = todos.map { todo ->
            TodoResponseDto(
                id = todo.id!!,
                title = todo.title,
                content = todo.content,
                dueDate = todo.dueDate,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt
            )
        }
        return ResponseEntity.ok(todoResponse)
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<TodoResponseDto> {
        return try {
            ResponseEntity.ok(todoService.getTodoById(id))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createTodo(@RequestBody request: TodoRequestDto): ResponseEntity<TodoResponseDto> {
        val todo = todoService.createTodo(
            title = request.title,
            content = request.content,
            dueDate = request.dueDate
        )

        val todoResponse = TodoResponseDto(
            id = todo.id!!,
            title = todo.title,
            content = todo.content,
            dueDate = todo.dueDate,
            isCompleted = todo.isCompleted,
            createdAt = todo.createdAt
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(todoResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            todoService.deleteTodo(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
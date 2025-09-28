package todo_alpha.todo_alpha.todo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import todo_alpha.todo_alpha.todo.dto.TodoPageResponseDto
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
        return ResponseEntity.ok(todos)
    }

    @GetMapping("/paged")
    fun getTodosWithPaging(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int,
        @RequestParam(value = "sort", defaultValue = "createdAt") sort: String = "createdAt",
        @RequestParam(value = "sortOrder", defaultValue = "desc") sortOrder: String = "desc",
    ): ResponseEntity<TodoPageResponseDto> {
        val validatedPage = if (page < 0) 0 else page
        val validatedSize = when {
            pageSize < 1 -> 10 // 음수일 경우 기본값 보정
            pageSize > 100 -> 100 // 최대값 100으로 한정
            else -> pageSize
        }

        val todos = todoService.getTodosWithPaging(validatedPage, validatedSize)
        return ResponseEntity.ok(todos)
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
        val todo = todoService.createTodo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(todo)
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
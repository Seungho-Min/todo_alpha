package todo_alpha.todo_alpha

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class TodoAlphaApplication

fun main(args: Array<String>) {
	runApplication<TodoAlphaApplication>(*args)
}

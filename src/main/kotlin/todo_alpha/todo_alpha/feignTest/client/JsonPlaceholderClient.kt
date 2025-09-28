package todo_alpha.todo_alpha.feignTest.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import todo_alpha.todo_alpha.feignTest.config.FeignConfig

data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

@FeignClient(
    name = "jsonPlaceholderClient",
    url = "https://jsonplaceholder.typicode.com",
    configuration = [FeignConfig::class]
)
interface JsonPlaceholderClient {
    @GetMapping("/posts/{id}")
    fun getPost(@PathVariable("id") id: Int): PostDto
}
package todo_alpha.todo_alpha.feignTest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import todo_alpha.todo_alpha.feignTest.client.JsonPlaceholderClient

@RestController
class TestController (
    private val jsonPlaceholderClient: JsonPlaceholderClient
){
    @GetMapping("/test/jsonplaceholder")
    fun testJson(@RequestParam id: Int): Any {
        return jsonPlaceholderClient.getPost(id)
    }
}
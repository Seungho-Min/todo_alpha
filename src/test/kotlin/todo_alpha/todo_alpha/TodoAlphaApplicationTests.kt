package todo_alpha.todo_alpha

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import todo_alpha.todo_alpha.todo.repository.TodoRepository
import todo_alpha.todo_alpha.todo.service.TodoService
import java.time.LocalDateTime
import kotlin.test.DefaultAsserter.fail

@SpringBootTest
@Transactional  // 각 테스트 후 롤백
class TodoAlphaApplicationTests {

    @Autowired
    private lateinit var todoService: TodoService

    @Autowired
    private lateinit var todoRepository: TodoRepository

    @Test
    @Rollback(false) // 실제 DB에 저장해서 확인하고 싶다면 false, 테스트용이라면 true
    fun `Todo 전체 플로우 테스트 - 생성, 조회, 상세조회, 소프트딜리트`() {
        println("=== Todo 전체 플로우 테스트 시작 ===")

        // 1. 초기 상태 확인
        println("\n1. 초기 Todo 개수 확인")
        val initialTodos = todoService.getAllTodos()
        println("초기 Todo 개수: ${initialTodos.size}")

        // 2. Todo 생성 테스트
        println("\n2. Todo 생성 테스트")
        val createdTodo = todoService.createTodo(
            title = "테스트 할일",
            content = "이것은 테스트용 할일입니다.",
            dueDate = LocalDateTime.now().plusDays(7)
        )

        println("생성된 Todo ID: ${createdTodo.id}")
        println("생성된 Todo 제목: ${createdTodo.title}")
        println("생성된 Todo 내용: ${createdTodo.content}")
        println("생성된 Todo 마감일: ${createdTodo.dueDate}")
        println("생성된 Todo 완료여부: ${createdTodo.isCompleted}")
        println("생성된 Todo 생성일: ${createdTodo.createdAt}")

        // 생성 검증
        assertThat(createdTodo.id).isNotNull()
        assertThat(createdTodo.title).isEqualTo("테스트 할일")
        assertThat(createdTodo.content).isEqualTo("이것은 테스트용 할일입니다.")
        assertThat(createdTodo.isCompleted).isFalse()

        // 3. 두 번째 Todo 생성
        println("\n3. 두 번째 Todo 생성")
        val secondTodo = todoService.createTodo(
            title = "두 번째 할일",
            content = "두 번째 테스트용 할일입니다.",
            dueDate = null
        )

        println("두 번째 Todo ID: ${secondTodo.id}")
        println("두 번째 Todo 제목: ${secondTodo.title}")

        // 4. 전체 Todo 목록 조회 테스트
        println("\n4. 전체 Todo 목록 조회 테스트")
        val allTodos = todoService.getAllTodos()
        println("현재 전체 Todo 개수: ${allTodos.size}")

        allTodos.forEachIndexed { index, todo ->
            println("Todo ${index + 1}: ID=${todo.id}, 제목='${todo.title}', 완료=${todo.isCompleted}")
        }

        // 목록 조회 검증
        assertThat(allTodos.size).isEqualTo(initialTodos.size + 2)
        assertThat(allTodos.any { it.id == createdTodo.id }).isTrue()
        assertThat(allTodos.any { it.id == secondTodo.id }).isTrue()

        // 5. 특정 Todo 상세 조회 테스트
        println("\n5. 특정 Todo 상세 조회 테스트")
        val detailTodo = todoService.getTodoById(createdTodo.id!!)

        println("상세 조회 결과:")
        println("  - ID: ${detailTodo.id}")
        println("  - 제목: ${detailTodo.title}")
        println("  - 내용: ${detailTodo.content}")
        println("  - 마감일: ${detailTodo.dueDate}")
        println("  - 완료여부: ${detailTodo.isCompleted}")
        println("  - 생성일: ${detailTodo.createdAt}")

        // 상세 조회 검증
        assertThat(detailTodo.id).isEqualTo(createdTodo.id)
        assertThat(detailTodo.title).isEqualTo("테스트 할일")
        assertThat(detailTodo.content).isEqualTo("이것은 테스트용 할일입니다.")

        // 6. Repository를 통한 직접 확인 (소프트 딜리트 전)
        println("\n6. Repository를 통한 직접 확인 (삭제 전)")
        val existsBeforeDelete = todoRepository.existsByIdAndDeletedAtIsNull(createdTodo.id!!)
        println("삭제 전 존재 여부: $existsBeforeDelete")
        assertThat(existsBeforeDelete).isTrue()

        // 7. Todo 소프트 딜리트 테스트
        println("\n7. Todo 소프트 딜리트 테스트")
        println("삭제할 Todo ID: ${createdTodo.id}")

        todoService.deleteTodo(createdTodo.id!!)
        println("소프트 딜리트 실행 완료")

        // 8. 소프트 딜리트 후 조회 테스트
        println("\n8. 소프트 딜리트 후 조회 테스트")

        // 전체 목록에서 삭제된 Todo가 제외되는지 확인
        val todosAfterDelete = todoService.getAllTodos()
        println("삭제 후 전체 Todo 개수: ${todosAfterDelete.size}")

        val deletedTodoExists = todosAfterDelete.any { it.id == createdTodo.id }
        println("삭제된 Todo가 목록에 있는지: $deletedTodoExists")
        assertThat(deletedTodoExists).isFalse()
        assertThat(todosAfterDelete.size).isEqualTo(initialTodos.size + 1) // 두 번째 Todo는 여전히 존재

        // 9. Repository를 통한 직접 확인 (소프트 딜리트 후)
        println("\n9. Repository를 통한 직접 확인 (삭제 후)")
        val existsAfterDelete = todoRepository.existsByIdAndDeletedAtIsNull(createdTodo.id!!)
        println("삭제 후 존재 여부: $existsAfterDelete")
        assertThat(existsAfterDelete).isFalse()

        // 10. 삭제된 Todo 상세 조회 시 예외 발생 확인
        println("\n10. 삭제된 Todo 상세 조회 시 예외 발생 확인")
        try {
            todoService.getTodoById(createdTodo.id!!)
            fail("삭제된 Todo 조회 시 예외가 발생해야 합니다.")
        } catch (e: IllegalArgumentException) {
            println("예상된 예외 발생: ${e.message}")
            assertThat(e.message).contains("Todo not found")
        }

        // 11. 존재하지 않는 Todo 삭제 시 예외 발생 확인
        println("\n11. 존재하지 않는 Todo 삭제 시 예외 발생 확인")
        try {
            todoService.deleteTodo(99999L)
            fail("존재하지 않는 Todo 삭제 시 예외가 발생해야 합니다.")
        } catch (e: IllegalArgumentException) {
            println("예상된 예외 발생: ${e.message}")
            assertThat(e.message).contains("Todo not found")
        }

        // 12. 최종 상태 확인
        println("\n12. 최종 상태 확인")
        val finalTodos = todoService.getAllTodos()
        println("최종 Todo 개수: ${finalTodos.size}")
        println("남은 Todo들:")
        finalTodos.forEach { todo ->
            println("  - ID: ${todo.id}, 제목: '${todo.title}'")
        }

        // 두 번째 Todo는 여전히 존재하는지 확인
        val secondTodoStillExists = finalTodos.any { it.id == secondTodo.id }
        println("두 번째 Todo 존재 여부: $secondTodoStillExists")
        assertThat(secondTodoStillExists).isTrue()

        println("\n=== Todo 전체 플로우 테스트 완료 ===")
        println("✅ 모든 테스트가 성공적으로 완료되었습니다!")
    }

    @Test
    fun contextLoads() {
        // 기본 컨텍스트 로드 테스트
        println("Spring Context 로드 성공!")
    }

    @Test
    fun `대량 데이터 테스트`() {
        println("\n=== 대량 데이터 테스트 시작 ===")

        // 10개의 Todo 생성
        val createdTodos = mutableListOf<Long>()

        repeat(10) { i ->
            val todo = todoService.createTodo(
                title = "대량 테스트 할일 ${i + 1}",
                content = "대량 테스트용 내용 ${i + 1}",
                dueDate = LocalDateTime.now().plusDays(i.toLong() + 1)
            )
            createdTodos.add(todo.id!!)
            println("생성된 Todo ${i + 1}: ID=${todo.id}")
        }

        // 전체 조회
        val allTodos = todoService.getAllTodos()
        println("총 Todo 개수: ${allTodos.size}")

        // 절반 삭제
        val todosToDelete = createdTodos.take(5)
        println("삭제할 Todo IDs: $todosToDelete")

        todosToDelete.forEach { id ->
            todoService.deleteTodo(id)
            println("Todo $id 삭제 완료")
        }

        // 삭제 후 조회
        val remainingTodos = todoService.getAllTodos()
        println("삭제 후 남은 Todo 개수: ${remainingTodos.size}")

        // 남은 Todo들 확인
        val remainingIds = remainingTodos.map { it.id }
        val shouldRemain = createdTodos.drop(5)

        shouldRemain.forEach { id ->
            assertThat(remainingIds).contains(id)
            println("Todo $id 가 남아있음을 확인")
        }

        todosToDelete.forEach { id ->
            assertThat(remainingIds).doesNotContain(id)
            println("Todo $id 가 삭제되었음을 확인")
        }

        println("=== 대량 데이터 테스트 완료 ===")
    }
}


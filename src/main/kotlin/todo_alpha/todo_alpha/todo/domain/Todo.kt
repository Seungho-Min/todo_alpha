package todo_alpha.todo_alpha.todo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(
    name = "todo"
)
@SQLDelete(sql = "UPDATE todo SET deleted_at = NOW() WHERE id = ?")
class Todo (
    title: String,
    content: String,
    dueDate: LocalDateTime? = null,
    isCompleted: Boolean,
    createdAt: LocalDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    protected set

    @Column(name = "title", nullable = false)
    var title: String = title
    protected set

    @Column(name = "content", nullable = false)
    var content: String = content
    protected set

    @Column(name = "due_date")
    var dueDate: LocalDateTime? = dueDate
    protected set

    @Column(name = "is_completed")
    var isCompleted: Boolean = isCompleted
    protected set

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = createdAt
    protected set


    // 소프트 딜리트용
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
    protected set
}

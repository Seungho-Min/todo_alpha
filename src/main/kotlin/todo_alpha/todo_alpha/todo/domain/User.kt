package todo_alpha.todo_alpha.todo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import todo_alpha.todo_alpha.feignTest.domain.base.UlidPrimaryKeyEntity

@Entity
@Table(name = "users")
class User(
    name: String,
    email: String,
) : UlidPrimaryKeyEntity() {

    @Column(name = "name", nullable = false)
    var name: String = name
    protected set

    @Column(name = "email", nullable = false, unique = true)
    var email: String = email
    protected set

    fun updateProfile(name: String, email: String) {
        this.name = name
        this.email = email
    }

}
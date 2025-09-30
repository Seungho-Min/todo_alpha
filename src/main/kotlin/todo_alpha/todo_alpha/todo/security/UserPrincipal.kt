package todo_alpha.todo_alpha.todo.security

import org.springframework.security.core.GrantedAuthority

data class UserPrincipal(
    val id: String, // 사용자의 고유 ID (ULID)
    val email: String,
    val authorities: Collection<GrantedAuthority> // 사용자의 권한 목록
)

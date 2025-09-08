package cloud.luigi99.blog.common.security

import java.util.*

data class SecurityContext(
    val userId: UUID?,
    val username: String?,
    val authorities: Set<String> = emptySet()
) {
    fun isAuthenticated(): Boolean = userId != null && username != null
    
    fun hasAuthority(authority: String): Boolean = authorities.contains(authority)
    
    companion object {
        fun anonymous(): SecurityContext = SecurityContext(null, null)
        
        fun authenticated(userId: UUID, username: String, authorities: Set<String> = emptySet()): SecurityContext {
            return SecurityContext(userId, username, authorities)
        }
    }
}
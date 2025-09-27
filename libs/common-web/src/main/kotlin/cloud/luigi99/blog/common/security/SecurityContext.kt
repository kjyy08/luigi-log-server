package cloud.luigi99.blog.common.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object SecurityContext {

    fun getCurrentUserId(): Long? {
        return getAuthentication()?.let { auth ->
            when (val principal = auth.principal) {
                is UserDetails -> principal.username.toLongOrNull()
                is String -> principal.toLongOrNull()
                else -> null
            }
        }
    }

    fun getCurrentUserAuthorities(): List<String> {
        return getAuthentication()?.authorities?.map { it.authority } ?: emptyList()
    }

    fun getCurrentUsername(): String? {
        return getAuthentication()?.let { auth ->
            when (val principal = auth.principal) {
                is UserDetails -> principal.username
                is String -> principal
                else -> null
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return getAuthentication()?.isAuthenticated == true
    }

    fun hasAuthority(authority: String): Boolean {
        return getCurrentUserAuthorities().contains(authority)
    }

    fun hasAnyAuthority(vararg authorities: String): Boolean {
        val currentAuthorities = getCurrentUserAuthorities()
        return authorities.any { currentAuthorities.contains(it) }
    }

    fun clear() {
        SecurityContextHolder.clearContext()
    }

    private fun getAuthentication(): Authentication? {
        return SecurityContextHolder.getContext()?.authentication
    }
}
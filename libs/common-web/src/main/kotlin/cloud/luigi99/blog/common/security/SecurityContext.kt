package cloud.luigi99.blog.common.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.authentication.AnonymousAuthenticationToken

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
        return getAuthentication()?.let { auth ->
            // Anonymous 사용자는 인증되지 않은 것으로 처리
            if (auth is AnonymousAuthenticationToken) {
                return false
            }

            // 기본 인증 상태 확인
            if (!auth.isAuthenticated) {
                return false
            }

            // Principal이 실제 사용자 정보인지 확인 (다른 메서드들과 일관성 유지)
            when (val principal = auth.principal) {
                is UserDetails -> !principal.username.isNullOrBlank()
                is String -> principal.isNotBlank()
                else -> false
            }
        } ?: false
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
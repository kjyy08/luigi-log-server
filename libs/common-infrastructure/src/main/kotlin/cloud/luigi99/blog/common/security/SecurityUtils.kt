package cloud.luigi99.blog.common.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object SecurityUtils {
    fun getCurrentUsername(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return when {
            authentication == null -> null
            authentication.principal is UserDetails -> {
                (authentication.principal as UserDetails).username
            }
            authentication.principal is String -> {
                authentication.principal as String
            }
            else -> null
        }
    }

    fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null && authentication.isAuthenticated
    }
}
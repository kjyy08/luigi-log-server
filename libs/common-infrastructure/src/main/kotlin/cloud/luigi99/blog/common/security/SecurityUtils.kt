package cloud.luigi99.blog.common.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

/**
 * 보안 관련 공통 유틸리티 클래스
 *
 * Spring Security Context를 활용하여 인증/인가 관련 유틸리티 메서드를 제공합니다.
 * - 현재 인증된 사용자 정보 조회
 * - 권한 체크 유틸리티
 * - 보안 컨텍스트 관리
 */
object SecurityUtils {

    /**
     * 현재 인증된 사용자의 Authentication 객체를 반환합니다.
     *
     * @return 인증 객체, 인증되지 않은 경우 null
     */
    fun getCurrentAuthentication(): Authentication? {
        return SecurityContextHolder.getContext().authentication
    }

    /**
     * 현재 인증된 사용자의 사용자명을 반환합니다.
     *
     * @return 사용자명, 인증되지 않은 경우 null
     */
    fun getCurrentUsername(): String? {
        val authentication = getCurrentAuthentication()
        return when {
            authentication == null -> null
            authentication.principal is UserDetails -> {
                (authentication.principal as UserDetails).username
            }

            authentication.principal is String -> {
                authentication.principal as String
            }

            else -> authentication.name
        }
    }

    /**
     * 현재 인증된 사용자의 UserDetails를 반환합니다.
     *
     * @return UserDetails 객체, 인증되지 않았거나 UserDetails가 아닌 경우 null
     */
    fun getCurrentUserDetails(): UserDetails? {
        val authentication = getCurrentAuthentication()
        return if (authentication?.principal is UserDetails) {
            authentication.principal as UserDetails
        } else {
            null
        }
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증 여부
     */
    fun isAuthenticated(): Boolean {
        val authentication = getCurrentAuthentication()
        return authentication != null && authentication.isAuthenticated && authentication.name != "anonymousUser"
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인합니다.
     *
     * @param authority 확인할 권한명
     * @return 권한 보유 여부
     */
    fun hasAuthority(authority: String): Boolean {
        val authentication = getCurrentAuthentication()
        return authentication?.authorities?.any { it.authority == authority } ?: false
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인합니다.
     * 역할명에 'ROLE_' 접두사가 없으면 자동으로 추가합니다.
     *
     * @param role 확인할 역할명 (ROLE_ 접두사 포함 또는 제외)
     * @return 역할 보유 여부
     */
    fun hasRole(role: String): Boolean {
        val roleWithPrefix = if (role.startsWith("ROLE_")) role else "ROLE_$role"
        return hasAuthority(roleWithPrefix)
    }

    /**
     * 현재 사용자가 여러 권한 중 하나라도 가지고 있는지 확인합니다.
     *
     * @param authorities 확인할 권한명 목록
     * @return 권한 보유 여부 (하나라도 있으면 true)
     */
    fun hasAnyAuthority(vararg authorities: String): Boolean {
        val authentication = getCurrentAuthentication() ?: return false

        val userAuthorities = authentication.authorities?.map { it.authority }?.toSet() ?: emptySet()
        return authorities.any { it in userAuthorities }
    }

    /**
     * 현재 사용자가 여러 역할 중 하나라도 가지고 있는지 확인합니다.
     *
     * @param roles 확인할 역할명 목록 (ROLE_ 접두사 포함 또는 제외)
     * @return 역할 보유 여부 (하나라도 있으면 true)
     */
    fun hasAnyRole(vararg roles: String): Boolean {
        val rolesWithPrefix = roles.map { role ->
            if (role.startsWith("ROLE_")) role else "ROLE_$role"
        }.toTypedArray()
        return hasAnyAuthority(*rolesWithPrefix)
    }

    /**
     * 현재 사용자의 모든 권한 목록을 반환합니다.
     *
     * @return 권한명 목록
     */
    fun getCurrentAuthorities(): List<String> {
        val authentication = getCurrentAuthentication()
        return authentication?.authorities?.map { it.authority } ?: emptyList()
    }

    /**
     * 현재 사용자의 모든 역할 목록을 반환합니다.
     * ROLE_ 접두사가 있는 권한만 추출하여 접두사를 제거한 형태로 반환합니다.
     *
     * @return 역할명 목록 (ROLE_ 접두사 제거됨)
     */
    fun getCurrentRoles(): List<String> {
        return getCurrentAuthorities()
            .filter { it.startsWith("ROLE_") }
            .map { it.removePrefix("ROLE_") }
    }

    /**
     * 현재 사용자가 특정 사용자인지 확인합니다.
     *
     * @param username 확인할 사용자명
     * @return 사용자 일치 여부
     */
    fun isCurrentUser(username: String): Boolean {
        return getCurrentUsername() == username
    }

    /**
     * 현재 사용자가 익명 사용자인지 확인합니다.
     *
     * @return 익명 사용자 여부
     */
    fun isAnonymous(): Boolean {
        val authentication = getCurrentAuthentication()
        return authentication == null || authentication.name == "anonymousUser" || !authentication.isAuthenticated
    }

    /**
     * 보안 컨텍스트를 초기화합니다.
     * 로그아웃 처리나 테스트 환경에서 사용됩니다.
     */
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }
}
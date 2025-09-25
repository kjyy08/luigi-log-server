package cloud.luigi99.blog.common.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

class SecurityUtilsTest : BehaviorSpec({

    given("인증 정보가 없는 상태") {
        `when`("getCurrentAuthentication을 호출할 때") {
            then("null을 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = SecurityUtils.getCurrentAuthentication()
                authentication shouldBe null
            }
        }

        `when`("getCurrentUsername을 호출할 때") {
            then("null을 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val username = SecurityUtils.getCurrentUsername()
                username shouldBe null
            }
        }

        `when`("isAuthenticated를 호출할 때") {
            then("false를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val isAuthenticated = SecurityUtils.isAuthenticated()
                isAuthenticated shouldBe false
            }
        }

        `when`("isAnonymous를 호출할 때") {
            then("true를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val isAnonymous = SecurityUtils.isAnonymous()
                isAnonymous shouldBe true
            }
        }
    }

    given("UserDetails 기반 인증 정보") {
        val username = "testuser"

        `when`("SecurityContext에 설정한 후") {
            then("현재 Authentication을 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER", "READ_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val result = SecurityUtils.getCurrentAuthentication()
                result shouldBe authentication

                SecurityContextHolder.clearContext()
            }

            then("현재 사용자명을 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER", "READ_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val result = SecurityUtils.getCurrentUsername()
                result shouldBe username

                SecurityContextHolder.clearContext()
            }

            then("UserDetails 객체를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER", "READ_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val userDetails = SecurityUtils.getCurrentUserDetails()
                userDetails shouldNotBe null
                userDetails!!.username shouldBe username

                SecurityContextHolder.clearContext()
            }

            then("인증되었다고 판단해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER", "READ_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val result = SecurityUtils.isAuthenticated()
                result shouldBe true

                SecurityContextHolder.clearContext()
            }

            then("익명이 아니라고 판단해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER", "READ_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val result = SecurityUtils.isAnonymous()
                result shouldBe false

                SecurityContextHolder.clearContext()
            }
        }
    }

    given("권한 체크 기능") {
        `when`("특정 권한을 확인할 때") {
            then("보유한 권한은 true를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasRead = SecurityUtils.hasAuthority("READ_PRIVILEGE")
                val hasWrite = SecurityUtils.hasAuthority("WRITE_PRIVILEGE")

                hasRead shouldBe true
                hasWrite shouldBe true

                SecurityContextHolder.clearContext()
            }

            then("없는 권한은 false를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasDelete = SecurityUtils.hasAuthority("DELETE_PRIVILEGE")
                hasDelete shouldBe false

                SecurityContextHolder.clearContext()
            }
        }

        `when`("역할을 확인할 때") {
            then("보유한 역할은 true를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasAdmin = SecurityUtils.hasRole("ADMIN")
                val hasRoleAdmin = SecurityUtils.hasRole("ROLE_ADMIN")

                hasAdmin shouldBe true
                hasRoleAdmin shouldBe true

                SecurityContextHolder.clearContext()
            }

            then("없는 역할은 false를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasUser = SecurityUtils.hasRole("USER")
                hasUser shouldBe false

                SecurityContextHolder.clearContext()
            }
        }

        `when`("여러 권한 중 하나를 확인할 때") {
            then("하나라도 있으면 true를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasAny = SecurityUtils.hasAnyAuthority("DELETE_PRIVILEGE", "READ_PRIVILEGE")
                hasAny shouldBe true

                SecurityContextHolder.clearContext()
            }

            then("모두 없으면 false를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val hasAny = SecurityUtils.hasAnyAuthority("DELETE_PRIVILEGE", "SUPER_ADMIN")
                hasAny shouldBe false

                SecurityContextHolder.clearContext()
            }
        }

        `when`("현재 권한 목록을 조회할 때") {
            then("모든 권한을 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val authorities = SecurityUtils.getCurrentAuthorities()

                authorities shouldHaveSize 3
                authorities shouldContain "ROLE_ADMIN"
                authorities shouldContain "READ_PRIVILEGE"
                authorities shouldContain "WRITE_PRIVILEGE"

                SecurityContextHolder.clearContext()
            }
        }

        `when`("현재 역할 목록을 조회할 때") {
            then("ROLE_ 접두사가 제거된 역할만 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE")
                SecurityContextHolder.getContext().authentication = authentication

                val roles = SecurityUtils.getCurrentRoles()

                roles shouldHaveSize 1
                roles shouldContain "ADMIN"

                SecurityContextHolder.clearContext()
            }
        }
    }

    given("익명 사용자") {
        `when`("인증 상태를 확인할 때") {
            then("인증되지 않은 것으로 판단해야 한다") {
                SecurityContextHolder.clearContext()
                val authorities = listOf(SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                val authentication = UsernamePasswordAuthenticationToken("anonymousUser", null, authorities)
                SecurityContextHolder.getContext().authentication = authentication

                val isAuthenticated = SecurityUtils.isAuthenticated()
                isAuthenticated shouldBe false

                SecurityContextHolder.clearContext()
            }

            then("익명 사용자로 판단해야 한다") {
                SecurityContextHolder.clearContext()
                val authorities = listOf(SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                val authentication = UsernamePasswordAuthenticationToken("anonymousUser", null, authorities)
                SecurityContextHolder.getContext().authentication = authentication

                val isAnonymous = SecurityUtils.isAnonymous()
                isAnonymous shouldBe true

                SecurityContextHolder.clearContext()
            }
        }
    }

    given("특정 사용자 확인") {
        val username = "testuser"

        `when`("현재 사용자를 확인할 때") {
            then("동일한 사용자는 true를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER")
                SecurityContextHolder.getContext().authentication = authentication

                val isCurrentUser = SecurityUtils.isCurrentUser(username)
                isCurrentUser shouldBe true

                SecurityContextHolder.clearContext()
            }

            then("다른 사용자는 false를 반환해야 한다") {
                SecurityContextHolder.clearContext()
                val authentication = createAuthenticationWithUserDetails(username, "ROLE_USER")
                SecurityContextHolder.getContext().authentication = authentication

                val isCurrentUser = SecurityUtils.isCurrentUser("otheruser")
                isCurrentUser shouldBe false

                SecurityContextHolder.clearContext()
            }
        }
    }

    given("보안 컨텍스트 관리") {
        `when`("보안 컨텍스트를 초기화할 때") {
            then("인증 정보가 제거되어야 한다") {
                val authentication = createAuthenticationWithUserDetails("testuser", "ROLE_USER")
                SecurityContextHolder.getContext().authentication = authentication

                SecurityUtils.clearSecurityContext()

                SecurityContextHolder.getContext().authentication shouldBe null
                SecurityUtils.getCurrentAuthentication() shouldBe null
            }
        }
    }

    given("인증되지 않은 상태에서 권한 확인") {
        `when`("권한 관련 메서드를 호출할 때") {
            then("모두 false를 반환해야 한다") {
                SecurityContextHolder.clearContext()

                val hasAuthority = SecurityUtils.hasAuthority("ROLE_USER")
                val hasRole = SecurityUtils.hasRole("USER")
                val hasAnyAuthority = SecurityUtils.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                val hasAnyRole = SecurityUtils.hasAnyRole("USER", "ADMIN")

                hasAuthority shouldBe false
                hasRole shouldBe false
                hasAnyAuthority shouldBe false
                hasAnyRole shouldBe false
            }
        }
    }
}) {
    companion object {
        private fun createAuthenticationWithUserDetails(username: String, vararg authorities: String): UsernamePasswordAuthenticationToken {
            val grantedAuthorities: List<GrantedAuthority> = authorities.map { SimpleGrantedAuthority(it) }
            val userDetails = User(username, "password", grantedAuthorities)

            return UsernamePasswordAuthenticationToken(userDetails, "password", grantedAuthorities)
        }
    }
}
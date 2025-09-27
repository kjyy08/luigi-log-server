package cloud.luigi99.blog.common.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

class SecurityContextTest : BehaviorSpec({

    beforeEach {
        clearAllMocks()
        SecurityContextHolder.clearContext()
    }

    afterEach {
        clearAllMocks()
        SecurityContextHolder.clearContext()
    }

    given("SecurityContext 인증된 사용자") {
        `when`("UserDetails 타입의 사용자가 인증되었을 때") {
            then("사용자 정보를 올바르게 추출해야 한다") {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"), SimpleGrantedAuthority("ROLE_ADMIN"))
                val userDetails = User("123", "password", authorities)
                val authentication = TestingAuthenticationToken(userDetails, "password", authorities)
                authentication.isAuthenticated = true

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)

                SecurityContext.isAuthenticated() shouldBe true
                SecurityContext.getCurrentUsername() shouldBe "123"
                SecurityContext.getCurrentUserId() shouldBe 123L

                val authoritiesList = SecurityContext.getCurrentUserAuthorities()
                authoritiesList.size shouldBe 2
                authoritiesList shouldContain "ROLE_USER"
                authoritiesList shouldContain "ROLE_ADMIN"

                SecurityContext.hasAuthority("ROLE_USER") shouldBe true
                SecurityContext.hasAuthority("ROLE_ADMIN") shouldBe true
                SecurityContext.hasAuthority("ROLE_GUEST") shouldBe false
            }
        }

        `when`("String 타입의 principal이 인증되었을 때") {
            then("사용자 정보를 올바르게 추출해야 한다") {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = TestingAuthenticationToken("456", "password", authorities)
                authentication.isAuthenticated = true

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)

                SecurityContext.getCurrentUserId() shouldBe 456L
                SecurityContext.getCurrentUsername() shouldBe "456"
                val authoritiesList = SecurityContext.getCurrentUserAuthorities()
                authoritiesList.size shouldBe 1
                authoritiesList shouldContain "ROLE_USER"
                SecurityContext.isAuthenticated() shouldBe true
            }
        }

        `when`("숫자가 아닌 문자열 principal이 인증되었을 때") {
            then("사용자 ID는 null이고 사용자명은 추출되어야 한다") {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = TestingAuthenticationToken("username", "password", authorities)
                authentication.isAuthenticated = true

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)

                SecurityContext.getCurrentUserId() shouldBe null
                SecurityContext.getCurrentUsername() shouldBe "username"
                val authoritiesList = SecurityContext.getCurrentUserAuthorities()
                authoritiesList.size shouldBe 1
                authoritiesList shouldContain "ROLE_USER"
                SecurityContext.isAuthenticated() shouldBe true
            }
        }
    }

    given("SecurityContext 권한 확인") {
        `when`("여러 권한을 가진 사용자가 인증되었을 때") {
            then("권한 확인이 올바르게 동작해야 한다") {
                val authorities = listOf(
                    SimpleGrantedAuthority("ROLE_USER"),
                    SimpleGrantedAuthority("ROLE_ADMIN"),
                    SimpleGrantedAuthority("ROLE_MODERATOR")
                )
                val authentication = TestingAuthenticationToken("123", "password", authorities)
                authentication.isAuthenticated = true

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)

                SecurityContext.hasAuthority("ROLE_USER") shouldBe true
                SecurityContext.hasAuthority("ROLE_ADMIN") shouldBe true
                SecurityContext.hasAuthority("ROLE_MODERATOR") shouldBe true
                SecurityContext.hasAuthority("ROLE_GUEST") shouldBe false

                SecurityContext.hasAnyAuthority("ROLE_USER") shouldBe true
                SecurityContext.hasAnyAuthority("ROLE_GUEST", "ROLE_USER") shouldBe true
                SecurityContext.hasAnyAuthority("ROLE_GUEST", "ROLE_VISITOR") shouldBe false
                SecurityContext.hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR") shouldBe true
            }
        }
    }

    given("SecurityContext 인증되지 않은 사용자") {
        `when`("인증 정보가 없을 때") {
            then("모든 정보가 null이거나 기본값이어야 한다") {
                SecurityContext.getCurrentUserId() shouldBe null
                SecurityContext.getCurrentUsername() shouldBe null
                SecurityContext.getCurrentUserAuthorities() shouldBe emptyList()
                SecurityContext.isAuthenticated() shouldBe false
                SecurityContext.hasAuthority("ROLE_USER") shouldBe false
                SecurityContext.hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") shouldBe false
            }
        }

        `when`("인증되지 않은 Authentication 객체가 있을 때") {
            val authentication = TestingAuthenticationToken("123", "password")
            authentication.isAuthenticated = false

            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)

            then("인증되지 않은 것으로 판단되어야 한다") {
                SecurityContext.isAuthenticated() shouldBe false
            }
        }
    }

    given("SecurityContext 컨텍스트 정리") {
        `when`("인증된 사용자가 있고 컨텍스트를 정리할 때") {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
            val authentication = TestingAuthenticationToken("123", "password", authorities)
            authentication.isAuthenticated = true

            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)

            // 정리 전 상태 확인
            SecurityContext.isAuthenticated() shouldBe true

            SecurityContext.clear()

            then("모든 인증 정보가 정리되어야 한다") {
                SecurityContext.getCurrentUserId() shouldBe null
                SecurityContext.getCurrentUsername() shouldBe null
                SecurityContext.getCurrentUserAuthorities() shouldBe emptyList()
                SecurityContext.isAuthenticated() shouldBe false
            }
        }
    }

    given("SecurityContext 예외적인 상황") {
        `when`("알 수 없는 타입의 principal이 설정되었을 때") {
            then("안전하게 null을 반환해야 한다") {
                val customPrincipal = mockk<Any>()
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = TestingAuthenticationToken(customPrincipal, "password", authorities)
                authentication.isAuthenticated = true

                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)

                SecurityContext.getCurrentUserId() shouldBe null
                SecurityContext.getCurrentUsername() shouldBe null
                val authoritiesList = SecurityContext.getCurrentUserAuthorities()
                authoritiesList.size shouldBe 1
                authoritiesList shouldContain "ROLE_USER"
                SecurityContext.isAuthenticated() shouldBe true
            }
        }
    }
})
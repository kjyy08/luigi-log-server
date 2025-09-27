package cloud.luigi99.blog.common.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class TokenProviderTest : BehaviorSpec({

    given("TokenProvider 인터페이스 테스트") {
        val mockTokenProvider = mockk<TokenProvider>()
        val userId = 1L
        val authorities = listOf("ROLE_USER", "ROLE_ADMIN")
        val accessToken = "mock.access.token"
        val refreshToken = "mock.refresh.token"

        `when`("액세스 토큰을 생성할 때") {
            every { mockTokenProvider.generateAccessToken(userId, authorities) } returns accessToken

            val result = mockTokenProvider.generateAccessToken(userId, authorities)

            then("토큰이 반환되어야 한다") {
                result shouldBe accessToken
                result shouldNotBe null
            }
        }

        `when`("리프레시 토큰을 생성할 때") {
            every { mockTokenProvider.generateRefreshToken(userId) } returns refreshToken

            val result = mockTokenProvider.generateRefreshToken(userId)

            then("토큰이 반환되어야 한다") {
                result shouldBe refreshToken
                result shouldNotBe null
            }
        }

        `when`("유효한 토큰을 검증할 때") {
            every { mockTokenProvider.validateToken(accessToken) } returns true

            val result = mockTokenProvider.validateToken(accessToken)

            then("true가 반환되어야 한다") {
                result shouldBe true
            }
        }

        `when`("유효하지 않은 토큰을 검증할 때") {
            val invalidToken = "invalid.token"
            every { mockTokenProvider.validateToken(invalidToken) } returns false

            val result = mockTokenProvider.validateToken(invalidToken)

            then("false가 반환되어야 한다") {
                result shouldBe false
            }
        }

        `when`("토큰에서 사용자 ID를 추출할 때") {
            every { mockTokenProvider.getUserIdFromToken(accessToken) } returns userId

            val result = mockTokenProvider.getUserIdFromToken(accessToken)

            then("올바른 사용자 ID가 반환되어야 한다") {
                result shouldBe userId
            }
        }

        `when`("토큰에서 권한을 추출할 때") {
            every { mockTokenProvider.getAuthoritiesFromToken(accessToken) } returns authorities

            val result = mockTokenProvider.getAuthoritiesFromToken(accessToken)

            then("올바른 권한 리스트가 반환되어야 한다") {
                result shouldBe authorities
            }
        }

        `when`("토큰의 만료 시간을 확인할 때") {
            val expirationTime = System.currentTimeMillis() + 86400000 // 24시간 후
            every { mockTokenProvider.getExpirationFromToken(accessToken) } returns expirationTime

            val result = mockTokenProvider.getExpirationFromToken(accessToken)

            then("만료 시간이 반환되어야 한다") {
                result shouldBe expirationTime
            }
        }

        `when`("토큰이 만료되지 않았을 때") {
            every { mockTokenProvider.isTokenExpired(accessToken) } returns false

            val result = mockTokenProvider.isTokenExpired(accessToken)

            then("false가 반환되어야 한다") {
                result shouldBe false
            }
        }

        `when`("토큰이 만료되었을 때") {
            val expiredToken = "expired.token"
            every { mockTokenProvider.isTokenExpired(expiredToken) } returns true

            val result = mockTokenProvider.isTokenExpired(expiredToken)

            then("true가 반환되어야 한다") {
                result shouldBe true
            }
        }
    }
})
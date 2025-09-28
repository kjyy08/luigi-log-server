package cloud.luigi99.blog.common.security

import cloud.luigi99.blog.common.security.TokenProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import java.util.*

class JwtTokenProviderTest : BehaviorSpec({

    given("JwtTokenProvider 토큰 생성") {
        val secretKey = "testSecretKeyForJwtTokenProviderTestingPurposes123456"
        val accessTokenValidity = 86400000L // 24시간
        val refreshTokenValidity = 2592000000L // 30일
        val jwtTokenProvider = JwtTokenProvider(secretKey, accessTokenValidity, refreshTokenValidity)

        `when`("액세스 토큰을 생성할 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER", "ROLE_ADMIN")
            val accessToken = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("유효한 토큰이 생성되어야 한다") {
                accessToken.shouldNotBeEmpty()
                jwtTokenProvider.validateToken(accessToken) shouldBe true
                jwtTokenProvider.getUserIdFromToken(accessToken) shouldBe userId
                jwtTokenProvider.getAuthoritiesFromToken(accessToken) shouldBe authorities
            }
        }

        `when`("리프레시 토큰을 생성할 때") {
            val userId = 2L
            val refreshToken = jwtTokenProvider.generateRefreshToken(userId)

            then("유효한 리프레시 토큰이 생성되어야 한다") {
                refreshToken.shouldNotBeEmpty()
                jwtTokenProvider.validateToken(refreshToken) shouldBe true
                jwtTokenProvider.getUserIdFromToken(refreshToken) shouldBe userId
            }
        }
    }

    given("JwtTokenProvider 토큰 검증") {
        val secretKey = "testSecretKeyForJwtTokenProviderTestingPurposes123456"
        val accessTokenValidity = 86400000L
        val refreshTokenValidity = 2592000000L
        val jwtTokenProvider = JwtTokenProvider(secretKey, accessTokenValidity, refreshTokenValidity)

        `when`("유효한 토큰이 주어질 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER")
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("토큰 검증이 성공해야 한다") {
                jwtTokenProvider.validateToken(token) shouldBe true
            }
        }

        `when`("잘못된 형식의 토큰이 주어질 때") {
            val invalidTokens = listOf(
                "invalid.token",
                "not.a.jwt.token",
                "",
                "eyJhbGciOiJIUzI1NiJ9.invalid",
                "bearer token"
            )

            then("모든 토큰 검증이 실패해야 한다") {
                invalidTokens.forEach { invalidToken ->
                    jwtTokenProvider.validateToken(invalidToken) shouldBe false
                }
            }
        }

        `when`("다른 비밀키로 생성된 토큰이 주어질 때") {
            val otherSecretKey = "differentSecretKeyForTestingPurposes987654321"
            val otherProvider = JwtTokenProvider(otherSecretKey, accessTokenValidity, refreshTokenValidity)
            val tokenFromOtherProvider = otherProvider.generateAccessToken(1L, listOf("ROLE_USER"))

            then("토큰 검증이 실패해야 한다") {
                // 다른 키로 서명된 토큰은 검증 실패해야 함
                val isValid = try {
                    jwtTokenProvider.validateToken(tokenFromOtherProvider)
                } catch (ex: Exception) {
                    false
                }
                isValid shouldBe false
            }
        }
    }

    given("JwtTokenProvider 토큰 정보 추출") {
        val secretKey = "testSecretKeyForJwtTokenProviderTestingPurposes123456"
        val accessTokenValidity = 86400000L
        val refreshTokenValidity = 2592000000L
        val jwtTokenProvider = JwtTokenProvider(secretKey, accessTokenValidity, refreshTokenValidity)

        `when`("토큰에서 사용자 ID를 추출할 때") {
            val userId = 12345L
            val authorities = listOf("ROLE_ADMIN")
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("올바른 사용자 ID가 추출되어야 한다") {
                jwtTokenProvider.getUserIdFromToken(token) shouldBe userId
            }
        }

        `when`("토큰에서 권한을 추출할 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR")
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("모든 권한이 올바르게 추출되어야 한다") {
                val extractedAuthorities = jwtTokenProvider.getAuthoritiesFromToken(token)
                extractedAuthorities.size shouldBe authorities.size
                authorities.forEach { authority ->
                    extractedAuthorities shouldContain authority
                }
            }
        }

        `when`("토큰의 만료 시간을 확인할 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER")
            val beforeGeneration = System.currentTimeMillis()
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)
            val afterGeneration = System.currentTimeMillis()

            then("적절한 만료 시간이 설정되어야 한다") {
                val expirationTime = jwtTokenProvider.getExpirationFromToken(token)
                expirationTime shouldNotBe null
                // 만료 시간이 생성 시간 + 유효기간 범위 내에 있는지 확인 (여유분 고려)
                val expectedMinExpiration = beforeGeneration + accessTokenValidity - 1000 // 1초 여유
                val expectedMaxExpiration = afterGeneration + accessTokenValidity + 1000 // 1초 여유
                (expirationTime >= expectedMinExpiration) shouldBe true
                (expirationTime <= expectedMaxExpiration) shouldBe true
            }
        }

        `when`("토큰이 만료되지 않았을 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER")
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("만료되지 않았다고 판단되어야 한다") {
                jwtTokenProvider.isTokenExpired(token) shouldBe false
            }
        }
    }

    given("JwtTokenProvider 만료된 토큰 처리") {
        val secretKey = "testSecretKeyForJwtTokenProviderTestingPurposes123456"
        val shortValidity = 1L // 1ms로 설정하여 즉시 만료
        val refreshTokenValidity = 2592000000L
        val jwtTokenProvider = JwtTokenProvider(secretKey, shortValidity, refreshTokenValidity)

        `when`("매우 짧은 유효기간으로 토큰을 생성하고 시간이 지날 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER")
            val token = jwtTokenProvider.generateAccessToken(userId, authorities)

            // 토큰이 만료되도록 잠시 대기
            Thread.sleep(10)

            then("토큰이 만료되었다고 판단되어야 한다") {
                // 만료된 토큰은 예외가 발생하거나 만료로 판단되어야 함
                val isExpired = try {
                    jwtTokenProvider.isTokenExpired(token)
                } catch (ex: Exception) {
                    // ExpiredJwtException이 발생하면 만료된 것으로 간주
                    true
                }
                isExpired shouldBe true
            }
        }
    }

    given("JwtTokenProvider 토큰 타입 검증") {
        val secretKey = "testSecretKeyForJwtTokenProviderTestingPurposes123456"
        val accessTokenValidity = 86400000L
        val refreshTokenValidity = 2592000000L
        val jwtTokenProvider = JwtTokenProvider(secretKey, accessTokenValidity, refreshTokenValidity)

        `when`("액세스 토큰을 생성하고 타입을 검증할 때") {
            val userId = 1L
            val authorities = listOf("ROLE_USER")
            val accessToken = jwtTokenProvider.generateAccessToken(userId, authorities)

            then("액세스 토큰 검증이 성공하고 타입이 ACCESS여야 한다") {
                jwtTokenProvider.validateAccessToken(accessToken) shouldBe true
                jwtTokenProvider.validateRefreshToken(accessToken) shouldBe false
                jwtTokenProvider.getTokenType(accessToken) shouldBe TokenType.ACCESS
            }
        }

        `when`("리프레시 토큰을 생성하고 타입을 검증할 때") {
            val userId = 2L
            val refreshToken = jwtTokenProvider.generateRefreshToken(userId)

            then("리프레시 토큰 검증이 성공하고 타입이 REFRESH여야 한다") {
                jwtTokenProvider.validateRefreshToken(refreshToken) shouldBe true
                jwtTokenProvider.validateAccessToken(refreshToken) shouldBe false
                jwtTokenProvider.getTokenType(refreshToken) shouldBe TokenType.REFRESH
            }
        }

        `when`("잘못된 토큰으로 타입 검증을 시도할 때") {
            val invalidTokens = listOf(
                "invalid.token",
                "",
                "eyJhbGciOiJIUzI1NiJ9.invalid",
                "malformed.jwt.token"
            )

            then("모든 타입 검증이 실패해야 한다") {
                invalidTokens.forEach { invalidToken ->
                    jwtTokenProvider.validateAccessToken(invalidToken) shouldBe false
                    jwtTokenProvider.validateRefreshToken(invalidToken) shouldBe false

                    // getTokenType은 예외를 던져야 함
                    try {
                        jwtTokenProvider.getTokenType(invalidToken)
                        throw AssertionError("예외가 발생해야 함")
                    } catch (ex: Exception) {
                        // 예외 발생이 정상
                    }
                }
            }
        }

        `when`("만료된 토큰으로 타입 검증을 시도할 때") {
            val shortValidity = 1L // 1ms로 설정하여 즉시 만료
            val expiredTokenProvider = JwtTokenProvider(secretKey, shortValidity, shortValidity)
            val expiredAccessToken = expiredTokenProvider.generateAccessToken(1L, listOf("ROLE_USER"))
            val expiredRefreshToken = expiredTokenProvider.generateRefreshToken(1L)

            // 토큰이 만료되도록 잠시 대기
            Thread.sleep(10)

            then("만료된 토큰의 타입 검증이 실패해야 한다") {
                jwtTokenProvider.validateAccessToken(expiredAccessToken) shouldBe false
                jwtTokenProvider.validateRefreshToken(expiredRefreshToken) shouldBe false

                // getTokenType은 만료된 토큰에 대해 예외를 던져야 함 (ExpiredJwtException)
                try {
                    jwtTokenProvider.getTokenType(expiredAccessToken)
                    throw AssertionError("만료된 토큰에 대해 예외가 발생해야 함")
                } catch (ex: Exception) {
                    // 예외 발생이 정상
                }

                try {
                    jwtTokenProvider.getTokenType(expiredRefreshToken)
                    throw AssertionError("만료된 토큰에 대해 예외가 발생해야 함")
                } catch (ex: Exception) {
                    // 예외 발생이 정상
                }
            }
        }

        `when`("다른 비밀키로 생성된 토큰으로 타입 검증을 시도할 때") {
            val otherSecretKey = "differentSecretKeyForTestingPurposes987654321"
            val otherProvider = JwtTokenProvider(otherSecretKey, accessTokenValidity, refreshTokenValidity)
            val accessTokenFromOther = otherProvider.generateAccessToken(1L, listOf("ROLE_USER"))
            val refreshTokenFromOther = otherProvider.generateRefreshToken(1L)

            then("다른 키로 서명된 토큰의 타입 검증이 실패해야 한다") {
                // 다른 키로 서명된 토큰은 서명 검증 단계에서 실패하므로 모든 검증이 false여야 함
                jwtTokenProvider.validateAccessToken(accessTokenFromOther) shouldBe false
                jwtTokenProvider.validateRefreshToken(refreshTokenFromOther) shouldBe false

                // getTokenType도 서명 검증에 실패하므로 예외를 던져야 함 (SecurityException 발생)
                try {
                    jwtTokenProvider.getTokenType(accessTokenFromOther)
                    throw AssertionError("서명 검증 실패로 예외가 발생해야 함")
                } catch (ex: Exception) {
                    // 예외 발생이 정상
                }

                try {
                    jwtTokenProvider.getTokenType(refreshTokenFromOther)
                    throw AssertionError("서명 검증 실패로 예외가 발생해야 함")
                } catch (ex: Exception) {
                    // 예외 발생이 정상
                }
            }
        }
    }

    given("TokenType enum 클래스") {
        `when`("유효한 문자열 값으로 TokenType을 찾을 때") {
            then("올바른 TokenType을 반환해야 한다") {
                TokenType.fromValue("access") shouldBe TokenType.ACCESS
                TokenType.fromValue("refresh") shouldBe TokenType.REFRESH
            }
        }

        `when`("유효하지 않은 문자열 값으로 TokenType을 찾을 때") {
            then("null을 반환해야 한다") {
                TokenType.fromValue("invalid") shouldBe null
                TokenType.fromValue("") shouldBe null
                TokenType.fromValue("bearer") shouldBe null
            }
        }

        `when`("fromValueOrThrow를 사용할 때") {
            then("유효한 값은 TokenType을 반환하고 유효하지 않은 값은 예외를 발생시켜야 한다") {
                TokenType.fromValueOrThrow("access") shouldBe TokenType.ACCESS
                TokenType.fromValueOrThrow("refresh") shouldBe TokenType.REFRESH

                try {
                    TokenType.fromValueOrThrow("invalid")
                    throw AssertionError("예외가 발생해야 함")
                } catch (ex: IllegalArgumentException) {
                    ex.message shouldBe "유효하지 않은 토큰 타입: invalid"
                }
            }
        }
    }
})
package cloud.luigi99.blog.user.domain.entity

import cloud.luigi99.blog.user.domain.vo.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime

class UserSessionTest : BehaviorSpec({

    val userId = UserId.generate()

    given("UserSession 생성") {
        `when`("유효한 정보로 생성하면") {
            val expiresAt = LocalDateTime.now().plusDays(30)
            val session = UserSession.create(
                userId = userId,
                refreshToken = "refresh_token_123",
                expiresAt = expiresAt,
                deviceInfo = "Chrome on Windows",
                ipAddress = "192.168.1.1"
            )

            then("UserSession이 생성된다") {
                session shouldNotBe null
                session.userId shouldBe userId
                session.refreshToken shouldBe "refresh_token_123"
                session.expiresAt shouldBe expiresAt
                session.deviceInfo shouldBe "Chrome on Windows"
                session.ipAddress shouldBe "192.168.1.1"
            }
        }

        `when`("deviceInfo와 ipAddress 없이 생성하면") {
            val expiresAt = LocalDateTime.now().plusDays(30)
            val session = UserSession.create(
                userId = userId,
                refreshToken = "refresh_token_123",
                expiresAt = expiresAt
            )

            then("UserSession이 생성된다") {
                session shouldNotBe null
                session.deviceInfo shouldBe null
                session.ipAddress shouldBe null
            }
        }

        `when`("빈 리프레시 토큰으로 생성하려고 하면") {
            val expiresAt = LocalDateTime.now().plusDays(30)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    UserSession.create(userId, "", expiresAt)
                }
                exception.message shouldContain "필수값"
            }
        }

        `when`("과거 시각을 만료 시각으로 생성하려고 하면") {
            val expiresAt = LocalDateTime.now().minusDays(1)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    UserSession.create(userId, "refresh_token", expiresAt)
                }
                exception.message shouldContain "현재 시각 이후"
            }
        }
    }

    given("UserSession 만료 확인") {
        `when`("만료 시각이 미래인 세션을 확인하면") {
            val expiresAt = LocalDateTime.now().plusDays(30)
            val session = UserSession.create(userId, "refresh_token", expiresAt)

            then("유효한 세션으로 판단된다") {
                session.isExpired() shouldBe false
                session.isValid() shouldBe true
            }
        }

        `when`("만료 시각이 과거인 세션을 확인하면") {
            val expiresAt = LocalDateTime.now().minusDays(1)
            // 생성 시점에는 미래였지만 이후 만료된 상황 시뮬레이션
            val session = UserSession(
                entityId = SessionId.generate(),
                userId = userId,
                refreshToken = "refresh_token",
                expiresAt = expiresAt
            )

            then("만료된 세션으로 판단된다") {
                session.isExpired() shouldBe true
                session.isValid() shouldBe false
            }
        }
    }

    given("UserSession 갱신") {
        val expiresAt = LocalDateTime.now().plusDays(30)
        val session = UserSession.create(userId, "old_token", expiresAt)

        `when`("세션을 갱신하면") {
            val newExpiresAt = LocalDateTime.now().plusDays(60)
            val renewedSession = session.renew("new_token", newExpiresAt)

            then("새로운 토큰과 만료 시각으로 갱신된다") {
                renewedSession.refreshToken shouldBe "new_token"
                renewedSession.expiresAt shouldBe newExpiresAt
                renewedSession.updatedAt shouldNotBe null
            }
        }

        `when`("빈 토큰으로 갱신하려고 하면") {
            val newExpiresAt = LocalDateTime.now().plusDays(60)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    session.renew("", newExpiresAt)
                }
                exception.message shouldContain "필수값"
            }
        }

        `when`("과거 시각으로 갱신하려고 하면") {
            val pastExpiresAt = LocalDateTime.now().minusDays(1)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    session.renew("new_token", pastExpiresAt)
                }
                exception.message shouldContain "현재 시각 이후"
            }
        }
    }

    given("UserSession 남은 시간 계산") {
        `when`("만료까지 시간이 남은 세션의 남은 시간을 계산하면") {
            val expiresAt = LocalDateTime.now().plusMinutes(30)
            val session = UserSession.create(userId, "refresh_token", expiresAt)

            then("양수 값이 반환된다") {
                session.remainingTimeInSeconds() shouldBeGreaterThan 0L
            }
        }

        `when`("만료된 세션의 남은 시간을 계산하면") {
            val expiresAt = LocalDateTime.now().minusDays(1)
            val session = UserSession(
                entityId = SessionId.generate(),
                userId = userId,
                refreshToken = "refresh_token",
                expiresAt = expiresAt
            )

            then("0이 반환된다") {
                session.remainingTimeInSeconds() shouldBe 0L
            }
        }
    }

    given("SessionId 생성 및 변환") {
        `when`("generate() 메서드를 호출하면") {
            val sessionId = SessionId.generate()

            then("새로운 SessionId가 생성된다") {
                sessionId shouldNotBe null
            }
        }

        `when`("유효한 UUID 문자열로 from() 메서드를 호출하면") {
            val sessionId1 = SessionId.generate()
            val uuidString = sessionId1.value.toString()
            val sessionId2 = SessionId.from(uuidString)

            then("SessionId가 생성된다") {
                sessionId2.value.toString() shouldBe uuidString
            }
        }

        `when`("잘못된 UUID 문자열로 from() 메서드를 호출하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    SessionId.from("invalid-uuid")
                }
                exception.message shouldContain "Invalid SessionId format"
            }
        }
    }
})

package cloud.luigi99.blog.user.domain.event

import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.UserId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class UserDomainEventsTest : BehaviorSpec({

    given("UserCreatedEvent") {
        val userId = UserId.generate()
        val email = Email.of("test@example.com")
        val createdAt = LocalDateTime.now()

        `when`("UserCreatedEvent를 생성하면") {
            val event = UserCreatedEvent(
                userId = userId,
                email = email,
                createdAt = createdAt
            )

            then("이벤트가 생성된다") {
                event shouldNotBe null
                event.userId shouldBe userId
                event.email shouldBe email
                event.createdAt shouldBe createdAt
                event.aggregateId shouldBe userId.value
                event.eventType shouldBe "UserCreated"
                event.aggregateType shouldBe "User"
                event.eventVersion shouldBe 1
            }
        }

        `when`("이벤트 타입을 확인하면") {
            val event = UserCreatedEvent(userId, email, createdAt)

            then("올바른 이벤트 타입이 반환된다") {
                event.isEventType("UserCreated") shouldBe true
                event.isEventType("UserLoggedIn") shouldBe false
            }
        }

        `when`("애그리게이트 소속을 확인하면") {
            val event = UserCreatedEvent(userId, email, createdAt)

            then("올바른 애그리게이트로 판단된다") {
                event.isFromAggregate(userId.value) shouldBe true
                event.isFromAggregate(UserId.generate().value) shouldBe false
            }
        }
    }

    given("UserLoggedInEvent") {
        val userId = UserId.generate()
        val email = Email.of("test@example.com")
        val loginAt = LocalDateTime.now()
        val ipAddress = "192.168.1.1"

        `when`("UserLoggedInEvent를 생성하면") {
            val event = UserLoggedInEvent(
                userId = userId,
                email = email,
                loginAt = loginAt,
                ipAddress = ipAddress
            )

            then("이벤트가 생성된다") {
                event shouldNotBe null
                event.userId shouldBe userId
                event.email shouldBe email
                event.loginAt shouldBe loginAt
                event.ipAddress shouldBe ipAddress
                event.aggregateId shouldBe userId.value
                event.eventType shouldBe "UserLoggedIn"
                event.aggregateType shouldBe "User"
                event.eventVersion shouldBe 1
            }
        }

        `when`("IP 주소 없이 생성하면") {
            val event = UserLoggedInEvent(
                userId = userId,
                email = email,
                loginAt = loginAt
            )

            then("이벤트가 생성된다") {
                event shouldNotBe null
                event.ipAddress shouldBe null
            }
        }
    }

    given("UserProfileUpdatedEvent") {
        val userId = UserId.generate()
        val email = Email.of("test@example.com")
        val updatedAt = LocalDateTime.now()

        `when`("UserProfileUpdatedEvent를 생성하면") {
            val event = UserProfileUpdatedEvent(
                userId = userId,
                email = email,
                updatedAt = updatedAt
            )

            then("이벤트가 생성된다") {
                event shouldNotBe null
                event.userId shouldBe userId
                event.email shouldBe email
                event.updatedAt shouldBe updatedAt
                event.aggregateId shouldBe userId.value
                event.eventType shouldBe "UserProfileUpdated"
                event.aggregateType shouldBe "User"
                event.eventVersion shouldBe 1
            }
        }
    }

    given("도메인 이벤트 시간 비교") {
        val userId = UserId.generate()
        val email = Email.of("test@example.com")
        val now = LocalDateTime.now()
        val event = UserCreatedEvent(userId, email, now)

        `when`("특정 시간 이후 발생 여부를 확인하면") {
            val pastTime = now.minusHours(1)
            val futureTime = now.plusHours(1)

            then("올바른 결과가 반환된다") {
                event.occurredAfter(pastTime) shouldBe true
                event.occurredAfter(futureTime) shouldBe false
            }
        }

        `when`("특정 시간 이전 발생 여부를 확인하면") {
            val pastTime = now.minusHours(1)
            val futureTime = now.plusHours(1)

            then("올바른 결과가 반환된다") {
                event.occurredBefore(futureTime) shouldBe true
                event.occurredBefore(pastTime) shouldBe false
            }
        }
    }
})

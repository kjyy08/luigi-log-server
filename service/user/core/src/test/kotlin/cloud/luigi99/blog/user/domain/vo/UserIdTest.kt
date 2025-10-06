package cloud.luigi99.blog.user.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import java.util.*

class UserIdTest : BehaviorSpec({

    given("UserId 생성") {
        `when`("generate() 메서드를 호출하면") {
            val userId = UserId.generate()

            then("새로운 UserId가 생성된다") {
                userId shouldNotBe null
                userId.value shouldNotBe null
            }
        }

        `when`("유효한 UUID 문자열로 from() 메서드를 호출하면") {
            val uuidString = UUID.randomUUID().toString()
            val userId = UserId.from(uuidString)

            then("UserId가 생성된다") {
                userId shouldNotBe null
                userId.value.toString() shouldBe uuidString
            }
        }

        `when`("잘못된 UUID 문자열로 from() 메서드를 호출하면") {
            val invalidUuidString = "invalid-uuid"

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    UserId.from(invalidUuidString)
                }
                exception.message shouldContain "Invalid UserId format"
            }
        }
    }

    given("UserId 동등성 비교") {
        val uuid = UUID.randomUUID()
        val userId1 = UserId(uuid)
        val userId2 = UserId(uuid)
        val userId3 = UserId(UUID.randomUUID())

        `when`("같은 UUID를 가진 두 UserId를 비교하면") {
            then("동등하다고 판단된다") {
                userId1 shouldBe userId2
                userId1.hashCode() shouldBe userId2.hashCode()
            }
        }

        `when`("다른 UUID를 가진 두 UserId를 비교하면") {
            then("동등하지 않다고 판단된다") {
                userId1 shouldNotBe userId3
            }
        }
    }

    given("UserId 문자열 변환") {
        val uuid = UUID.randomUUID()
        val userId = UserId(uuid)

        `when`("toString() 메서드를 호출하면") {
            val result = userId.toString()

            then("UUID 문자열이 반환된다") {
                result shouldBe uuid.toString()
            }
        }
    }
})

package cloud.luigi99.blog.content.guestbook.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class GuestbookIdTest :
    BehaviorSpec({

        Given("시스템이 새로운 방명록 항목을 생성해야 할 때") {
            When("새 식별자를 생성하면") {
                val guestbookId = GuestbookId.generate()

                Then("고유한 UUID 기반 식별자가 생성된다") {
                    guestbookId shouldNotBe null
                    guestbookId.value shouldNotBe null
                }
            }
        }

        Given("데이터베이스에 저장된 방명록 ID 문자열이 있을 때") {
            val uuidString = "123e4567-e89b-12d3-a456-426614174000"

            When("문자열로부터 GuestbookId를 복원하면") {
                val guestbookId = GuestbookId.from(uuidString)

                Then("동일한 UUID 값을 가진 식별자가 생성된다") {
                    guestbookId.value.toString() shouldBe uuidString
                }
            }
        }

        Given("잘못된 UUID 형식의 문자열이 주어졌을 때") {
            val invalidUuidString = "invalid-uuid"

            When("GuestbookId 생성을 시도하면") {
                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        GuestbookId.from(invalidUuidString)
                    }
                }
            }
        }

        Given("동일한 UUID 값을 가진 두 GuestbookId가 있을 때") {
            val uuid = UUID.randomUUID()
            val guestbookId1 = GuestbookId(uuid)
            val guestbookId2 = GuestbookId(uuid)

            Then("두 식별자는 논리적으로 동일한 것으로 취급한다") {
                guestbookId1 shouldBe guestbookId2
            }

            Then("해시코드도 동일하다") {
                guestbookId1.hashCode() shouldBe guestbookId2.hashCode()
            }
        }
    })

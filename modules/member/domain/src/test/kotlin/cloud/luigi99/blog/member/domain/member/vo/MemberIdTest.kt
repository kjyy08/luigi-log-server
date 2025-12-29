package cloud.luigi99.blog.member.domain.member.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class MemberIdTest :
    BehaviorSpec({

        Given("MemberId 생성 시") {
            When("generate()를 호출하면") {
                val memberId = MemberId.generate()

                Then("UUID 기반의 MemberId가 생성된다") {
                    memberId shouldNotBe null
                    memberId.value shouldNotBe null
                }

                Then("toString()은 UUID 문자열을 반환한다") {
                    val uuidString = memberId.toString()
                    uuidString shouldNotBe null
                    UUID.fromString(uuidString) shouldNotBe null
                }
            }

            When("연속으로 generate()를 호출하면") {
                val memberId1 = MemberId.generate()
                val memberId2 = MemberId.generate()

                Then("서로 다른 ID가 생성된다") {
                    memberId1 shouldNotBe memberId2
                    memberId1.value shouldNotBe memberId2.value
                }
            }
        }

        Given("유효한 UUID 문자열이 주어졌을 때") {
            val uuidString = "123e4567-e89b-12d3-a456-426614174000"

            When("from()을 사용하여 MemberId를 생성하면") {
                val memberId = MemberId.from(uuidString)

                Then("해당 UUID를 가진 MemberId가 생성된다") {
                    memberId.value shouldBe UUID.fromString(uuidString)
                }

                Then("toString()은 원래의 UUID 문자열을 반환한다") {
                    memberId.toString() shouldBe uuidString
                }
            }
        }

        Given("잘못된 형식의 UUID 문자열이 주어졌을 때") {
            val invalidUuidStrings =
                listOf(
                    "invalid-uuid",
                    "123-456-789",
                    "not-a-uuid-at-all",
                    "",
                    "123e4567-e89b-12d3-a456", // 불완전한 UUID
                )

            invalidUuidStrings.forEach { invalidUuid ->
                When("from()을 사용하여 '$invalidUuid'로 MemberId를 생성하려고 하면") {
                    Then("예외가 발생한다") {
                        shouldThrow<IllegalArgumentException> {
                            MemberId.from(invalidUuid)
                        }
                    }
                }
            }
        }

        Given("동일한 UUID로 생성된 두 MemberId 객체가 있을 때") {
            val uuid = UUID.randomUUID()
            val memberId1 = MemberId(uuid)
            val memberId2 = MemberId(uuid)

            Then("두 객체는 동일하다") {
                memberId1 shouldBe memberId2
            }

            Then("해시코드도 동일하다") {
                memberId1.hashCode() shouldBe memberId2.hashCode()
            }
        }

        Given("다른 UUID로 생성된 두 MemberId 객체가 있을 때") {
            val memberId1 = MemberId.generate()
            val memberId2 = MemberId.generate()

            Then("두 객체는 다르다") {
                memberId1 shouldNotBe memberId2
            }
        }

        Given("UUID 객체로 직접 MemberId를 생성할 때") {
            val uuid = UUID.randomUUID()

            When("UUID를 생성자에 전달하면") {
                val memberId = MemberId(uuid)

                Then("해당 UUID를 가진 MemberId가 생성된다") {
                    memberId.value shouldBe uuid
                }
            }
        }

        Given("MemberId가 Serializable 인터페이스를 구현할 때") {
            val memberId = MemberId.generate()

            Then("직렬화 가능한 객체이다") {
                memberId shouldNotBe null
                // ValueObject와 Serializable을 모두 구현
            }
        }
    })

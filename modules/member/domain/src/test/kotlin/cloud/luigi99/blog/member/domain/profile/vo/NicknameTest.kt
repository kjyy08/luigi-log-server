package cloud.luigi99.blog.member.domain.profile.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class NicknameTest :
    BehaviorSpec({

        Given("유효한 닉네임이 주어졌을 때") {
            val validNickname = "개발자"

            When("Nickname 객체를 생성하면") {
                val nickname = Nickname(validNickname)

                Then("닉네임 값이 올바르게 저장된다") {
                    nickname.value shouldBe validNickname
                }
            }
        }

        Given("다양한 길이의 유효한 닉네임이 주어졌을 때") {
            val validNicknames =
                listOf(
                    "A", // 1자
                    "닉네임", // 한글
                    "Developer123", // 영문 + 숫자
                    "a".repeat(100), // 최대 길이 100자
                )

            validNicknames.forEach { validNickname ->
                When("닉네임 '$validNickname'로 Nickname 객체를 생성하면") {
                    val nickname = Nickname(validNickname)

                    Then("정상적으로 생성된다") {
                        nickname shouldNotBe null
                        nickname.value shouldBe validNickname
                    }
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            val emptyNickname = ""

            When("Nickname 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Nickname(emptyNickname)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Nickname cannot be blank"
                }
            }
        }

        Given("공백만 있는 문자열이 주어졌을 때") {
            val blankNickname = "   "

            When("Nickname 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Nickname(blankNickname)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Nickname cannot be blank"
                }
            }
        }

        Given("101자를 초과하는 닉네임이 주어졌을 때") {
            val longNickname = "a".repeat(101)

            When("Nickname 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Nickname(longNickname)
                    }

                Then("예외가 발생하며 최대 길이 요구사항을 언급한다") {
                    exception.message shouldContain "Nickname cannot exceed 100 characters"
                }
            }
        }

        Given("동일한 닉네임으로 생성된 두 Nickname 객체가 있을 때") {
            val nickname1 = Nickname("개발자")
            val nickname2 = Nickname("개발자")

            Then("두 객체는 동일하다") {
                nickname1 shouldBe nickname2
            }

            Then("해시코드도 동일하다") {
                nickname1.hashCode() shouldBe nickname2.hashCode()
            }
        }

        Given("다른 닉네임으로 생성된 두 Nickname 객체가 있을 때") {
            val nickname1 = Nickname("개발자1")
            val nickname2 = Nickname("개발자2")

            Then("두 객체는 다르다") {
                nickname1 shouldNotBe nickname2
            }
        }

        Given("경계값 테스트") {
            When("정확히 100자인 닉네임을 생성하면") {
                val nickname = Nickname("a".repeat(100))

                Then("정상적으로 생성된다") {
                    nickname.value.length shouldBe 100
                }
            }
        }
    })

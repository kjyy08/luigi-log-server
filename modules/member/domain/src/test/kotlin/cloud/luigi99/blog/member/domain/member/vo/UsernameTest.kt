package cloud.luigi99.blog.member.domain.member.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class UsernameTest :
    BehaviorSpec({

        Given("유효한 사용자 이름이 주어졌을 때") {
            val validUsername = "john_doe"

            When("Username 객체를 생성하면") {
                val username = Username(validUsername)

                Then("사용자 이름 값이 올바르게 저장된다") {
                    username.value shouldBe validUsername
                }

                Then("toString()은 사용자 이름 값을 반환한다") {
                    username.toString() shouldBe validUsername
                }
            }
        }

        Given("2자부터 100자까지의 다양한 길이의 사용자 이름이 주어졌을 때") {
            val validUsernames =
                listOf(
                    "ab", // 최소 길이 2자
                    "john", // 일반적인 이름
                    "user_name_123", // 언더스코어와 숫자 포함
                    "a".repeat(100), // 최대 길이 100자
                )

            validUsernames.forEach { validUsername ->
                When("사용자 이름 '$validUsername'로 Username 객체를 생성하면") {
                    val username = Username(validUsername)

                    Then("정상적으로 생성된다") {
                        username shouldNotBe null
                        username.value shouldBe validUsername
                    }
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            val emptyUsername = ""

            When("Username 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Username(emptyUsername)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Username cannot be blank"
                }
            }
        }

        Given("공백만 있는 문자열이 주어졌을 때") {
            val blankUsername = "   "

            When("Username 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Username(blankUsername)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Username cannot be blank"
                }
            }
        }

        Given("1자 길이의 사용자 이름이 주어졌을 때") {
            val shortUsername = "a"

            When("Username 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Username(shortUsername)
                    }

                Then("예외가 발생하며 최소 길이 요구사항을 언급한다") {
                    exception.message shouldContain "Username must be between 2 and 100 characters"
                    exception.message shouldContain "actual: 1"
                }
            }
        }

        Given("101자 길이의 사용자 이름이 주어졌을 때") {
            val longUsername = "a".repeat(101)

            When("Username 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Username(longUsername)
                    }

                Then("예외가 발생하며 최대 길이 요구사항을 언급한다") {
                    exception.message shouldContain "Username must be between 2 and 100 characters"
                    exception.message shouldContain "actual: 101"
                }
            }
        }

        Given("동일한 사용자 이름으로 생성된 두 Username 객체가 있을 때") {
            val username1 = Username("john_doe")
            val username2 = Username("john_doe")

            Then("두 객체는 동일하다") {
                username1 shouldBe username2
            }

            Then("해시코드도 동일하다") {
                username1.hashCode() shouldBe username2.hashCode()
            }
        }

        Given("다른 사용자 이름으로 생성된 두 Username 객체가 있을 때") {
            val username1 = Username("john_doe")
            val username2 = Username("jane_doe")

            Then("두 객체는 다르다") {
                username1 shouldNotBe username2
            }
        }

        Given("경계값 테스트") {
            When("정확히 2자인 사용자 이름을 생성하면") {
                val username = Username("ab")

                Then("정상적으로 생성된다") {
                    username.value shouldBe "ab"
                }
            }

            When("정확히 100자인 사용자 이름을 생성하면") {
                val username = Username("a".repeat(100))

                Then("정상적으로 생성된다") {
                    username.value.length shouldBe 100
                }
            }
        }
    })

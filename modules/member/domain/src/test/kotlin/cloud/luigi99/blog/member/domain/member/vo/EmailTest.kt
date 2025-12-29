package cloud.luigi99.blog.member.domain.member.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class EmailTest :
    BehaviorSpec({

        Given("유효한 이메일 주소가 주어졌을 때") {
            val validEmail = "user@example.com"

            When("Email 객체를 생성하면") {
                val email = Email(validEmail)

                Then("이메일 값이 올바르게 저장된다") {
                    email.value shouldBe validEmail
                }

                Then("toString()은 이메일 값을 반환한다") {
                    email.toString() shouldBe validEmail
                }
            }
        }

        Given("다양한 형식의 유효한 이메일이 주어졌을 때") {
            val validEmails =
                listOf(
                    "simple@example.com",
                    "user.name@example.com",
                    "user+tag@example.co.kr",
                    "user_name@example-domain.com",
                    "123@example.com",
                    "a@b.c",
                )

            validEmails.forEach { validEmail ->
                When("이메일 '$validEmail'로 Email 객체를 생성하면") {
                    val email = Email(validEmail)

                    Then("정상적으로 생성된다") {
                        email shouldNotBe null
                        email.value shouldBe validEmail
                    }
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            val emptyEmail = ""

            When("Email 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Email(emptyEmail)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Email cannot be blank"
                }
            }
        }

        Given("공백만 있는 문자열이 주어졌을 때") {
            val blankEmail = "   "

            When("Email 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Email(blankEmail)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Email cannot be blank"
                }
            }
        }

        Given("잘못된 형식의 이메일이 주어졌을 때") {
            val invalidEmails =
                listOf(
                    "invalid.email", // @ 없음
                    "@example.com", // 로컬 파트 없음
                    "user@", // 도메인 없음
                    "user @example.com", // 공백 포함
                    "user@example .com", // 도메인에 공백
                )

            invalidEmails.forEach { invalidEmail ->
                When("이메일 '$invalidEmail'로 Email 객체를 생성하려고 하면") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            Email(invalidEmail)
                        }

                    Then("예외가 발생한다") {
                        exception.message shouldNotBe null
                    }
                }
            }
        }

        Given("동일한 이메일 주소로 생성된 두 Email 객체가 있을 때") {
            val email1 = Email("user@example.com")
            val email2 = Email("user@example.com")

            Then("두 객체는 동일하다") {
                email1 shouldBe email2
            }

            Then("해시코드도 동일하다") {
                email1.hashCode() shouldBe email2.hashCode()
            }
        }

        Given("다른 이메일 주소로 생성된 두 Email 객체가 있을 때") {
            val email1 = Email("user1@example.com")
            val email2 = Email("user2@example.com")

            Then("두 객체는 다르다") {
                email1 shouldNotBe email2
            }
        }
    })

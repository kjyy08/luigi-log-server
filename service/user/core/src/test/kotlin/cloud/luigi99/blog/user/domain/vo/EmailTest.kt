package cloud.luigi99.blog.user.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class EmailTest : BehaviorSpec({

    given("Email 생성") {
        `when`("유효한 이메일 주소로 생성하면") {
            val email = Email.of("test@example.com")

            then("Email 객체가 생성된다") {
                email shouldNotBe null
                email.value shouldBe "test@example.com"
            }
        }

        `when`("대문자가 포함된 이메일 주소로 생성하면") {
            val email = Email.of("Test@Example.COM")

            then("소문자로 정규화된다") {
                email.value shouldBe "test@example.com"
            }
        }

        `when`("공백이 포함된 이메일 주소로 생성하면") {
            val email = Email.of("  test@example.com  ")

            then("공백이 제거되고 생성된다") {
                email.value shouldBe "test@example.com"
            }
        }

        `when`("빈 문자열로 생성하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Email.of("")
                }
                exception.message shouldContain "이메일은 필수값입니다"
            }
        }

        `when`("공백만 있는 문자열로 생성하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Email.of("   ")
                }
                exception.message shouldContain "이메일은 필수값입니다"
            }
        }

        `when`("잘못된 이메일 형식으로 생성하려고 하면") {
            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email.of("invalid-email")
                }
            }
        }

        `when`("@가 없는 이메일로 생성하려고 하면") {
            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email.of("invalidemail.com")
                }
            }
        }

        `when`("도메인이 없는 이메일로 생성하려고 하면") {
            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email.of("test@")
                }
            }
        }
    }

    given("Email 도메인 추출") {
        val email = Email.of("test@example.com")

        `when`("domain() 메서드를 호출하면") {
            val domain = email.domain()

            then("도메인 부분이 반환된다") {
                domain shouldBe "example.com"
            }
        }

        `when`("localPart() 메서드를 호출하면") {
            val localPart = email.localPart()

            then("로컬 파트가 반환된다") {
                localPart shouldBe "test"
            }
        }
    }

    given("Email 동등성 비교") {
        val email1 = Email.of("test@example.com")
        val email2 = Email.of("test@example.com")
        val email3 = Email.of("other@example.com")

        `when`("같은 이메일 주소를 가진 두 Email을 비교하면") {
            then("동등하다고 판단된다") {
                email1 shouldBe email2
                email1.hashCode() shouldBe email2.hashCode()
            }
        }

        `when`("다른 이메일 주소를 가진 두 Email을 비교하면") {
            then("동등하지 않다고 판단된다") {
                email1 shouldNotBe email3
            }
        }
    }

    given("Email 문자열 변환") {
        val email = Email.of("test@example.com")

        `when`("toString() 메서드를 호출하면") {
            val result = email.toString()

            then("이메일 주소가 반환된다") {
                result shouldBe "test@example.com"
            }
        }
    }
})

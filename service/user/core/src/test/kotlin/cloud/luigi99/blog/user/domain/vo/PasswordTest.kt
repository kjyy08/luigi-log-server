package cloud.luigi99.blog.user.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class PasswordTest : BehaviorSpec({

    val mockEncoder: (String) -> String = { "encoded_$it" }

    given("Password 생성") {
        `when`("유효한 평문 비밀번호로 생성하면") {
            val rawPassword = "Pass123!"
            val password = Password.create(rawPassword, mockEncoder)

            then("암호화된 Password가 생성된다") {
                password shouldNotBe null
                password.value shouldBe "encoded_$rawPassword"
            }
        }

        `when`("암호화된 비밀번호로 생성하면") {
            val encodedPassword = "encoded_password"
            val password = Password.fromEncoded(encodedPassword)

            then("Password가 생성된다") {
                password shouldNotBe null
                password.value shouldBe encodedPassword
            }
        }

        `when`("빈 문자열로 생성하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Password.create("", mockEncoder)
                }
                exception.message shouldContain "비밀번호는 필수값입니다"
            }
        }
    }

    given("Password 정책 검증") {
        `when`("8자 미만의 비밀번호로 생성하려고 하면") {
            val shortPassword = "Pass1!"

            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Password.create(shortPassword, mockEncoder)
                }
            }
        }

        `when`("특수문자가 없는 비밀번호로 생성하려고 하면") {
            val noSpecialChar = "Password123"

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Password.create(noSpecialChar, mockEncoder)
                }
                exception.message shouldContain "특수문자"
            }
        }

        `when`("숫자가 없는 비밀번호로 생성하려고 하면") {
            val noDigit = "Password!"

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Password.create(noDigit, mockEncoder)
                }
                exception.message shouldContain "숫자"
            }
        }

        `when`("영문자가 없는 비밀번호로 생성하려고 하면") {
            val noLetter = "12345678!"

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Password.create(noLetter, mockEncoder)
                }
                exception.message shouldContain "영문자"
            }
        }

        `when`("모든 정책을 만족하는 비밀번호로 생성하면") {
            val validPassword = "Valid123!"

            then("Password가 생성된다") {
                val password = Password.create(validPassword, mockEncoder)
                password shouldNotBe null
            }
        }
    }

    given("Password 동등성 비교") {
        val password1 = Password.fromEncoded("encoded_password")
        val password2 = Password.fromEncoded("encoded_password")
        val password3 = Password.fromEncoded("other_encoded_password")

        `when`("같은 암호화된 값을 가진 두 Password를 비교하면") {
            then("동등하다고 판단된다") {
                password1 shouldBe password2
                password1.hashCode() shouldBe password2.hashCode()
            }
        }

        `when`("다른 암호화된 값을 가진 두 Password를 비교하면") {
            then("동등하지 않다고 판단된다") {
                password1 shouldNotBe password3
            }
        }
    }

    given("Password 문자열 변환") {
        val password = Password.fromEncoded("encoded_password")

        `when`("toString() 메서드를 호출하면") {
            val result = password.toString()

            then("마스킹된 문자열이 반환된다") {
                result shouldBe "********"
            }
        }
    }

    given("Password 유효성 검증 함수") {
        `when`("validateRawPassword를 유효한 비밀번호로 호출하면") {
            val validPassword = "Valid123!"

            then("예외가 발생하지 않는다") {
                Password.validateRawPassword(validPassword)
            }
        }

        `when`("validateRawPassword를 무효한 비밀번호로 호출하면") {
            val invalidPassword = "weak"

            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Password.validateRawPassword(invalidPassword)
                }
            }
        }
    }
})

package cloud.luigi99.blog.common.validation

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PasswordValidatorTest : BehaviorSpec({

    val passwordValidator = PasswordValidator()

    given("비밀번호 검증") {
        `when`("유효한 비밀번호가 주어질 때") {
            val validPasswords = listOf(
                "Password123!",
                "Test@1234",
                "ComplexPass9@",
                "MySecure#Pass1"
            )

            then("모두 유효한 것으로 판단되어야 한다") {
                validPasswords.forEach { password ->
                    val result = passwordValidator.validate(password)
                    result.isValid shouldBe true
                    result.errorMessage shouldBe null
                }
            }
        }

        `when`("유효하지 않은 비밀번호가 주어질 때") {
            then("각각 적절한 오류 메시지가 반환되어야 한다") {
                passwordValidator.validate(null).let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "비밀번호는 필수입니다"
                }

                passwordValidator.validate("short").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "비밀번호는 최소 8자 이상이어야 합니다"
                }

                passwordValidator.validate("lowercase123!").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "대문자를 포함해야 합니다"
                }

                passwordValidator.validate("UPPERCASE123!").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "소문자를 포함해야 합니다"
                }

                passwordValidator.validate("Password!").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "숫자를 포함해야 합니다"
                }

                passwordValidator.validate("Password123").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "특수문자를 포함해야 합니다"
                }
            }
        }

        `when`("커스텀 설정으로 비밀번호 검증기를 생성할 때") {
            val customValidator = PasswordValidator(
                minLength = 6,
                requireUppercase = false,
                requireSpecialChar = false
            )

            then("커스텀 규칙에 따라 검증되어야 한다") {
                customValidator.validate("simple1").let { result ->
                    result.isValid shouldBe true
                    result.errorMessage shouldBe null
                }

                customValidator.validate("short").let { result ->
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "비밀번호는 최소 6자 이상이어야 합니다"
                }
            }
        }
    }
})
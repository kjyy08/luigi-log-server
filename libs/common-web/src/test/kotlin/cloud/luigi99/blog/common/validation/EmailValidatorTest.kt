package cloud.luigi99.blog.common.validation

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class EmailValidatorTest : BehaviorSpec({

    val emailValidator = EmailValidator()

    given("이메일 검증") {
        `when`("유효한 이메일 주소가 주어질 때") {
            val validEmails = listOf(
                "test@example.com",
                "user.name@domain.com",
                "test123@gmail.com",
                "user+tag@example.com",
                "user_name@domain.org"
            )

            then("모두 유효한 것으로 판단되어야 한다") {
                validEmails.forEach { email ->
                    val result = emailValidator.validate(email)
                    result.isValid shouldBe true
                    result.errorMessage shouldBe null
                }
            }
        }

        `when`("유효하지 않은 이메일 주소가 주어질 때") {
            val invalidEmails = listOf(
                "invalid-email",
                "@domain.com",
                "test@",
                "test..test@domain.com", // 연속된 점은 유효하지 않음
                null,
                "",
                "test@domain", // TLD 없음
                "test space@domain.com" // 공백 포함
            )

            then("모두 유효하지 않은 것으로 판단되어야 한다") {
                invalidEmails.forEach { email ->
                    val result = emailValidator.validate(email)
                    result.isValid shouldBe false
                    result.errorMessage shouldBe "유효하지 않은 이메일 형식입니다"
                }
            }
        }
    }
})
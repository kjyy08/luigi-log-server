package cloud.luigi99.blog.common.validation

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ValidationResultTest : BehaviorSpec({

    given("ValidationResult 생성") {
        `when`("isValid가 true이고 errorMessage가 null일 때") {
            val result = ValidationResult(true, null)

            then("검증 성공 상태여야 한다") {
                result.isValid shouldBe true
                result.errorMessage shouldBe null
            }
        }

        `when`("isValid가 false이고 errorMessage가 있을 때") {
            val errorMessage = "검증 실패 메시지"
            val result = ValidationResult(false, errorMessage)

            then("검증 실패 상태여야 한다") {
                result.isValid shouldBe false
                result.errorMessage shouldBe errorMessage
            }
        }
    }

    given("ValidationResult companion object") {
        `when`("success() 메서드를 호출할 때") {
            val result = ValidationResult.success()

            then("성공 상태의 ValidationResult가 반환되어야 한다") {
                result.isValid shouldBe true
                result.errorMessage shouldBe null
            }
        }

        `when`("failure() 메서드를 호출할 때") {
            val errorMessage = "실패 메시지"
            val result = ValidationResult.failure(errorMessage)

            then("실패 상태의 ValidationResult가 반환되어야 한다") {
                result.isValid shouldBe false
                result.errorMessage shouldBe errorMessage
            }
        }
    }

    given("ValidationResult 동등성 검사") {
        `when`("같은 값으로 생성된 두 ValidationResult를 비교할 때") {
            val result1 = ValidationResult(true, null)
            val result2 = ValidationResult(true, null)

            then("두 객체는 동일해야 한다") {
                result1 shouldBe result2
            }
        }

        `when`("다른 값으로 생성된 두 ValidationResult를 비교할 때") {
            val result1 = ValidationResult(true, null)
            val result2 = ValidationResult(false, "오류")

            then("두 객체는 다르다고 판단되어야 한다") {
                (result1 == result2) shouldBe false
            }
        }
    }
})
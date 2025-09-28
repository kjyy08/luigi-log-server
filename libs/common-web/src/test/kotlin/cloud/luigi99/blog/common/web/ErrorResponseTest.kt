package cloud.luigi99.blog.common.web

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class ErrorResponseTest : BehaviorSpec({

    given("ErrorResponse 기본 생성") {
        `when`("에러와 메시지, 경로로 ErrorResponse를 생성할 때") {
            val error = "VALIDATION_ERROR"
            val message = "입력값이 유효하지 않습니다"
            val path = "/api/users"
            val errorResponse = ErrorResponse.of(error, message, path)

            then("모든 필드가 올바르게 설정되어야 한다") {
                errorResponse.error shouldBe error
                errorResponse.message shouldBe message
                errorResponse.path shouldBe path
                errorResponse.details shouldBe null
                errorResponse.timestamp shouldNotBe null
            }
        }

        `when`("경로 없이 ErrorResponse를 생성할 때") {
            val error = "SERVER_ERROR"
            val message = "서버 내부 오류가 발생했습니다"
            val errorResponse = ErrorResponse.of(error, message)

            then("경로가 null로 설정되어야 한다") {
                errorResponse.error shouldBe error
                errorResponse.message shouldBe message
                errorResponse.path shouldBe null
                errorResponse.details shouldBe null
                errorResponse.timestamp shouldNotBe null
            }
        }
    }

    given("ErrorResponse 상세 정보 포함 생성") {
        `when`("상세 정보와 함께 ErrorResponse를 생성할 때") {
            val error = "VALIDATION_ERROR"
            val message = "입력값 검증 실패"
            val path = "/api/users"
            val details = listOf(
                "이메일 형식이 올바르지 않습니다",
                "비밀번호는 8자 이상이어야 합니다"
            )
            val errorResponse = ErrorResponse.of(error, message, path, details)

            then("상세 정보가 포함되어 설정되어야 한다") {
                errorResponse.error shouldBe error
                errorResponse.message shouldBe message
                errorResponse.path shouldBe path
                errorResponse.details shouldBe details
                errorResponse.timestamp shouldNotBe null
            }
        }

        `when`("빈 상세 정보 리스트와 함께 생성할 때") {
            val error = "BUSINESS_ERROR"
            val message = "비즈니스 규칙 위반"
            val path = "/api/orders"
            val details = emptyList<String>()
            val errorResponse = ErrorResponse.of(error, message, path, details)

            then("빈 리스트가 설정되어야 한다") {
                errorResponse.error shouldBe error
                errorResponse.message shouldBe message
                errorResponse.path shouldBe path
                errorResponse.details shouldBe emptyList()
                errorResponse.timestamp shouldNotBe null
            }
        }
    }

    given("ErrorResponse 타임스탬프") {
        `when`("ErrorResponse를 생성할 때") {
            val beforeCreation = LocalDateTime.now()
            val errorResponse = ErrorResponse.of("ERROR", "메시지")
            val afterCreation = LocalDateTime.now()

            then("현재 시간으로 타임스탬프가 설정되어야 한다") {
                errorResponse.timestamp shouldNotBe null
                (errorResponse.timestamp.isAfter(beforeCreation) || errorResponse.timestamp.isEqual(beforeCreation)) shouldBe true
                (errorResponse.timestamp.isBefore(afterCreation) || errorResponse.timestamp.isEqual(afterCreation)) shouldBe true
            }
        }
    }
})
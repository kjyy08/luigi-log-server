package cloud.luigi99.blog.common.web

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class ApiResponseTest : BehaviorSpec({

    given("ApiResponse 성공 응답") {
        `when`("데이터와 메시지가 있는 성공 응답을 생성할 때") {
            val data = "test data"
            val message = "성공적으로 처리되었습니다"
            val response = ApiResponse.success(data, message)

            then("성공 상태와 데이터, 메시지가 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe data
                response.message shouldBe message
                response.timestamp shouldNotBe null
            }
        }

        `when`("데이터 없이 메시지만 있는 성공 응답을 생성할 때") {
            val message = "처리 완료"
            val response = ApiResponse.success<String>(message)

            then("성공 상태와 메시지가 설정되고 데이터는 null이어야 한다") {
                response.success shouldBe true
                response.data shouldBe null
                response.message shouldBe message
                response.timestamp shouldNotBe null
            }
        }

        `when`("기본 성공 응답을 생성할 때") {
            val response = ApiResponse.success<String>("성공")

            then("기본 성공 메시지가 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe null
                response.message shouldBe "성공"
                response.timestamp shouldNotBe null
            }
        }
    }

    given("ApiResponse 실패 응답") {
        `when`("실패 응답을 생성할 때") {
            val errorMessage = "처리 중 오류가 발생했습니다"
            val response = ApiResponse.failure<String>(errorMessage)

            then("실패 상태와 오류 메시지가 설정되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe errorMessage
                response.timestamp shouldNotBe null
            }
        }
    }

    given("ApiResponse 타임스탬프") {
        `when`("응답을 생성할 때") {
            val beforeCreation = LocalDateTime.now()
            val response = ApiResponse.success<String>("test")
            val afterCreation = LocalDateTime.now()

            then("현재 시간으로 타임스탬프가 설정되어야 한다") {
                response.timestamp shouldNotBe null
                (response.timestamp.isAfter(beforeCreation) || response.timestamp.isEqual(beforeCreation)) shouldBe true
                (response.timestamp.isBefore(afterCreation) || response.timestamp.isEqual(afterCreation)) shouldBe true
            }
        }
    }
})
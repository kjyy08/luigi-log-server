package cloud.luigi99.blog.common.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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
            val response = ApiResponse.success<String>()

            then("기본 성공 메시지가 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe null
                response.message shouldBe "성공"
                response.timestamp shouldNotBe null
            }
        }

        `when`("복합 객체 데이터로 성공 응답을 생성할 때") {
            data class TestData(val id: Long, val name: String)
            val testData = TestData(1L, "테스트")
            val response = ApiResponse.success(testData)

            then("복합 객체가 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe testData
                response.message shouldBe "성공"
                response.timestamp shouldNotBe null
            }
        }

        `when`("빈 문자열 메시지로 성공 응답을 생성할 때") {
            val response = ApiResponse.success("data", "")

            then("빈 문자열 메시지가 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe "data"
                response.message shouldBe ""
                response.timestamp shouldNotBe null
            }
        }

        `when`("컬렉션 데이터로 성공 응답을 생성할 때") {
            val listData = listOf("item1", "item2", "item3")
            val response = ApiResponse.success(listData)

            then("컬렉션이 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe listData
                response.message shouldBe "성공"
                response.timestamp shouldNotBe null
            }
        }
    }

    given("ApiResponse 실패 응답") {
        `when`("failure 메서드로 실패 응답을 생성할 때") {
            val errorMessage = "처리 중 오류가 발생했습니다"
            val response = ApiResponse.failure<String>(errorMessage)

            then("실패 상태와 오류 메시지가 설정되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe errorMessage
                response.timestamp shouldNotBe null
            }
        }

        `when`("error 메서드로 에러 응답을 생성할 때") {
            val errorMessage = "시스템 에러가 발생했습니다"
            val response = ApiResponse.error<String>(errorMessage)

            then("실패 상태와 에러 메시지가 설정되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe errorMessage
                response.timestamp shouldNotBe null
            }
        }

        `when`("빈 문자열 메시지로 실패 응답을 생성할 때") {
            val response = ApiResponse.failure<String>("")

            then("빈 문자열 에러 메시지가 설정되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe ""
                response.timestamp shouldNotBe null
            }
        }
    }

    given("ApiResponse 직접 생성자 사용") {
        `when`("모든 파라미터를 지정해서 생성할 때") {
            val customTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
            val response = ApiResponse(
                success = true,
                data = "custom data",
                message = "custom message",
                timestamp = customTimestamp
            )

            then("지정된 값들이 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe "custom data"
                response.message shouldBe "custom message"
                response.timestamp shouldBe customTimestamp
            }
        }

        `when`("필수 파라미터만 지정해서 생성할 때") {
            val response = ApiResponse<String>(success = false)

            then("기본값들이 올바르게 설정되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe ""
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

    given("ApiResponse JSON 직렬화/역직렬화") {
        val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        `when`("성공 응답을 JSON으로 직렬화할 때") {
            val response = ApiResponse.success("test data", "성공 메시지")
            val json = objectMapper.writeValueAsString(response)

            then("올바른 JSON 형식으로 변환되어야 한다") {
                json shouldContain "\"success\":true"
                json shouldContain "\"data\":\"test data\""
                json shouldContain "\"message\":\"성공 메시지\""
                json shouldContain "\"timestamp\""
            }
        }

        `when`("실패 응답을 JSON으로 직렬화할 때") {
            val response = ApiResponse.failure<String>("에러 메시지")
            val json = objectMapper.writeValueAsString(response)

            then("올바른 JSON 형식으로 변환되어야 한다") {
                json shouldContain "\"success\":false"
                json shouldContain "\"data\":null"
                json shouldContain "\"message\":\"에러 메시지\""
                json shouldContain "\"timestamp\""
            }
        }

        `when`("JSON을 ApiResponse로 역직렬화할 때") {
            val json = """
                {
                    "success": true,
                    "data": "역직렬화 테스트",
                    "message": "테스트 메시지",
                    "timestamp": "2024-01-01 12:00:00"
                }
            """.trimIndent()

            val response: ApiResponse<String> = objectMapper.readValue(json)

            then("올바르게 객체로 변환되어야 한다") {
                response.success shouldBe true
                response.data shouldBe "역직렬화 테스트"
                response.message shouldBe "테스트 메시지"
                response.timestamp shouldBe LocalDateTime.of(2024, 1, 1, 12, 0, 0)
            }
        }

        `when`("타임스탬프 포맷이 적용되어 JSON으로 직렬화할 때") {
            val customTimestamp = LocalDateTime.of(2024, 12, 25, 15, 30, 45)
            val response = ApiResponse(
                success = true,
                data = "format test",
                message = "포맷 테스트",
                timestamp = customTimestamp
            )
            val json = objectMapper.writeValueAsString(response)

            then("yyyy-MM-dd HH:mm:ss 포맷이 적용되어야 한다") {
                json shouldContain "\"timestamp\":\"2024-12-25 15:30:45\""
            }
        }
    }

    given("ApiResponse 다양한 제네릭 타입") {
        `when`("Int 타입 데이터로 응답을 생성할 때") {
            val response = ApiResponse.success(42)

            then("Int 데이터가 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe 42
                response.message shouldBe "성공"
            }
        }

        `when`("Boolean 타입 데이터로 응답을 생성할 때") {
            val response = ApiResponse.success(true)

            then("Boolean 데이터가 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe true
                response.message shouldBe "성공"
            }
        }

        `when`("Map 타입 데이터로 응답을 생성할 때") {
            val mapData = mapOf("key1" to "value1", "key2" to "value2")
            val response = ApiResponse.success(mapData)

            then("Map 데이터가 올바르게 설정되어야 한다") {
                response.success shouldBe true
                response.data shouldBe mapData
                response.message shouldBe "성공"
            }
        }

        `when`("null 타입으로 실패 응답을 생성할 때") {
            val response = ApiResponse.error<Any?>("null 타입 에러")

            then("null 타입이 올바르게 처리되어야 한다") {
                response.success shouldBe false
                response.data shouldBe null
                response.message shouldBe "null 타입 에러"
            }
        }
    }
})
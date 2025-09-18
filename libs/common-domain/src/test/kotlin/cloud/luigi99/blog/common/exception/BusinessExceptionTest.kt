package cloud.luigi99.blog.common.exception

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

/**
 * BusinessException의 행위를 검증하는 테스트
 */
class BusinessExceptionTest : BehaviorSpec({

    given("기본 생성자로 BusinessException을 생성할 때") {
        val message = "비즈니스 로직 오류가 발생했습니다"
        val exception = BusinessException(message)

        `when`("예외 정보를 확인하면") {
            then("메시지와 기본 에러 코드가 설정된다") {
                exception.message shouldBe message
                exception.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
                exception.cause shouldBe null
            }
        }
    }

    given("에러 코드를 포함하여 BusinessException을 생성할 때") {
        val errorCode = ErrorCode.COMMON_INVALID_INPUT.code
        val message = "입력값이 유효하지 않습니다"
        val exception = BusinessException(errorCode, message)

        `when`("예외 정보를 확인하면") {
            then("커스텀 에러 코드와 메시지가 설정된다") {
                exception.errorCode shouldBe errorCode
                exception.message shouldBe message
            }
        }
    }

    given("원인 예외를 포함하여 BusinessException을 생성할 때") {
        val cause = IllegalArgumentException("원인 예외")
        val exception = BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.code, "래핑된 오류", cause as Throwable?)

        `when`("예외 정보를 확인하면") {
            then("원인 예외가 올바르게 설정된다") {
                exception.cause shouldBe cause
                exception.errorCode shouldBe ErrorCode.COMMON_INTERNAL_SERVER_ERROR.code
            }
        }
    }

    given("포맷된 메시지로 BusinessException을 생성할 때") {
        val errorCode = ErrorCode.USER_NOT_FOUND.code
        val messageFormat = "사용자를 찾을 수 없습니다. ID: %s, 이름: %s"
        val userId = "12345"
        val userName = "테스트사용자"
        val exception = BusinessException(errorCode, messageFormat, userId, userName)

        `when`("예외 메시지를 확인하면") {
            val expectedMessage = "사용자를 찾을 수 없습니다. ID: 12345, 이름: 테스트사용자"

            then("포맷된 메시지가 생성된다") {
                exception.message shouldBe expectedMessage
                exception.errorCode shouldBe errorCode
            }
        }
    }

    given("다른 BusinessException을 기반으로 새 예외를 생성할 때") {
        val originalException = BusinessException(ErrorCode.COMMON_BUSINESS_ERROR.code, "원본 오류")
        val additionalMessage = "추가 정보"
        val newException = BusinessException(originalException, additionalMessage)

        `when`("새 예외의 정보를 확인하면") {
            then("원본 예외의 정보와 추가 메시지가 합쳐진다") {
                newException.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
                newException.message shouldBe "원본 오류 - 추가 정보"
                newException.cause shouldBe originalException.cause
            }
        }
    }

    given("다른 BusinessException을 기반으로 추가 메시지 없이 새 예외를 생성할 때") {
        val originalException = BusinessException(ErrorCode.ENTITY_CREATION_FAILED.code, "원본 오류")
        val newException = BusinessException(originalException)

        `when`("새 예외의 정보를 확인하면") {
            then("원본 예외의 정보가 그대로 복사된다") {
                newException.errorCode shouldBe ErrorCode.ENTITY_CREATION_FAILED.code
                newException.message shouldBe "원본 오류"
            }
        }
    }

    given("에러 코드 확인 기능을 테스트할 때") {
        val exception = BusinessException(ErrorCode.ENTITY_NOT_FOUND.code, "테스트 오류")

        `when`("일치하는 에러 코드로 확인하면") {
            val hasErrorCode = exception.hasErrorCode(ErrorCode.ENTITY_NOT_FOUND.code)

            then("true를 반환한다") {
                hasErrorCode shouldBe true
            }
        }

        `when`("일치하지 않는 에러 코드로 확인하면") {
            val hasErrorCode = exception.hasErrorCode(ErrorCode.ENTITY_ALREADY_EXISTS.code)

            then("false를 반환한다") {
                hasErrorCode shouldBe false
            }
        }
    }

    given("여러 에러 코드 중 하나와 일치하는지 확인할 때") {
        val exception = BusinessException(ErrorCode.VALIDATION_INVALID_EMAIL.code, "유효성 검증 오류")

        `when`("포함된 에러 코드들로 확인하면") {
            val hasAnyErrorCode = exception.hasAnyErrorCode(ErrorCode.COMMON_INVALID_INPUT.code, ErrorCode.VALIDATION_INVALID_EMAIL.code, ErrorCode.COMMON_BUSINESS_ERROR.code)

            then("true를 반환한다") {
                hasAnyErrorCode shouldBe true
            }
        }

        `when`("포함되지 않은 에러 코드들로 확인하면") {
            val hasAnyErrorCode = exception.hasAnyErrorCode(ErrorCode.COMMON_INVALID_INPUT.code, ErrorCode.COMMON_INTERNAL_SERVER_ERROR.code, ErrorCode.ENTITY_CREATION_FAILED.code)

            then("false를 반환한다") {
                hasAnyErrorCode shouldBe false
            }
        }
    }

    given("상세한 에러 메시지를 확인할 때") {
        val exception = BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS.code, "인증에 실패했습니다")

        `when`("getDetailedMessage를 호출하면") {
            val detailedMessage = exception.getDetailedMessage()

            then("에러 코드와 메시지를 포함한 문자열을 반환한다") {
                detailedMessage shouldBe "[${ErrorCode.AUTH_INVALID_CREDENTIALS.code}] 인증에 실패했습니다"
            }
        }
    }

    given("toString 메서드를 테스트할 때") {
        val exception = BusinessException(ErrorCode.ENTITY_UPDATE_FAILED.code, "커스텀 오류 메시지")

        `when`("toString을 호출하면") {
            val toStringResult = exception.toString()

            then("클래스명, 에러 코드, 메시지를 포함한 문자열을 반환한다") {
                toStringResult shouldContain "BusinessException"
                toStringResult shouldContain ErrorCode.ENTITY_UPDATE_FAILED.code
                toStringResult shouldContain "커스텀 오류 메시지"
            }
        }
    }

    given("BusinessException이 RuntimeException의 특성을 가지는지 확인할 때") {
        val exception = BusinessException(ErrorCode.ENTITY_DELETE_FAILED.code, "테스트 에러")

        `when`("예외가 던져지면") {
            then("RuntimeException처럼 언체크드 예외로 동작한다") {
                // 함수가 checked exception을 던지지 않아도 컴파일 에러가 발생하지 않음을 테스트
                val thrownException = try {
                    throw exception
                } catch (e: RuntimeException) {
                    e
                }

                thrownException shouldBe exception
                thrownException.message shouldBe "테스트 에러"
            }
        }

        `when`("스택트레이스를 확인하면") {
            then("RuntimeException의 기본 동작을 수행한다") {
                exception.stackTrace shouldNotBe null
                exception.stackTrace.isNotEmpty() shouldBe true
            }
        }
    }

    given("에러 코드가 null이 아닌지 확인할 때") {
        val exception1 = BusinessException("기본 메시지")
        val exception2 = BusinessException(ErrorCode.ENTITY_DELETE_FAILED.code, "커스텀 메시지")

        `when`("에러 코드를 확인하면") {
            then("모든 경우에 null이 아니다") {
                exception1.errorCode shouldNotBe null
                exception2.errorCode shouldNotBe null
                exception1.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
                exception2.errorCode shouldBe ErrorCode.ENTITY_DELETE_FAILED.code
            }
        }
    }
})
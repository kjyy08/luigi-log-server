package cloud.luigi99.blog.common.exception

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.util.*

/**
 * DomainException의 행위를 검증하는 테스트
 */
class DomainExceptionTest : BehaviorSpec({

    given("기본 생성자로 DomainException을 생성할 때") {
        val message = "도메인 규칙이 위반되었습니다"
        val exception = DomainException(message)

        `when`("예외 정보를 확인하면") {
            then("메시지와 기본 에러 코드, Unknown 도메인 컨텍스트가 설정된다") {
                exception.message shouldBe message
                exception.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
                exception.domainContext shouldBe "Unknown"
            }
        }
    }

    given("도메인 컨텍스트를 포함하여 DomainException을 생성할 때") {
        val domainContext = "User"
        val message = "사용자 도메인 규칙 위반"
        val exception = DomainException(domainContext, message)

        `when`("예외 정보를 확인하면") {
            then("도메인 컨텍스트와 메시지가 올바르게 설정된다") {
                exception.domainContext shouldBe domainContext
                exception.message shouldBe message
                exception.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
            }
        }
    }

    given("도메인별 에러 코드를 포함하여 DomainException을 생성할 때") {
        val domainContext = "Post"
        val errorCode = ErrorCode.POST_NOT_DRAFT.code
        val message = "포스트 상태가 유효하지 않습니다"
        val exception = DomainException(domainContext, errorCode, message)

        `when`("예외 정보를 확인하면") {
            then("커스텀 에러 코드와 도메인 컨텍스트가 설정된다") {
                exception.domainContext shouldBe domainContext
                exception.errorCode shouldBe errorCode
                exception.message shouldBe message
            }
        }
    }

    given("포맷된 메시지로 DomainException을 생성할 때") {
        val domainContext = "Comment"
        val errorCode = ErrorCode.VALIDATION_CONTENT_TOO_LONG.code
        val messageFormat = "댓글이 너무 깁니다. 최대 길이: %d, 현재 길이: %d"
        val maxLength = 500
        val currentLength = 600
        val exception = DomainException(domainContext, errorCode, messageFormat, maxLength, currentLength)

        `when`("예외 메시지를 확인하면") {
            val expectedMessage = "댓글이 너무 깁니다. 최대 길이: 500, 현재 길이: 600"

            then("포맷된 메시지가 생성된다") {
                exception.message shouldBe expectedMessage
                exception.domainContext shouldBe domainContext
                exception.errorCode shouldBe errorCode
            }
        }
    }

    given("다른 DomainException을 기반으로 새 예외를 생성할 때") {
        val originalException = DomainException("User", ErrorCode.USER_NOT_FOUND.code, "사용자를 찾을 수 없습니다")
        val additionalMessage = "권한 검증 중 오류 발생"
        val newException = DomainException(originalException, additionalMessage)

        `when`("새 예외의 정보를 확인하면") {
            then("원본 예외의 정보와 추가 메시지가 합쳐진다") {
                newException.domainContext shouldBe "User"
                newException.errorCode shouldBe ErrorCode.USER_NOT_FOUND.code
                newException.message shouldBe "사용자를 찾을 수 없습니다 - 권한 검증 중 오류 발생"
            }
        }
    }

    given("도메인 컨텍스트 확인 기능을 테스트할 때") {
        val exception = DomainException("Post", "포스트 도메인 오류")

        `when`("일치하는 도메인 컨텍스트로 확인하면") {
            val isFromDomain = exception.isFromDomain("Post")

            then("true를 반환한다") {
                isFromDomain shouldBe true
            }
        }

        `when`("대소문자가 다른 도메인 컨텍스트로 확인하면") {
            val isFromDomain = exception.isFromDomain("post")

            then("대소문자 무시하고 true를 반환한다") {
                isFromDomain shouldBe true
            }
        }

        `when`("일치하지 않는 도메인 컨텍스트로 확인하면") {
            val isFromDomain = exception.isFromDomain("User")

            then("false를 반환한다") {
                isFromDomain shouldBe false
            }
        }
    }

    given("여러 도메인 컨텍스트 중 하나와 일치하는지 확인할 때") {
        val exception = DomainException("Comment", "댓글 도메인 오류")

        `when`("포함된 도메인 컨텍스트들로 확인하면") {
            val isFromAnyDomain = exception.isFromAnyDomain("Post", "Comment", "User")

            then("true를 반환한다") {
                isFromAnyDomain shouldBe true
            }
        }

        `when`("포함되지 않은 도메인 컨텍스트들로 확인하면") {
            val isFromAnyDomain = exception.isFromAnyDomain("Post", "User", "Category")

            then("false를 반환한다") {
                isFromAnyDomain shouldBe false
            }
        }
    }

    given("상세한 에러 메시지를 확인할 때") {
        val exception = DomainException("User", ErrorCode.AUTH_INVALID_CREDENTIALS.code, "인증에 실패했습니다")

        `when`("getDetailedMessage를 호출하면") {
            val detailedMessage = exception.getDetailedMessage()

            then("도메인 컨텍스트, 에러 코드, 메시지를 포함한 문자열을 반환한다") {
                detailedMessage shouldBe "[User:${ErrorCode.AUTH_INVALID_CREDENTIALS.code}] 인증에 실패했습니다"
            }
        }
    }

    given("companion object의 팩토리 메서드를 테스트할 때") {
        `when`("entityNotFound로 예외를 생성하면") {
            val entityId = UUID.randomUUID()
            val exception = DomainException.entityNotFound("User", entityId)

            then("엔티티를 찾을 수 없다는 예외가 생성된다") {
                exception.domainContext shouldBe "User"
                exception.errorCode shouldBe ErrorCode.ENTITY_NOT_FOUND.code
                exception.message shouldContain "User를 찾을 수 없습니다"
                exception.message shouldContain entityId.toString()
            }
        }

        `when`("businessRuleViolation으로 예외를 생성하면") {
            val exception = DomainException.businessRuleViolation("Post", "최소 제목 길이", "제목은 최소 5자 이상이어야 합니다")

            then("비즈니스 규칙 위반 예외가 생성된다") {
                exception.domainContext shouldBe "Post"
                exception.errorCode shouldBe ErrorCode.COMMON_BUSINESS_ERROR.code
                exception.message shouldContain "Post 도메인의 '최소 제목 길이' 규칙이 위반되었습니다"
                exception.message shouldContain "제목은 최소 5자 이상이어야 합니다"
            }
        }

        `when`("invalidStateTransition으로 예외를 생성하면") {
            val exception = DomainException.invalidStateTransition("Post", "DRAFT", "ARCHIVED")

            then("유효하지 않은 상태 전환 예외가 생성된다") {
                exception.domainContext shouldBe "Post"
                exception.errorCode shouldBe ErrorCode.STATE_INVALID_TRANSITION.code
                exception.message shouldContain "DRAFT 상태에서 ARCHIVED 상태로 전환할 수 없습니다"
            }
        }
    }

    given("DomainException이 BusinessException의 특성을 제대로 상속받았는지 확인할 때") {
        val domainException = DomainException("User", ErrorCode.USER_NOT_FOUND.code, "사용자를 찾을 수 없습니다")
        val businessException = BusinessException(ErrorCode.USER_NOT_FOUND.code, "사용자를 찾을 수 없습니다")

        `when`("동일한 에러 코드와 메시지로 생성된 예외들을 비교하면") {
            then("BusinessException과 동일한 핵심 기능을 제공한다") {
                domainException.errorCode shouldBe businessException.errorCode
                domainException.message shouldBe businessException.message
                domainException.hasErrorCode(ErrorCode.USER_NOT_FOUND.code) shouldBe businessException.hasErrorCode(ErrorCode.USER_NOT_FOUND.code)
                domainException.getDetailedMessage() shouldContain ErrorCode.USER_NOT_FOUND.code
            }
        }

        `when`("DomainException만의 고유 기능을 확인하면") {
            then("도메인 컨텍스트라는 추가 정보를 제공한다") {
                domainException.domainContext shouldBe "User"
                domainException.isFromDomain("User") shouldBe true
                domainException.getDetailedMessage() shouldContain "User:"
            }
        }
    }

    given("BusinessException의 기능을 상속받는지 확인할 때") {
        val exception = DomainException("User", ErrorCode.VALIDATION_INVALID_EMAIL.code, "사용자 유효성 검증 실패")

        `when`("부모 클래스의 메서드를 사용하면") {
            then("정상적으로 동작한다") {
                exception.hasErrorCode(ErrorCode.VALIDATION_INVALID_EMAIL.code) shouldBe true
                exception.hasAnyErrorCode(ErrorCode.USER_NOT_FOUND.code, ErrorCode.VALIDATION_INVALID_EMAIL.code) shouldBe true
                exception.hasErrorCode(ErrorCode.ENTITY_ALREADY_EXISTS.code) shouldBe false
            }
        }
    }
})
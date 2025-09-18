package cloud.luigi99.blog.common.exception

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * ErrorCode enum의 행위를 검증하는 테스트
 */
class ErrorCodeTest : BehaviorSpec({

    given("ErrorCode enum이 정의되어 있을 때") {
        `when`("모든 에러 코드를 조회하면") {
            val allCodes = ErrorCode.getAllCodes()

            then("정의된 모든 에러 코드가 반환된다") {
                allCodes shouldHaveSize ErrorCode.entries.size
                allCodes shouldContainAll listOf(
                    "COMMON_BUSINESS_ERROR",
                    "COMMON_INVALID_PARAMETER",
                    "ENTITY_NOT_FOUND",
                    "USER_NOT_FOUND",
                    "POST_NOT_FOUND",
                    "AUTH_UNAUTHENTICATED"
                )
            }
        }

        `when`("각 ErrorCode의 toString을 호출하면") {
            val errorCode = ErrorCode.COMMON_BUSINESS_ERROR

            then("code 값이 반환된다") {
                errorCode.toString() shouldBe "COMMON_BUSINESS_ERROR"
            }
        }
    }

    given("유효한 에러 코드 문자열이 주어졌을 때") {
        val validCode = "USER_NOT_FOUND"

        `when`("fromCode 메서드를 호출하면") {
            val result = ErrorCode.fromCode(validCode)

            then("해당하는 ErrorCode를 반환한다") {
                result shouldBe ErrorCode.USER_NOT_FOUND
                result?.code shouldBe validCode
                result?.description shouldBe "사용자를 찾을 수 없습니다"
            }
        }

        `when`("fromCodeOrDefault 메서드를 호출하면") {
            val result = ErrorCode.fromCodeOrDefault(validCode)

            then("해당하는 ErrorCode를 반환한다") {
                result shouldBe ErrorCode.USER_NOT_FOUND
                result.code shouldBe validCode
            }
        }
    }

    given("유효하지 않은 에러 코드 문자열이 주어졌을 때") {
        val invalidCode = "INVALID_ERROR_CODE"

        `when`("fromCode 메서드를 호출하면") {
            val result = ErrorCode.fromCode(invalidCode)

            then("null을 반환한다") {
                result shouldBe null
            }
        }

        `when`("fromCodeOrDefault 메서드를 기본값 없이 호출하면") {
            val result = ErrorCode.fromCodeOrDefault(invalidCode)

            then("COMMON_BUSINESS_ERROR를 반환한다") {
                result shouldBe ErrorCode.COMMON_BUSINESS_ERROR
            }
        }

        `when`("fromCodeOrDefault 메서드를 커스텀 기본값과 함께 호출하면") {
            val customDefault = ErrorCode.ENTITY_NOT_FOUND
            val result = ErrorCode.fromCodeOrDefault(invalidCode, customDefault)

            then("커스텀 기본값을 반환한다") {
                result shouldBe customDefault
            }
        }
    }

    given("특정 접두사로 시작하는 에러 코드들이 있을 때") {
        `when`("COMMON_ 접두사로 검색하면") {
            val commonErrors = ErrorCode.getByPrefix("COMMON_")

            then("COMMON_으로 시작하는 모든 에러 코드가 반환된다") {
                commonErrors shouldContain ErrorCode.COMMON_BUSINESS_ERROR
                commonErrors shouldContain ErrorCode.COMMON_INVALID_PARAMETER
                commonErrors shouldContain ErrorCode.COMMON_INVALID_INPUT
                commonErrors shouldContain ErrorCode.COMMON_REQUIRED_VALUE_MISSING
                commonErrors shouldContain ErrorCode.COMMON_INTERNAL_SERVER_ERROR

                commonErrors.forEach { errorCode ->
                    errorCode.code.startsWith("COMMON_") shouldBe true
                }
            }
        }

        `when`("USER_ 접두사로 검색하면") {
            val userErrors = ErrorCode.getByPrefix("USER_")

            then("USER_로 시작하는 모든 에러 코드가 반환된다") {
                userErrors shouldContain ErrorCode.USER_NOT_FOUND
                userErrors shouldContain ErrorCode.USER_ALREADY_EXISTS
                userErrors shouldContain ErrorCode.USER_EMAIL_DUPLICATE
                userErrors shouldContain ErrorCode.USER_DEACTIVATED

                userErrors.forEach { errorCode ->
                    errorCode.code.startsWith("USER_") shouldBe true
                }
            }
        }

        `when`("존재하지 않는 접두사로 검색하면") {
            val nonExistentErrors = ErrorCode.getByPrefix("NONEXISTENT_")

            then("빈 리스트가 반환된다") {
                nonExistentErrors shouldHaveSize 0
            }
        }

        `when`("빈 접두사로 검색하면") {
            val allErrors = ErrorCode.getByPrefix("")

            then("모든 에러 코드가 반환된다") {
                allErrors shouldHaveSize ErrorCode.entries.size
            }
        }
    }

    given("각 ErrorCode의 프로퍼티를 검증할 때") {
        `when`("VALIDATION_INVALID_EMAIL을 확인하면") {
            val errorCode = ErrorCode.VALIDATION_INVALID_EMAIL

            then("올바른 code와 description을 가진다") {
                errorCode.code shouldBe "VALIDATION_INVALID_EMAIL"
                errorCode.description shouldBe "이메일 형식이 올바르지 않습니다"
            }
        }

        `when`("POST_ALREADY_PUBLISHED를 확인하면") {
            val errorCode = ErrorCode.POST_ALREADY_PUBLISHED

            then("올바른 code와 description을 가진다") {
                errorCode.code shouldBe "POST_ALREADY_PUBLISHED"
                errorCode.description shouldBe "이미 발행된 포스트입니다"
            }
        }

        `when`("AUTH_TOKEN_EXPIRED를 확인하면") {
            val errorCode = ErrorCode.AUTH_TOKEN_EXPIRED

            then("올바른 code와 description을 가진다") {
                errorCode.code shouldBe "AUTH_TOKEN_EXPIRED"
                errorCode.description shouldBe "인증 토큰이 만료되었습니다"
            }
        }
    }

    given("에러 코드 카테고리별 검증") {
        `when`("인증 관련 에러 코드들을 확인하면") {
            val authErrors = ErrorCode.getByPrefix("AUTH_")

            then("모든 인증 에러 코드가 포함된다") {
                authErrors shouldContain ErrorCode.AUTH_UNAUTHENTICATED
                authErrors shouldContain ErrorCode.AUTH_UNAUTHORIZED
                authErrors shouldContain ErrorCode.AUTH_INVALID_CREDENTIALS
                authErrors shouldContain ErrorCode.AUTH_TOKEN_EXPIRED
                authErrors shouldContain ErrorCode.AUTH_INVALID_TOKEN
            }
        }

        `when`("엔티티 관련 에러 코드들을 확인하면") {
            val entityErrors = ErrorCode.getByPrefix("ENTITY_")

            then("모든 엔티티 에러 코드가 포함된다") {
                entityErrors shouldContain ErrorCode.ENTITY_NOT_FOUND
                entityErrors shouldContain ErrorCode.ENTITY_ALREADY_EXISTS
                entityErrors shouldContain ErrorCode.ENTITY_CREATION_FAILED
                entityErrors shouldContain ErrorCode.ENTITY_UPDATE_FAILED
                entityErrors shouldContain ErrorCode.ENTITY_DELETE_FAILED
            }
        }

        `when`("검증 관련 에러 코드들을 확인하면") {
            val validationErrors = ErrorCode.getByPrefix("VALIDATION_")

            then("모든 검증 에러 코드가 포함된다") {
                validationErrors shouldContain ErrorCode.VALIDATION_INVALID_EMAIL
                validationErrors shouldContain ErrorCode.VALIDATION_INVALID_PASSWORD
                validationErrors shouldContain ErrorCode.VALIDATION_TITLE_TOO_LONG
                validationErrors shouldContain ErrorCode.VALIDATION_CONTENT_TOO_LONG
                validationErrors shouldContain ErrorCode.VALIDATION_TOO_MANY_TAGS
            }
        }
    }

    given("에러 코드의 고유성 검증") {
        `when`("모든 에러 코드를 확인하면") {
            val allCodes = ErrorCode.entries.map { it.code }
            val uniqueCodes = allCodes.toSet()

            then("모든 코드가 고유하다") {
                allCodes.size shouldBe uniqueCodes.size
            }
        }

        `when`("두 개의 서로 다른 ErrorCode를 비교하면") {
            val error1 = ErrorCode.USER_NOT_FOUND
            val error2 = ErrorCode.POST_NOT_FOUND

            then("서로 다른 객체다") {
                error1 shouldNotBe error2
                error1.code shouldNotBe error2.code
                error1.description shouldNotBe error2.description
            }
        }
    }
})
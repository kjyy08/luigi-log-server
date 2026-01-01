package cloud.luigi99.blog.content.domain.post.exception

import cloud.luigi99.blog.common.exception.ErrorCode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * Post 도메인 예외 테스트
 */
class PostExceptionTest :
    BehaviorSpec({

        Given("PostNotFoundException이 주어졌을 때") {
            When("예외를 생성하면") {
                val exception = PostNotFoundException()

                Then("올바른 에러 코드를 가진다") {
                    exception.errorCode shouldBe ErrorCode.POST_NOT_FOUND
                }

                Then("기본 메시지를 가진다") {
                    exception.message shouldBe "게시글을 찾을 수 없습니다."
                }
            }

            When("커스텀 메시지로 예외를 생성하면") {
                val customMessage = "해당 ID의 게시글을 찾을 수 없습니다"
                val exception = PostNotFoundException(customMessage)

                Then("커스텀 메시지를 가진다") {
                    exception.message shouldBe customMessage
                }
            }
        }

        Given("DuplicateSlugException이 주어졌을 때") {
            When("예외를 생성하면") {
                val exception = DuplicateSlugException()

                Then("올바른 에러 코드를 가진다") {
                    exception.errorCode shouldBe ErrorCode.SLUG_ALREADY_EXISTS
                }

                Then("기본 메시지를 가진다") {
                    exception.message shouldBe "이미 존재하는 슬러그입니다."
                }
            }

            When("커스텀 메시지로 예외를 생성하면") {
                val customMessage = "슬러그 'test-post'는 이미 사용 중입니다"
                val exception = DuplicateSlugException(customMessage)

                Then("커스텀 메시지를 가진다") {
                    exception.message shouldBe customMessage
                }
            }
        }
    })

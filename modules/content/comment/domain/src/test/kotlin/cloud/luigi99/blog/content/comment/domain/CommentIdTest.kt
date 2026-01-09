package cloud.luigi99.blog.content.comment.domain

import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class CommentIdTest :
    BehaviorSpec({

        Given("새로운 댓글 식별자가 필요할 때") {

            When("고유한 식별자를 생성하면") {
                val commentId = CommentId.generate()

                Then("UUID 기반의 고유 식별자가 생성된다") {
                    commentId shouldNotBe null
                    commentId.value shouldNotBe null
                }
            }
        }

        Given("저장된 댓글의 식별자 문자열이 주어졌을 때") {
            val uuidString = "123e4567-e89b-12d3-a456-426614174000"

            When("문자열로부터 식별자를 복원하면") {
                val commentId = CommentId.from(uuidString)

                Then("원본 UUID와 동일한 식별자가 생성된다") {
                    commentId.value shouldBe UUID.fromString(uuidString)
                }
            }
        }

        Given("시스템 내에서 동일한 댓글을 가리키는 두 식별자가 있을 때") {
            val uuid = UUID.randomUUID()
            val commentId1 = CommentId(uuid)
            val commentId2 = CommentId(uuid)

            Then("두 식별자는 논리적으로 동일한 것으로 취급한다") {
                commentId1 shouldBe commentId2
            }

            Then("해시코드도 동일하다") {
                commentId1.hashCode() shouldBe commentId2.hashCode()
            }
        }
    })

package cloud.luigi99.blog.content.comment.domain

import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class CommentContentTest :
    BehaviorSpec({

        Given("사용자가 유효한 댓글 내용을 입력했을 때") {
            val validContent = "좋은 글 감사합니다!"

            When("댓글 내용 객체를 생성하면") {
                val content = CommentContent(validContent)

                Then("댓글 내용이 올바르게 저장된다") {
                    content.value shouldBe validContent
                }
            }
        }

        Given("사용자가 댓글 내용을 입력하지 않았을 때") {
            val emptyContent = ""

            When("댓글 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        CommentContent(emptyContent)
                    }

                Then("필수 입력 항목이므로 생성이 거절된다") {
                    exception.message shouldContain "Comment content cannot be blank"
                }
            }
        }

        Given("사용자가 공백만으로 이루어진 댓글 내용을 입력했을 때") {
            val blankContent = "   "

            When("댓글 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        CommentContent(blankContent)
                    }

                Then("유효하지 않은 형식이므로 생성이 거절된다") {
                    exception.message shouldContain "Comment content cannot be blank"
                }
            }
        }

        Given("사용자가 최대 길이를 초과하는 댓글 내용을 입력했을 때") {
            val tooLongContent = "a".repeat(1001)

            When("댓글 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        CommentContent(tooLongContent)
                    }

                Then("길이 제한을 초과하여 생성이 거절된다") {
                    exception.message shouldContain "Comment content must be between 1 and 1000 characters"
                }
            }
        }

        Given("사용자가 경계값인 1000자 댓글을 입력했을 때") {
            val maxContent = "a".repeat(1000)

            When("댓글 내용 객체를 생성하면") {
                val content = CommentContent(maxContent)

                Then("정상적으로 생성된다") {
                    content shouldNotBe null
                    content.value.length shouldBe 1000
                }
            }
        }

        Given("시스템 내에서 동일한 댓글 내용을 가진 두 객체가 있을 때") {
            val text = "좋은 글 감사합니다!"
            val content1 = CommentContent(text)
            val content2 = CommentContent(text)

            Then("두 객체는 논리적으로 동일한 것으로 취급한다") {
                content1 shouldBe content2
            }

            Then("해시코드도 동일하다") {
                content1.hashCode() shouldBe content2.hashCode()
            }
        }
    })

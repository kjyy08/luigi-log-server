package cloud.luigi99.blog.content.comment.domain

import cloud.luigi99.blog.content.comment.domain.exception.UnauthorizedCommentAccessException
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class CommentTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(cloud.luigi99.blog.common.domain.event.EventManager)
            every { cloud.luigi99.blog.common.domain.event.EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("사용자가 게시글에 댓글을 작성하려고 할 때") {
            val postId = PostId.generate()
            val authorId = MemberId.generate()
            val content = CommentContent("좋은 글 감사합니다!")

            When("댓글 생성을 요청하면") {
                val comment = Comment.create(postId, authorId, content)

                Then("댓글이 생성되어 고유 식별자가 부여된다") {
                    comment.entityId shouldNotBe null
                }

                Then("작성한 내용으로 댓글이 설정된다") {
                    comment.content shouldBe content
                    comment.postId shouldBe postId
                    comment.authorId shouldBe authorId
                }
            }
        }

        Given("댓글 작성자가 자신의 댓글 내용을 수정하려고 할 때") {
            val postId = PostId.generate()
            val authorId = MemberId.generate()
            val originalContent = CommentContent("좋은 글 감사합니다!")
            val originalComment = Comment.create(postId, authorId, originalContent)
            val newContent = CommentContent("정말 유익한 글이네요!")

            When("새로운 내용으로 수정을 요청하면") {
                val updatedComment = originalComment.updateContent(newContent, authorId)

                Then("원본 댓글 객체 자체가 변경된다") {
                    originalComment.content shouldNotBe originalContent
                    originalComment.content shouldBe newContent
                }

                Then("수정된 내용을 가진 현재 댓글 객체가 반환된다") {
                    updatedComment shouldBeSameInstanceAs originalComment
                    updatedComment.content shouldBe newContent
                }

                Then("댓글 식별자는 동일하게 유지된다") {
                    updatedComment.entityId shouldBe originalComment.entityId
                }
            }
        }

        Given("다른 사용자가 타인의 댓글을 수정하려고 할 때") {
            val postId = PostId.generate()
            val originalAuthorId = MemberId.generate()
            val otherUserId = MemberId.generate()
            val content = CommentContent("좋은 글 감사합니다!")
            val comment = Comment.create(postId, originalAuthorId, content)
            val newContent = CommentContent("악의적인 수정 시도")

            When("수정을 시도하면") {

                Then("작성자가 아니므로 수정이 거절된다") {
                    shouldThrow<UnauthorizedCommentAccessException> {
                        comment.updateContent(newContent, otherUserId)
                    }
                }
            }
        }

        Given("작성자가 자신이 작성한 댓글인지 확인하려고 할 때") {
            val authorId = MemberId.generate()
            val postId = PostId.generate()
            val content = CommentContent("좋은 글 감사합니다!")
            val comment = Comment.create(postId, authorId, content)

            When("자신의 ID로 검증하면") {
                val result = comment.verifyAuthor(authorId)

                Then("작성자임이 확인된다") {
                    result shouldBe true
                }
            }

            When("다른 사용자의 ID로 검증하면") {
                val otherId = MemberId.generate()
                val result = comment.verifyAuthor(otherId)

                Then("작성자가 아님이 확인된다") {
                    result shouldBe false
                }
            }
        }

        Given("과거에 작성된 댓글 정보를 데이터베이스에서 불러왔을 때") {
            val commentId = CommentId.generate()
            val postId = PostId.generate()
            val authorId = MemberId.generate()
            val content = CommentContent("좋은 글 감사합니다!")
            val createdAt =
                java.time.LocalDateTime
                    .now()
                    .minusDays(7)
            val updatedAt =
                java.time.LocalDateTime
                    .now()
                    .minusDays(1)

            When("비즈니스 객체로 재구성하면") {
                val comment =
                    Comment.from(
                        entityId = commentId,
                        postId = postId,
                        authorId = authorId,
                        content = content,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )

                Then("모든 속성이 저장된 상태 그대로 복원된다") {
                    comment.entityId shouldBe commentId
                    comment.postId shouldBe postId
                    comment.authorId shouldBe authorId
                    comment.content shouldBe content
                    comment.createdAt shouldBe createdAt
                    comment.updatedAt shouldBe updatedAt
                }
            }
        }
    })

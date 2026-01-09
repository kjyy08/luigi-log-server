package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.comment.application.port.`in`.command.UpdateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.comment.domain.exception.CommentNotFoundException
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.time.LocalDateTime

class UpdateCommentServiceTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("사용자가 댓글을 수정하려고 할 때") {
            val commentRepository = mockk<CommentRepository>()
            val memberClient = mockk<MemberClient>()
            val service = UpdateCommentService(commentRepository, memberClient)

            val commentId = CommentId.generate()
            val authorId = MemberId.generate()
            val postId = PostId.generate()
            val content = "기존 댓글 내용"
            val newContent = "수정된 댓글 내용"
            val now = LocalDateTime.now()

            val command =
                UpdateCommentUseCase.Command(
                    commentId = commentId.value.toString(),
                    authorId = authorId.value.toString(),
                    content = newContent,
                )

            val existingComment =
                Comment.from(
                    entityId = commentId,
                    postId = postId,
                    authorId = authorId,
                    content = CommentContent(content),
                    createdAt = now,
                    updatedAt = now,
                )

            When("정상적으로 댓글 수정을 요청하면") {
                every { commentRepository.findById(commentId) } returns existingComment
                every { commentRepository.save(any()) } answers {
                    firstArg<Comment>()
                }
                every { memberClient.getAuthor(authorId.value.toString()) } returns
                    MemberClient.Author(
                        memberId = authorId.value.toString(),
                        nickname = "Luigi99",
                        profileImageUrl = null,
                        username = "luigi99",
                    )

                val response = service.execute(command)

                Then("수정된 댓글이 저장소에 저장된다") {
                    verify(exactly = 1) { commentRepository.save(any()) }
                }

                Then("수정된 내용이 반환된다") {
                    response.content shouldBe newContent
                    response.author.memberId shouldBe authorId.value.toString()
                }

                Then("수정 시각이 반환된다") {
                    response.updatedAt shouldNotBe null
                }
            }

            When("수정하려는 댓글이 존재하지 않으면") {
                every { commentRepository.findById(commentId) } returns null

                Then("CommentNotFoundException이 발생한다") {
                    shouldThrow<CommentNotFoundException> { service.execute(command) }
                }
            }

            When("댓글 작성자가 아닌 사용자가 수정을 요청하면") {
                val otherAuthorId = MemberId.generate()
                val unauthorizedCommand = command.copy(authorId = otherAuthorId.value.toString())

                every { commentRepository.findById(commentId) } returns existingComment

                Then("UnauthorizedCommentAccessException이 발생한다") {
                    shouldThrow<UnauthorizedCommentAccessException> {
                        service.execute(unauthorizedCommand)
                    }
                }
            }
        }
    })

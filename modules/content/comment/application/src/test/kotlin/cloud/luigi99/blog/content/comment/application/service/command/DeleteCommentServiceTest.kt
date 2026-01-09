package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.DeleteCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.domain.exception.CommentNotFoundException
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class DeleteCommentServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(cloud.luigi99.blog.common.domain.event.EventManager)
            every { cloud.luigi99.blog.common.domain.event.EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("댓글 작성자가 자신의 댓글을 삭제하려고 할 때") {
            val commentRepository = mockk<CommentRepository>()
            val service = DeleteCommentService(commentRepository)

            val commentId = CommentId.generate()
            val authorId = MemberId.generate()
            val postId = PostId.generate()

            val existingComment = Comment.create(postId, authorId, CommentContent("댓글 내용"))

            val command =
                DeleteCommentUseCase.Command(
                    commentId = commentId.value.toString(),
                    authorId = authorId.value.toString(),
                )

            When("댓글 삭제를 요청하면") {
                every { commentRepository.findById(commentId) } returns existingComment
                every { commentRepository.deleteById(commentId) } returns Unit

                service.execute(command)

                Then("댓글이 저장소에서 삭제된다") {
                    verify(exactly = 1) { commentRepository.deleteById(commentId) }
                }
            }
        }

        Given("존재하지 않는 댓글을 삭제하려고 할 때") {
            val commentRepository = mockk<CommentRepository>()
            val service = DeleteCommentService(commentRepository)

            val commentId = CommentId.generate()
            val authorId = MemberId.generate()

            val command =
                DeleteCommentUseCase.Command(
                    commentId = commentId.value.toString(),
                    authorId = authorId.value.toString(),
                )

            When("댓글 삭제를 시도하면") {
                every { commentRepository.findById(commentId) } returns null

                Then("댓글을 찾을 수 없어 삭제가 거절된다") {
                    shouldThrow<CommentNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }
    })

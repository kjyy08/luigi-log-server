package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.CreateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.post.domain.post.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class CreateCommentServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(cloud.luigi99.blog.common.domain.event.EventManager)
            every { cloud.luigi99.blog.common.domain.event.EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("사용자가 게시글에 댓글을 작성하려고 할 때") {
            val commentRepository = mockk<CommentRepository>()
            val memberClient = mockk<MemberClient>()
            val service = CreateCommentService(commentRepository, memberClient)

            val postId = PostId.generate()
            val authorId = MemberId.generate()
            val content = "좋은 글 감사합니다!"

            val command =
                CreateCommentUseCase.Command(
                    postId = postId.value.toString(),
                    authorId = authorId.value.toString(),
                    content = content,
                )

            val savedComment =
                Comment.create(
                    postId = postId,
                    authorId = authorId,
                    content = CommentContent(content),
                )

            When("댓글 생성을 요청하면") {
                every { commentRepository.save(any()) } returns savedComment
                every { memberClient.getAuthor(authorId.value.toString()) } returns
                    MemberClient.Author(
                        memberId = authorId.value.toString(),
                        nickname = "Luigi99",
                        profileImageUrl = null,
                        username = "luigi99",
                    )

                val response = service.execute(command)

                Then("댓글이 저장소에 저장된다") {
                    verify(exactly = 1) { commentRepository.save(any()) }
                }

                Then("생성된 댓글의 식별자가 반환된다") {
                    response.commentId shouldNotBe null
                }

                Then("요청한 내용으로 댓글이 생성된다") {
                    response.content shouldBe content
                    response.postId shouldBe postId.value.toString()
                    response.author.memberId shouldBe authorId.value.toString()
                    response.author.nickname shouldBe "Luigi99"
                }

                Then("생성 시각이 반환된다") {
                    response.createdAt shouldNotBe null
                }
            }
        }
    })

package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.command.DeletePostUseCase
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.exception.UnauthorizedPostAccessException
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

/**
 * DeletePostService 테스트
 */
class DeletePostServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("게시글이 존재하고 작성자가 삭제를 요청하는 상황에서") {
            val postRepository = mockk<PostRepository>()
            val service = DeletePostService(postRepository)
            val memberId = MemberId.generate()
            val postId = PostId.generate()

            val post =
                Post.create(
                    memberId = memberId,
                    title = Title("삭제할 글"),
                    slug = Slug("delete-post"),
                    body = Body("내용"),
                    type = ContentType.BLOG,
                )

            every { postRepository.findById(postId) } returns post

            When("정상적으로 삭제를 시도하면") {
                val command =
                    DeletePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                    )

                every { postRepository.deleteById(any()) } just Runs

                service.execute(command)

                Then("게시글 삭제 로직이 실행된다") {
                    verify(exactly = 1) { postRepository.deleteById(any()) }
                }
            }

            When("작성자가 아닌 다른 사용자가 삭제를 시도하면") {
                val otherMemberId = MemberId.generate()
                val command =
                    DeletePostUseCase.Command(
                        memberId = otherMemberId.value.toString(),
                        postId = postId.value.toString(),
                    )

                Then("권한 없음 예외(UnauthorizedPostAccessException)가 발생하여 삭제가 거부된다") {
                    shouldThrow<UnauthorizedPostAccessException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("존재하지 않는 게시글을 삭제하려는 상황에서") {
            val postRepository = mockk<PostRepository>()
            val service = DeletePostService(postRepository)
            val memberId = MemberId.generate()
            val postId = PostId.generate()

            When("삭제 요청을 보내면") {
                val command =
                    DeletePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                    )

                every { postRepository.findById(postId) } returns null

                Then("게시글을 찾을 수 없다는 예외(PostNotFoundException)가 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }
    })

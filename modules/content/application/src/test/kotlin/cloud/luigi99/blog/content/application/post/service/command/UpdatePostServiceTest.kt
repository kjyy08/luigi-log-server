package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.command.UpdatePostUseCase
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
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.UUID

/**
 * UpdatePostService 테스트
 */
class UpdatePostServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("글을 수정할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val memberId = MemberId.generate()

            When("작성자가 제목과 본문을 수정하면") {
                val postId = PostId.generate()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                        title = "수정된 제목",
                        body = "수정된 본문",
                    )

                val originalPost =
                    Post.create(
                        memberId = memberId,
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.BLOG,
                    )

                val updatedPost = originalPost.update(Title("수정된 제목"), Body("수정된 본문"))

                every { postRepository.findById(postId) } returns originalPost
                every { postRepository.save(any()) } returns updatedPost

                val response = service.execute(command)

                Then("수정된 제목이 반환된다") {
                    response.title shouldBe "수정된 제목"
                }

                Then("수정된 본문이 반환된다") {
                    response.body shouldBe "수정된 본문"
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { postRepository.save(any()) }
                }
            }
        }

        Given("글의 상태를 PUBLISHED로 변경할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val memberId = MemberId.generate()

            When("작성자가 status를 PUBLISHED로 수정하면") {
                val postId = PostId.generate()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                        title = null,
                        body = null,
                        status = "PUBLISHED",
                    )

                val originalPost =
                    Post.create(
                        memberId = memberId,
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.BLOG,
                    )

                val publishedPost = originalPost.publish()

                every { postRepository.findById(postId) } returns originalPost
                every { postRepository.save(any()) } returns publishedPost

                val response = service.execute(command)

                Then("상태가 PUBLISHED로 변경된다") {
                    response.status shouldBe "PUBLISHED"
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { postRepository.save(any()) }
                }
            }
        }

        Given("글의 상태를 ARCHIVED로 변경할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val memberId = MemberId.generate()

            When("작성자가 status를 ARCHIVED로 수정하면") {
                val postId = PostId.generate()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                        title = null,
                        body = null,
                        status = "ARCHIVED",
                    )

                val originalPost =
                    Post.create(
                        memberId = memberId,
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.BLOG,
                    )

                val archivedPost = originalPost.archive()

                every { postRepository.findById(postId) } returns originalPost
                every { postRepository.save(any()) } returns archivedPost

                val response = service.execute(command)

                Then("상태가 ARCHIVED로 변경된다") {
                    response.status shouldBe "ARCHIVED"
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { postRepository.save(any()) }
                }
            }
        }

        Given("제목, 본문, 상태를 동시에 수정할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val memberId = MemberId.generate()

            When("작성자가 모든 필드를 수정하면") {
                val postId = PostId.generate()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.value.toString(),
                        title = "수정된 제목",
                        body = "수정된 본문",
                        status = "PUBLISHED",
                    )

                val originalPost =
                    Post.create(
                        memberId = memberId,
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.BLOG,
                    )

                val updatedPost = originalPost.update(Title("수정된 제목"), Body("수정된 본문"))
                val publishedPost = updatedPost.publish()

                every { postRepository.findById(postId) } returns originalPost
                every { postRepository.save(any()) } returns publishedPost

                val response = service.execute(command)

                Then("제목이 변경된다") {
                    response.title shouldBe "수정된 제목"
                }

                Then("본문이 변경된다") {
                    response.body shouldBe "수정된 본문"
                }

                Then("상태가 PUBLISHED로 변경된다") {
                    response.status shouldBe "PUBLISHED"
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { postRepository.save(any()) }
                }
            }
        }

        Given("존재하지 않는 글을 수정하려고 할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val memberId = MemberId.generate()

            When("없는 Post ID로 수정하면") {
                val postId = UUID.randomUUID()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = memberId.value.toString(),
                        postId = postId.toString(),
                        title = "수정된 제목",
                        body = "수정된 본문",
                    )

                every { postRepository.findById(PostId(postId)) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("다른 사용자의 글을 수정하려고 할 때") {
            val postRepository = mockk<PostRepository>()
            val service = UpdatePostService(postRepository)
            val authorId = MemberId.generate()
            val otherMemberId = MemberId.generate()

            When("작성자가 아닌 사용자가 수정하려고 하면") {
                val postId = PostId.generate()
                val command =
                    UpdatePostUseCase.Command(
                        memberId = otherMemberId.value.toString(),
                        postId = postId.value.toString(),
                        title = "수정 시도",
                        body = "수정 시도",
                    )

                val originalPost =
                    Post.create(
                        memberId = authorId,
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.BLOG,
                    )

                every { postRepository.findById(postId) } returns originalPost

                Then("UnauthorizedPostAccessException이 발생한다") {
                    shouldThrow<UnauthorizedPostAccessException> {
                        service.execute(command)
                    }
                }
            }
        }
    })

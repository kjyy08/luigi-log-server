package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.domain.exception.PostNotFoundException
import cloud.luigi99.blog.content.post.domain.model.Post
import cloud.luigi99.blog.content.post.domain.vo.Body
import cloud.luigi99.blog.content.post.domain.vo.ContentType
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.Slug
import cloud.luigi99.blog.content.post.domain.vo.Title
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
 * GetPostByIdService 테스트
 */
class GetPostByIdServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("ID로 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostByIdService(postRepository, memberClient)

            When("존재하는 Post ID로 조회하면") {
                val post =
                    Post.create(
                        memberId = MemberId.generate(),
                        title = Title("테스트 글"),
                        slug = Slug("test-post"),
                        body = Body("테스트 내용"),
                        type = ContentType.BLOG,
                    )
                val postId = post.entityId
                val query = GetPostByIdUseCase.Query(postId = postId.value.toString())

                every { postRepository.findById(postId) } returns post

                every { postRepository.incrementViewCount(postId) } returns 1
                every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
                every { memberClient.getAuthor(any()) } returns
                    MemberClient.Author(
                        memberId =
                            post.memberId.value
                                .toString(),
                        nickname = "TestUser",
                        profileImageUrl = null,
                        username = "test_user",
                    )

                val response = service.execute(query)

                Then("제목이 반환된다") {
                    response.title shouldBe "테스트 글"
                }

                Then("본문이 반환된다") {
                    response.body shouldBe "테스트 내용"
                }

                Then("조회수는 full aggregate save 없이 atomic increment로 증가한다") {
                    response.viewCount shouldBe 1
                    verify(exactly = 1) { postRepository.incrementViewCount(postId) }
                    verify(exactly = 0) { postRepository.save(any()) }
                }
            }
        }

        Given("존재하지 않는 ID로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostByIdService(postRepository, memberClient)

            When("없는 Post ID로 조회하면") {
                val postId = UUID.randomUUID()
                val query = GetPostByIdUseCase.Query(postId = postId.toString())

                every { postRepository.findById(PostId(postId)) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }
    })

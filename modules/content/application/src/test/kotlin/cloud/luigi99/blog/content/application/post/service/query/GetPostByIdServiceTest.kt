package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
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
                val postId = PostId.generate()
                val query = GetPostByIdUseCase.Query(postId = postId.value.toString())

                val post =
                    Post.create(
                        memberId = MemberId.generate(),
                        title = Title("테스트 글"),
                        slug = Slug("test-post"),
                        body = Body("테스트 내용"),
                        type = ContentType.BLOG,
                    )

                every { postRepository.findById(postId) } returns post
                every { memberClient.getAuthor(any()) } returns
                    MemberClient.Author(
                        memberId =
                            post.memberId.value
                                .toString(),
                        nickname = "TestUser",
                        profileImageUrl = null,
                    )

                val response = service.execute(query)

                Then("제목이 반환된다") {
                    response.title shouldBe "테스트 글"
                }

                Then("본문이 반환된다") {
                    response.body shouldBe "테스트 내용"
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

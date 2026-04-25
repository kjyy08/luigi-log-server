package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.application.port.out.PostViewCountDeduplicationPort
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

        Given("ID로 발행 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostByIdService(postRepository, memberClient, deduplicationPort)
            val post = publishedPost()
            val postId = post.entityId
            val query = GetPostByIdUseCase.Query(postId = postId.value.toString(), visitorKey = "visitor-key")

            every { postRepository.findById(postId) } returns post
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOf(post)

            When("Redis dedupe가 최초 조회로 판단하면") {
                every { deduplicationPort.isUniqueView(postId, "visitor-key") } returns true
                every { postRepository.incrementViewCount(postId) } returns 1

                val response = service.execute(query)

                Then("조회수를 atomic increment하고 응답 조회수를 1 보정한다") {
                    response.viewCount shouldBe 1
                    verify(exactly = 1) { postRepository.incrementViewCount(postId) }
                    verify(exactly = 0) { postRepository.save(any()) }
                }
            }
        }

        Given("ID로 발행 글을 중복 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostByIdService(postRepository, memberClient, deduplicationPort)
            val post = publishedPost()
            val postId = post.entityId
            val query = GetPostByIdUseCase.Query(postId = postId.value.toString(), visitorKey = "visitor-key")

            every { postRepository.findById(postId) } returns post
            every { deduplicationPort.isUniqueView(postId, "visitor-key") } returns false
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOf(post)

            When("Redis dedupe가 중복 조회로 판단하면") {
                val response = service.execute(query)

                Then("조회수를 증가하지 않고 기존 DB 조회수를 반환한다") {
                    response.viewCount shouldBe 0
                    verify(exactly = 0) { postRepository.incrementViewCount(any()) }
                }
            }
        }

        Given("조회수 dedupe 저장소 장애가 발생할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostByIdService(postRepository, memberClient, deduplicationPort)
            val post = publishedPost()
            val postId = post.entityId
            val query = GetPostByIdUseCase.Query(postId = postId.value.toString(), visitorKey = "visitor-key")

            every { postRepository.findById(postId) } returns post
            every { deduplicationPort.isUniqueView(postId, "visitor-key") } throws IllegalStateException("redis down")
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOf(post)

            When("상세 글을 조회하면") {
                val response = service.execute(query)

                Then("조회는 성공하고 조회수 증가는 skip한다") {
                    response.title shouldBe "테스트 글"
                    response.viewCount shouldBe 0
                    verify(exactly = 0) { postRepository.incrementViewCount(any()) }
                }
            }
        }

        Given("존재하지 않는 ID로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostByIdService(postRepository, memberClient, deduplicationPort)

            When("없는 Post ID로 조회하면") {
                val postId = UUID.randomUUID()
                val query = GetPostByIdUseCase.Query(postId = postId.toString(), visitorKey = "visitor-key")

                every { postRepository.findById(PostId(postId)) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }
    })

private fun publishedPost(): Post =
    Post.create(
        memberId = MemberId.generate(),
        title = Title("테스트 글"),
        slug = Slug("test-post"),
        body = Body("테스트 내용"),
        type = ContentType.BLOG,
    ).publish()

private fun authorOf(post: Post): MemberClient.Author =
    MemberClient.Author(
        memberId = post.memberId.value.toString(),
        nickname = "TestUser",
        profileImageUrl = null,
        username = "test_user",
    )

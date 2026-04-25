package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.application.port.out.PostViewCountDeduplicationPort
import cloud.luigi99.blog.content.post.domain.exception.PostNotFoundException
import cloud.luigi99.blog.content.post.domain.model.Post
import cloud.luigi99.blog.content.post.domain.vo.Body
import cloud.luigi99.blog.content.post.domain.vo.ContentType
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

/**
 * GetPostBySlugService 테스트
 */
class GetPostBySlugServiceTest :
    BehaviorSpec({
        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("Username과 Slug로 발행 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val memberId = MemberId.generate()
            val post = publishedSlugPost(memberId)
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

            When("Redis dedupe가 최초 조회로 판단하면") {
                every { deduplicationPort.isUniqueView(post.entityId, "visitor-key") } returns true
                every { postRepository.incrementViewCount(post.entityId) } returns 1

                val response = service.execute(query)

                Then("조회수를 atomic increment하고 응답 조회수를 1 보정한다") {
                    response.title shouldBe "테스트 글"
                    response.slug shouldBe "test-post"
                    response.viewCount shouldBe 1
                    verify(exactly = 1) { postRepository.incrementViewCount(post.entityId) }
                    verify(exactly = 0) { postRepository.save(any()) }
                }
            }
        }

        Given("Username과 Slug로 발행 글을 중복 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val post = publishedSlugPost(MemberId.generate())
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every { deduplicationPort.isUniqueView(post.entityId, "visitor-key") } returns false
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

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
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val post = publishedSlugPost(MemberId.generate())
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every {
                deduplicationPort.isUniqueView(post.entityId, "visitor-key")
            } throws IllegalStateException("redis down")
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

            When("상세 글을 조회하면") {
                val response = service.execute(query)

                Then("조회는 성공하고 조회수 증가는 skip한다") {
                    response.body shouldBe "테스트 내용"
                    response.viewCount shouldBe 0
                    verify(exactly = 0) { postRepository.incrementViewCount(any()) }
                }
            }
        }

        Given("존재하지 않는 Username으로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)

            When("존재하지 않는 Username으로 조회하면") {
                val query =
                    GetPostBySlugUseCase.Query(
                        username = "nonexistent",
                        slug = "test-post",
                        visitorKey = "visitor-key",
                    )

                every { postRepository.findByUsernameAndSlug("nonexistent", Slug("test-post")) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }

        Given("존재하지 않는 Slug로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)

            When("존재하는 Username이지만 없는 Slug로 조회하면") {
                val query =
                    GetPostBySlugUseCase.Query(
                        username = "testuser",
                        slug = "non-existent",
                        visitorKey = "visitor-key",
                    )

                every { postRepository.findByUsernameAndSlug("testuser", Slug("non-existent")) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }
    })

private fun publishedSlugPost(memberId: MemberId): Post =
    Post
        .create(
            memberId = memberId,
            title = Title("테스트 글"),
            slug = Slug("test-post"),
            body = Body("테스트 내용"),
            type = ContentType.BLOG,
        )
        .publish()

private fun authorOfSlugPost(post: Post): MemberClient.Author =
    MemberClient.Author(
        memberId =
            post.memberId.value
                .toString(),
        nickname = "TestUser",
        profileImageUrl = null,
        username = "test_user",
    )

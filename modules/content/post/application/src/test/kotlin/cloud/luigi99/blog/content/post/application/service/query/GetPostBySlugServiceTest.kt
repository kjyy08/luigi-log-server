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
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.PostStatus
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
import java.time.LocalDateTime

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
            every { postRepository.findPreviousPublishedPost(post) } returns null
            every { postRepository.findNextPublishedPost(post) } returns null
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
            every { postRepository.findPreviousPublishedPost(post) } returns null
            every { postRepository.findNextPublishedPost(post) } returns null
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
            every { postRepository.findPreviousPublishedPost(post) } returns null
            every { postRepository.findNextPublishedPost(post) } returns null
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

        Given("Username과 Slug로 발행 글의 인접 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val memberId = MemberId.generate()
            val post = publishedSlugPost(memberId)
            val previous =
                postFromPersistence(
                    memberId = memberId,
                    title = "이전 글",
                    slug = "older-post",
                    createdAt = LocalDateTime.of(2026, 4, 27, 12, 0),
                )
            val next =
                postFromPersistence(
                    memberId = memberId,
                    title = "다음 글",
                    slug = "newer-post",
                    createdAt = LocalDateTime.of(2026, 4, 29, 12, 0),
                )
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every { deduplicationPort.isUniqueView(post.entityId, "visitor-key") } returns false
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { postRepository.findPreviousPublishedPost(post) } returns previous
            every { postRepository.findNextPublishedPost(post) } returns next
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

            When("현재 글이 목록 중간에 있으면") {
                val response = service.execute(query)

                Then("previousPost와 nextPost를 모두 매핑한다") {
                    response.previousPost?.postId shouldBe previous.entityId.value.toString()
                    response.previousPost?.title shouldBe "이전 글"
                    response.previousPost?.slug shouldBe "older-post"
                    response.previousPost?.createdAt shouldBe previous.createdAt
                    response.nextPost?.postId shouldBe next.entityId.value.toString()
                    response.nextPost?.title shouldBe "다음 글"
                    response.nextPost?.slug shouldBe "newer-post"
                    response.nextPost?.createdAt shouldBe next.createdAt
                }
            }
        }

        Given("Username과 Slug로 발행 글의 가장자리 인접 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val memberId = MemberId.generate()
            val post = publishedSlugPost(memberId)
            val next =
                postFromPersistence(
                    memberId = memberId,
                    title = "최신 글",
                    slug = "newest-post",
                    createdAt = LocalDateTime.of(2026, 4, 30, 12, 0),
                )
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every { deduplicationPort.isUniqueView(post.entityId, "visitor-key") } returns false
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { postRepository.findPreviousPublishedPost(post) } returns null
            every { postRepository.findNextPublishedPost(post) } returns next
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

            When("더 오래된 글이 없으면") {
                val response = service.execute(query)

                Then("previousPost는 null이고 nextPost만 매핑한다") {
                    response.previousPost shouldBe null
                    response.nextPost?.postId shouldBe next.entityId.value.toString()
                    response.nextPost?.title shouldBe "최신 글"
                    response.nextPost?.slug shouldBe "newest-post"
                    response.nextPost?.createdAt shouldBe next.createdAt
                }
            }
        }

        Given("Username과 Slug로 미발행 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val deduplicationPort = mockk<PostViewCountDeduplicationPort>()
            val service = GetPostBySlugService(postRepository, memberClient, deduplicationPort)
            val post = draftSlugPost(MemberId.generate())
            val query =
                GetPostBySlugUseCase.Query(
                    username = "testuser",
                    slug = "test-post",
                    visitorKey = "visitor-key",
                )

            every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
            every { postRepository.countCommentsByPostIds(any()) } returns emptyMap()
            every { memberClient.getAuthor(any()) } returns authorOfSlugPost(post)

            When("현재 글이 DRAFT이면") {
                val response = service.execute(query)

                Then("인접 글을 조회하지 않고 null로 응답한다") {
                    response.previousPost shouldBe null
                    response.nextPost shouldBe null
                    verify(exactly = 0) { postRepository.findPreviousPublishedPost(any()) }
                    verify(exactly = 0) { postRepository.findNextPublishedPost(any()) }
                    verify(exactly = 0) { deduplicationPort.isUniqueView(any(), any()) }
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
    Post.create(
        memberId = memberId,
        title = Title("테스트 글"),
        slug = Slug("test-post"),
        body = Body("테스트 내용"),
        type = ContentType.BLOG,
    ).publish()

private fun draftSlugPost(memberId: MemberId): Post =
    Post.create(
        memberId = memberId,
        title = Title("테스트 글"),
        slug = Slug("test-post"),
        body = Body("테스트 내용"),
        type = ContentType.BLOG,
    )

private fun postFromPersistence(
    memberId: MemberId,
    title: String,
    slug: String,
    createdAt: LocalDateTime,
): Post =
    Post.from(
        entityId = PostId.generate(),
        memberId = memberId,
        title = Title(title),
        slug = Slug(slug),
        body = Body("인접 글 내용"),
        type = ContentType.BLOG,
        status = PostStatus.PUBLISHED,
        tags = emptySet(),
        createdAt = createdAt,
        updatedAt = createdAt,
    )

private fun authorOfSlugPost(post: Post): MemberClient.Author =
    MemberClient.Author(
        memberId =
            post.memberId.value
                .toString(),
        nickname = "TestUser",
        profileImageUrl = null,
        username = "test_user",
    )

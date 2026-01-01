package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.DuplicateSlugException
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import java.util.UUID

/**
 * CreatePostService 테스트
 */
class CreatePostServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("블로그 글을 생성할 때") {
            val postRepository = mockk<PostRepository>()
            val service = CreatePostService(postRepository)
            val memberId = UUID.randomUUID().toString()

            When("유효한 memberId, 제목, slug로 생성하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = memberId,
                        title = "테스트 블로그 글",
                        slug = "test-blog-post",
                        body = "# 제목\n본문 내용",
                        type = "BLOG",
                    )

                val savedPostSlot = slot<Post>()
                val savedPost =
                    Post.from(
                        entityId = PostId.generate(),
                        memberId = MemberId.from(memberId),
                        title = Title("테스트 블로그 글"),
                        slug = Slug("test-blog-post"),
                        body = Body("# 제목\n본문 내용"),
                        type = ContentType.BLOG,
                        status = PostStatus.DRAFT,
                        tags = emptySet(),
                        createdAt = null,
                        updatedAt = null,
                    )

                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(memberId),
                        Slug("test-blog-post"),
                    )
                } returns false
                every { postRepository.save(capture(savedPostSlot)) } returns savedPost

                val response = service.execute(command)

                Then("Post ID가 반환된다") {
                    response.postId shouldNotBe null
                }

                Then("memberId가 반환된다") {
                    response.memberId shouldBe memberId
                }

                Then("제목이 반환된다") {
                    response.title shouldBe "테스트 블로그 글"
                }

                Then("본문이 반환된다") {
                    response.body shouldBe "# 제목\n본문 내용"
                }

                Then("타입이 반환된다") {
                    response.type shouldBe "BLOG"
                }

                Then("DRAFT 상태로 생성된다") {
                    response.status shouldBe "DRAFT"
                }

                Then("태그가 빈 Set으로 반환된다") {
                    response.tags shouldBe emptySet()
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { postRepository.save(any()) }
                }
            }
        }

        Given("같은 사용자가 중복된 slug로 글을 생성하려고 할 때") {
            val postRepository = mockk<PostRepository>()
            val service = CreatePostService(postRepository)
            val memberId = UUID.randomUUID().toString()

            When("이미 자신이 사용 중인 slug를 사용하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = memberId,
                        title = "중복 테스트",
                        slug = "duplicate-slug",
                        body = "내용",
                        type = "BLOG",
                    )

                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(memberId),
                        Slug("duplicate-slug"),
                    )
                } returns true

                Then("DuplicateSlugException이 발생한다") {
                    shouldThrow<DuplicateSlugException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("다른 사용자가 동일한 slug를 사용할 때") {
            val postRepository = mockk<PostRepository>()
            val service = CreatePostService(postRepository)
            val member1Id = UUID.randomUUID().toString()
            val member2Id = UUID.randomUUID().toString()

            When("다른 사용자가 이미 사용 중인 slug로 글을 생성하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = member2Id,
                        title = "다른 사용자의 글",
                        slug = "same-slug",
                        body = "내용",
                        type = "BLOG",
                    )

                val savedPost =
                    Post
                        .from(
                            entityId = PostId.generate(),
                            memberId = MemberId.from(member2Id),
                            title = Title("다른 사용자의 글"),
                            slug = Slug("same-slug"),
                            body = Body("내용"),
                            type = ContentType.BLOG,
                            status = PostStatus.DRAFT,
                            tags = emptySet(),
                            createdAt = null,
                            updatedAt = null,
                        )

                // member2는 same-slug를 사용하지 않았음
                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(member2Id),
                        Slug("same-slug"),
                    )
                } returns false
                every { postRepository.save(any()) } returns savedPost

                val response = service.execute(command)

                Then("글이 정상적으로 생성된다") {
                    response.postId shouldNotBe null
                    response.slug shouldBe "same-slug"
                }
            }
        }

        Given("태그와 함께 글을 생성할 때") {
            val postRepository = mockk<PostRepository>()
            val service = CreatePostService(postRepository)
            val memberId = UUID.randomUUID().toString()

            When("여러 태그를 포함하여 생성하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = memberId,
                        title = "태그 테스트",
                        slug = "tagged-post",
                        body = "태그가 있는 글",
                        type = "BLOG",
                        tags = listOf("Kotlin", "Spring", "DDD"),
                    )

                val savedPost =
                    Post
                        .from(
                            entityId = PostId.generate(),
                            memberId = MemberId.from(memberId),
                            title = Title("태그 테스트"),
                            slug = Slug("tagged-post"),
                            body = Body("태그가 있는 글"),
                            type = ContentType.BLOG,
                            status = PostStatus.DRAFT,
                            tags = setOf("Kotlin", "Spring", "DDD"),
                            createdAt = null,
                            updatedAt = null,
                        )

                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(memberId),
                        Slug("tagged-post"),
                    )
                } returns false
                every { postRepository.save(any()) } returns savedPost

                val response = service.execute(command)

                Then("태그가 모두 반환된다") {
                    response.tags shouldBe setOf("Kotlin", "Spring", "DDD")
                }
            }
        }

        Given("여러 타입의 글을 생성할 때") {
            val postRepository = mockk<PostRepository>()
            val service = CreatePostService(postRepository)
            val memberId = UUID.randomUUID().toString()

            When("BLOG 타입으로 생성하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = memberId,
                        title = "블로그 글",
                        slug = "blog-post",
                        body = "블로그 내용",
                        type = "BLOG",
                    )

                val postSlot = slot<Post>()
                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(memberId),
                        Slug("blog-post"),
                    )
                } returns false
                every { postRepository.save(capture(postSlot)) } answers { firstArg() }

                service.execute(command)

                Then("BLOG 타입으로 저장된다") {
                    val post = postSlot.captured
                    post.type shouldBe ContentType.BLOG
                }

                Then("memberId가 설정된다") {
                    val post = postSlot.captured
                    post.memberId shouldBe MemberId.from(memberId)
                }
            }

            When("PORTFOLIO 타입으로 생성하면") {
                val command =
                    CreatePostUseCase.Command(
                        memberId = memberId,
                        title = "포트폴리오",
                        slug = "portfolio",
                        body = "프로젝트 설명",
                        type = "PORTFOLIO",
                    )

                val postSlot = slot<Post>()
                every {
                    postRepository.existsByMemberIdAndSlug(
                        MemberId.from(memberId),
                        Slug("portfolio"),
                    )
                } returns false
                every { postRepository.save(capture(postSlot)) } answers { firstArg() }

                service.execute(command)

                Then("PORTFOLIO 타입으로 저장된다") {
                    val post = postSlot.captured
                    post.type shouldBe ContentType.PORTFOLIO
                }
            }
        }
    })

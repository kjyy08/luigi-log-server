package cloud.luigi99.blog.content.adapter.out.persistence.jpa.post

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.application.port.out.EventContextManager
import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.domain.post.event.PostCreatedEvent
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostId
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.Optional
import java.util.UUID

/**
 * PostRepositoryAdapter 테스트
 */
class PostRepositoryAdapterTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("새로운 Post를 저장하려고 할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val post =
                Post.create(
                    memberId = MemberId.generate(),
                    title = Title("테스트 글"),
                    slug = Slug("test-post"),
                    body = Body("테스트 본문"),
                    type = ContentType.BLOG,
                )

            When("Post를 저장하면") {
                val savedEntity = PostMapper.toEntity(post)
                every { jpaRepository.save(any()) } returns savedEntity
                every { eventContextManager.getDomainEventsAndClear() } returns
                    listOf(
                        PostCreatedEvent(post.entityId, post.slug),
                    )
                every { domainEventPublisher.publish(any()) } just Runs

                val saved = adapter.save(post)

                Then("저장된 Post가 반환되어야 한다") {
                    saved.entityId shouldBe post.entityId
                    saved.title shouldBe post.title
                    saved.slug shouldBe post.slug
                }

                Then("도메인 이벤트가 발행되어야 한다") {
                    verify(exactly = 1) {
                        domainEventPublisher.publish(match { it is PostCreatedEvent })
                    }
                }
            }
        }

        Given("ID로 Post를 조회할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val postId = PostId.generate()
            val entity =
                PostJpaEntity.from(
                    entityId = postId.value,
                    memberId = UUID.randomUUID(),
                    title = "찾을 글",
                    slug = "find-post",
                    body = "본문",
                    type = ContentType.BLOG,
                    status = PostStatus.DRAFT,
                )

            When("존재하는 ID로 조회하면") {
                every { jpaRepository.findById(postId.value) } returns Optional.of(entity)

                val found = adapter.findById(postId)

                Then("Post가 반환되어야 한다") {
                    found shouldNotBe null
                    found?.entityId shouldBe postId
                    found?.title?.value shouldBe "찾을 글"
                }
            }

            When("존재하지 않는 ID로 조회하면") {
                every { jpaRepository.findById(postId.value) } returns Optional.empty()

                val found = adapter.findById(postId)

                Then("null이 반환되어야 한다") {
                    found shouldBe null
                }
            }
        }

        Given("Slug로 Post를 조회할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val slug = Slug("my-post")
            val entity =
                PostJpaEntity.from(
                    entityId = UUID.randomUUID(),
                    memberId = UUID.randomUUID(),
                    title = "내 글",
                    slug = "my-post",
                    body = "본문",
                    type = ContentType.BLOG,
                    status = PostStatus.DRAFT,
                )

            When("존재하는 Slug로 조회하면") {
                every { jpaRepository.findBySlugValue("my-post") } returns entity

                val found = adapter.findBySlug(slug)

                Then("Post가 반환되어야 한다") {
                    found shouldNotBe null
                    found?.slug shouldBe slug
                }
            }

            When("존재하지 않는 Slug로 조회하면") {
                every { jpaRepository.findBySlugValue("non-existent") } returns null

                val found = adapter.findBySlug(Slug("non-existent"))

                Then("null이 반환되어야 한다") {
                    found shouldBe null
                }
            }
        }

        Given("Slug 존재 여부를 확인할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val slug = Slug("existing-post")

            When("존재하는 Slug를 확인하면") {
                every { jpaRepository.existsBySlugValue("existing-post") } returns true

                val exists = adapter.existsBySlug(slug)

                Then("true가 반환되어야 한다") {
                    exists shouldBe true
                }
            }

            When("존재하지 않는 Slug를 확인하면") {
                every { jpaRepository.existsBySlugValue("non-existent") } returns false

                val exists = adapter.existsBySlug(Slug("non-existent"))

                Then("false가 반환되어야 한다") {
                    exists shouldBe false
                }
            }
        }

        Given("상태별로 Post 목록을 조회할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val publishedEntities =
                listOf(
                    PostJpaEntity.from(
                        entityId = UUID.randomUUID(),
                        memberId = UUID.randomUUID(),
                        title = "발행글 1",
                        slug = "published-1",
                        body = "본문1",
                        type = ContentType.BLOG,
                        status = PostStatus.PUBLISHED,
                    ),
                    PostJpaEntity.from(
                        entityId = UUID.randomUUID(),
                        memberId = UUID.randomUUID(),
                        title = "발행글 2",
                        slug = "published-2",
                        body = "본문2",
                        type = ContentType.BLOG,
                        status = PostStatus.PUBLISHED,
                    ),
                )

            When("PUBLISHED 상태로 조회하면") {
                every { jpaRepository.findAllByStatus(PostStatus.PUBLISHED) } returns publishedEntities

                val posts = adapter.findAllByStatus(PostStatus.PUBLISHED)

                Then("PUBLISHED 상태의 Post 목록이 반환되어야 한다") {
                    posts.size shouldBe 2
                    posts.all { it.status == PostStatus.PUBLISHED } shouldBe true
                }
            }
        }

        Given("타입별로 Post 목록을 조회할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val portfolioEntities =
                listOf(
                    PostJpaEntity.from(
                        entityId = UUID.randomUUID(),
                        memberId = UUID.randomUUID(),
                        title = "프로젝트 1",
                        slug = "project-1",
                        body = "설명1",
                        type = ContentType.PORTFOLIO,
                        status = PostStatus.DRAFT,
                    ),
                )

            When("PORTFOLIO 타입으로 조회하면") {
                every { jpaRepository.findAllByType(ContentType.PORTFOLIO) } returns portfolioEntities

                val posts = adapter.findAllByContentType(ContentType.PORTFOLIO)

                Then("PORTFOLIO 타입의 Post 목록이 반환되어야 한다") {
                    posts.size shouldBe 1
                    posts.all { it.type == ContentType.PORTFOLIO } shouldBe true
                }
            }
        }

        Given("사용자 이름과 Slug로 글을 조회할 때") {
            val jpaRepository = mockk<PostJpaRepository>()
            val eventContextManager = mockk<EventContextManager>()
            val domainEventPublisher = mockk<DomainEventPublisher>()

            val adapter =
                PostRepositoryAdapter(
                    jpaRepository,
                    eventContextManager,
                    domainEventPublisher,
                )

            val username = "testuser"
            val slug = Slug("test-post")
            val entity =
                PostJpaEntity.from(
                    entityId = UUID.randomUUID(),
                    memberId = UUID.randomUUID(),
                    title = "조회된 글",
                    slug = "test-post",
                    body = "본문",
                    type = ContentType.BLOG,
                    status = PostStatus.PUBLISHED,
                )

            When("사용자 이름과 Slug가 일치하는 글이 존재하면") {
                every { jpaRepository.findByUsernameAndSlug(username, slug.value) } returns entity

                val found = adapter.findByUsernameAndSlug(username, slug)

                Then("해당 게시글이 반환된다") {
                    found shouldNotBe null
                    found?.slug shouldBe slug
                }
            }
        }
    })

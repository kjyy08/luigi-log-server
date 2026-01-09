package cloud.luigi99.blog.content.comment.adapter.out.persistence

import cloud.luigi99.blog.adapter.message.spring.SpringDomainEventPublisher
import cloud.luigi99.blog.adapter.message.spring.SpringEventContextManager
import cloud.luigi99.blog.content.comment.adapter.out.persistence.entity.CommentJpaEntity
import cloud.luigi99.blog.content.comment.adapter.out.persistence.repository.CommentJpaRepository
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class CommentRepositoryAdapterTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(cloud.luigi99.blog.common.domain.event.EventManager)
            every {
                cloud.luigi99.blog.common.domain.event.EventManager.eventContextManager
            } returns mockk(relaxed = true)
        }

        Given("댓글을 저장하려고 할 때") {
            val jpaRepository = mockk<CommentJpaRepository>()
            val eventContextManager = mockk<SpringEventContextManager>()
            val domainEventPublisher = mockk<SpringDomainEventPublisher>(relaxed = true)
            val adapter = CommentRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

            val comment =
                Comment.create(
                    postId = PostId.generate(),
                    authorId = MemberId.generate(),
                    content = CommentContent("테스트 댓글"),
                )

            When("저장소에 저장을 요청하면") {
                every { jpaRepository.save(any()) } answers { firstArg() }
                every { eventContextManager.getDomainEventsAndClear() } returns emptyList()

                val savedComment = adapter.save(comment)

                Then("JPA 리포지토리를 통해 저장된다") {
                    verify(exactly = 1) { jpaRepository.save(any()) }
                }

                Then("저장된 댓글이 반환된다") {
                    savedComment shouldNotBe null
                    savedComment.entityId shouldBe comment.entityId
                }

                Then("도메인 이벤트가 발행된다") {
                    verify(exactly = 1) { eventContextManager.getDomainEventsAndClear() }
                }
            }
        }

        Given("댓글 ID로 댓글을 조회하려고 할 때") {
            val jpaRepository = mockk<CommentJpaRepository>()
            val eventContextManager = mockk<SpringEventContextManager>()
            val domainEventPublisher = mockk<SpringDomainEventPublisher>(relaxed = true)
            val adapter = CommentRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

            val commentId = CommentId.generate()
            val postId = PostId.generate()
            val authorId = MemberId.generate()

            val entity =
                CommentJpaEntity.from(
                    entityId = commentId.value,
                    postId = postId.value,
                    authorId = authorId.value,
                    content = "테스트 댓글",
                )

            When("존재하는 댓글을 조회하면") {
                every { jpaRepository.findById(commentId.value) } returns Optional.of(entity)

                val foundComment = adapter.findById(commentId)

                Then("댓글이 조회된다") {
                    foundComment shouldNotBe null
                    foundComment!!.entityId shouldBe commentId
                }
            }

            When("존재하지 않는 댓글을 조회하면") {
                every { jpaRepository.findById(commentId.value) } returns Optional.empty()

                val foundComment = adapter.findById(commentId)

                Then("null이 반환된다") {
                    foundComment shouldBe null
                }
            }
        }

        Given("게시글 ID로 댓글 목록을 조회하려고 할 때") {
            val jpaRepository = mockk<CommentJpaRepository>()
            val eventContextManager = mockk<SpringEventContextManager>()
            val domainEventPublisher = mockk<SpringDomainEventPublisher>(relaxed = true)
            val adapter = CommentRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

            val postId = PostId.generate()
            val author1 = MemberId.generate()
            val author2 = MemberId.generate()

            val entity1 =
                CommentJpaEntity.from(
                    entityId = UUID.randomUUID(),
                    postId = postId.value,
                    authorId = author1.value,
                    content = "첫 번째 댓글",
                )

            val entity2 =
                CommentJpaEntity.from(
                    entityId = UUID.randomUUID(),
                    postId = postId.value,
                    authorId = author2.value,
                    content = "두 번째 댓글",
                )

            When("게시글의 댓글 목록을 조회하면") {
                every { jpaRepository.findByPostIdValue(postId.value) } returns listOf(entity1, entity2)

                val comments = adapter.findByPostId(postId)

                Then("모든 댓글이 조회된다") {
                    comments shouldHaveSize 2
                    comments[0].content.value shouldBe "첫 번째 댓글"
                    comments[1].content.value shouldBe "두 번째 댓글"
                }
            }
        }

        Given("댓글을 삭제하려고 할 때") {
            val jpaRepository = mockk<CommentJpaRepository>()
            val eventContextManager = mockk<SpringEventContextManager>()
            val domainEventPublisher = mockk<SpringDomainEventPublisher>(relaxed = true)
            val adapter = CommentRepositoryAdapter(jpaRepository, eventContextManager, domainEventPublisher)

            val commentId = CommentId.generate()

            When("삭제를 요청하면") {
                every { jpaRepository.deleteById(commentId.value) } returns Unit

                adapter.deleteById(commentId)

                Then("JPA 리포지토리를 통해 삭제된다") {
                    verify(exactly = 1) { jpaRepository.deleteById(commentId.value) }
                }
            }
        }
    })

package cloud.luigi99.blog.content.comment.adapter.out.persistence.mapper

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.comment.adapter.out.persistence.entity.CommentJpaEntity
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.comment.domain.vo.CommentId
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime
import java.util.UUID

class CommentMapperTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("도메인 모델이 주어졌을 때") {
            val commentId = CommentId.generate()
            val postId = PostId.generate()
            val authorId = MemberId.generate()
            val content = CommentContent("좋은 글 감사합니다!")
            val createdAt = LocalDateTime.now().minusDays(7)
            val updatedAt = LocalDateTime.now().minusDays(1)

            val comment =
                Comment.from(
                    entityId = commentId,
                    postId = postId,
                    authorId = authorId,
                    content = content,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )

            When("JPA 엔티티로 변환하면") {
                val entity = CommentMapper.toEntity(comment)

                Then("모든 필드가 정확히 매핑된다") {
                    entity.id shouldBe commentId.value
                    entity.postId shouldBe postId.value
                    entity.authorId shouldBe authorId.value
                    entity.content shouldBe content.value
                    entity.createdAt shouldBe createdAt
                    entity.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("JPA 엔티티가 주어졌을 때") {
            val id = UUID.randomUUID()
            val postId = UUID.randomUUID()
            val authorId = UUID.randomUUID()
            val content = "좋은 글 감사합니다!"
            val createdAt = LocalDateTime.now().minusDays(7)
            val updatedAt = LocalDateTime.now().minusDays(1)

            val entity =
                CommentJpaEntity.from(
                    entityId = id,
                    postId = postId,
                    authorId = authorId,
                    content = content,
                )
            entity.createdAt = createdAt
            entity.updatedAt = updatedAt

            When("도메인 모델로 변환하면") {
                val comment = CommentMapper.toDomain(entity)

                Then("모든 필드가 정확히 매핑된다") {
                    comment.entityId.value shouldBe id
                    comment.postId.value shouldBe postId
                    comment.authorId.value shouldBe authorId
                    comment.content.value shouldBe content
                    comment.createdAt shouldBe createdAt
                    comment.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("도메인과 엔티티를 왕복 변환할 때") {
            val originalComment =
                Comment.create(
                    postId = PostId.generate(),
                    authorId = MemberId.generate(),
                    content = CommentContent("왕복 변환 테스트"),
                )

            When("도메인 → 엔티티 → 도메인으로 변환하면") {
                val entity = CommentMapper.toEntity(originalComment)
                val restoredComment = CommentMapper.toDomain(entity)

                Then("원본 도메인 모델과 동일한 값을 가진다") {
                    restoredComment.entityId shouldBe originalComment.entityId
                    restoredComment.postId shouldBe originalComment.postId
                    restoredComment.authorId shouldBe originalComment.authorId
                    restoredComment.content shouldBe originalComment.content
                }
            }
        }
    })

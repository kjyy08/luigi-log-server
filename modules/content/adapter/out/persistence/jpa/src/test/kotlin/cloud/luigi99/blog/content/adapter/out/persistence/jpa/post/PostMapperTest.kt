package cloud.luigi99.blog.content.adapter.out.persistence.jpa.post

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

/**
 * PostMapper 테스트
 */
class PostMapperTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("Post 도메인 모델이 주어졌을 때") {
            val post =
                Post.create(
                    memberId = MemberId.generate(),
                    title = Title("테스트 제목"),
                    slug = Slug("test-post"),
                    body = Body("테스트 본문"),
                    type = ContentType.BLOG,
                )

            When("JPA 엔티티로 변환하면") {
                val entity = PostMapper.toEntity(post)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    entity.id shouldBe post.entityId.value
                    entity.title shouldBe post.title.value
                    entity.slug shouldBe post.slug.value
                    entity.body shouldBe post.body.value
                    entity.type shouldBe post.type
                    entity.status shouldBe post.status
                }

                Then("tags가 빈 Set으로 초기화되어야 한다") {
                    entity.tags shouldBe emptySet()
                }
            }
        }

        Given("tags가 있는 Post 도메인 모델이 주어졌을 때") {
            val post =
                Post
                    .create(
                        memberId = MemberId.generate(),
                        title = Title("태그 테스트"),
                        slug = Slug("tag-test"),
                        body = Body("태그 본문"),
                        type = ContentType.BLOG,
                    ).addTag("Kotlin")
                    .addTag("Spring")

            When("JPA 엔티티로 변환하면") {
                val entity = PostMapper.toEntity(post)

                Then("tags가 정확하게 매핑되어야 한다") {
                    entity.tags shouldBe setOf("Kotlin", "Spring")
                }
            }
        }

        Given("Post 도메인 모델을 JPA 엔티티로 변환한 후") {
            val originalPost =
                Post
                    .create(
                        memberId = MemberId.generate(),
                        title = Title("원본 제목"),
                        slug = Slug("original-post"),
                        body = Body("원본 본문"),
                        type = ContentType.PORTFOLIO,
                    ).addTag("DDD")
                    .addTag("Hexagonal")

            When("다시 도메인 모델로 변환하면") {
                val entity = PostMapper.toEntity(originalPost)
                val converted = PostMapper.toDomain(entity)

                Then("원본 데이터가 손실 없이 복원되어야 한다") {
                    converted.entityId shouldBe originalPost.entityId
                    converted.title shouldBe originalPost.title
                    converted.slug shouldBe originalPost.slug
                    converted.body shouldBe originalPost.body
                    converted.type shouldBe originalPost.type
                    converted.status shouldBe originalPost.status
                }

                Then("tags도 손실 없이 복원되어야 한다") {
                    converted.tags shouldBe setOf("DDD", "Hexagonal")
                }
            }
        }

        Given("JPA 엔티티가 주어졌을 때") {
            val entity =
                PostJpaEntity.from(
                    entityId =
                        java.util.UUID
                            .randomUUID(),
                    memberId =
                        java.util.UUID
                            .randomUUID(),
                    title = "JPA 제목",
                    slug = "jpa-post",
                    body = "JPA 본문",
                    type = ContentType.BLOG,
                    status = cloud.luigi99.blog.content.domain.post.vo.PostStatus.DRAFT,
                )

            entity.tags.addAll(setOf("JPA", "Hibernate"))

            When("도메인 모델로 변환하면") {
                val domain = PostMapper.toDomain(entity)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    domain.title.value shouldBe "JPA 제목"
                    domain.slug.value shouldBe "jpa-post"
                    domain.body.value shouldBe "JPA 본문"
                    domain.type shouldBe ContentType.BLOG
                    domain.status shouldBe cloud.luigi99.blog.content.domain.post.vo.PostStatus.DRAFT
                }

                Then("tags가 정확하게 매핑되어야 한다") {
                    domain.tags shouldBe setOf("JPA", "Hibernate")
                }
            }
        }
    })

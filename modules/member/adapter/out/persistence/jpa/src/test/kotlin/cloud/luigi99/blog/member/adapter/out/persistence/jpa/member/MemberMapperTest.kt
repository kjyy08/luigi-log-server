package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import java.util.UUID

/**
 * MemberMapper 테스트
 *
 * 도메인 모델과 JPA 엔티티 간의 변환 로직을 검증합니다.
 */
class MemberMapperTest :
    BehaviorSpec({

        Given("프로필이 없는 회원 도메인 모델이 주어졌을 때") {
            val memberId = MemberId.generate()
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = null,
                    createdAt = LocalDateTime.of(2025, 1, 1, 10, 0),
                    updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30),
                )

            When("JPA 엔티티로 변환하면") {
                val entity = MemberMapper.toEntity(member)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    entity.id shouldBe memberId.value
                    entity.email shouldBe "test@example.com"
                    entity.username shouldBe "testuser"
                    entity.profile shouldBe null
                    entity.createdAt shouldBe LocalDateTime.of(2025, 1, 1, 10, 0)
                    entity.updatedAt shouldBe LocalDateTime.of(2025, 1, 2, 15, 30)
                }
            }
        }

        Given("프로필이 있는 회원 도메인 모델이 주어졌을 때") {
            val memberId = MemberId.generate()
            val profile =
                Profile.create(
                    nickname = Nickname("개발자"),
                )
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("dev@example.com"),
                    username = Username("developer"),
                    profile = profile,
                    createdAt = null,
                    updatedAt = null,
                )

            When("JPA 엔티티로 변환하면") {
                val entity = MemberMapper.toEntity(member)

                Then("프로필 정보도 함께 매핑되어야 한다") {
                    entity.id shouldBe memberId.value
                    entity.email shouldBe "dev@example.com"
                    entity.profile shouldNotBe null
                    entity.profile?.nickname shouldBe "개발자"
                }
            }
        }

        Given("프로필이 없는 JPA 엔티티가 주어졌을 때") {
            val entityId = UUID.randomUUID()
            val jpaEntity =
                MemberJpaEntity
                    .from(
                        entityId = entityId,
                        email = "stored@example.com",
                        username = "storeduser",
                    ).apply {
                        createdAt = LocalDateTime.of(2025, 1, 1, 12, 0)
                        updatedAt = LocalDateTime.of(2025, 1, 3, 18, 0)
                    }

            When("도메인 모델로 변환하면") {
                val domain = MemberMapper.toDomain(jpaEntity)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    domain.entityId.value shouldBe entityId
                    domain.email.value shouldBe "stored@example.com"
                    domain.username.value shouldBe "storeduser"
                    domain.profile shouldBe null
                    domain.createdAt shouldBe LocalDateTime.of(2025, 1, 1, 12, 0)
                    domain.updatedAt shouldBe LocalDateTime.of(2025, 1, 3, 18, 0)
                }
            }
        }

        Given("도메인 모델을 JPA 엔티티로 변환한 후") {
            val memberId = MemberId.generate()
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("original@example.com"),
                    username = Username("original"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )
            val entity = MemberMapper.toEntity(member)

            When("다시 도메인 모델로 변환하면") {
                val converted = MemberMapper.toDomain(entity)

                Then("원본 데이터가 손실 없이 복원되어야 한다") {
                    converted.entityId shouldBe member.entityId
                    converted.email shouldBe member.email
                    converted.username shouldBe member.username
                    converted.profile shouldBe member.profile
                }
            }
        }

        Given("여러 회원 정보가 있을 때") {
            val memberId1 = MemberId.generate()
            val memberId2 = MemberId.generate()

            val member1 =
                Member.from(
                    entityId = memberId1,
                    email = Email("user1@example.com"),
                    username = Username("user1"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )
            val member2 =
                Member.from(
                    entityId = memberId2,
                    email = Email("user2@example.com"),
                    username = Username("user2"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )

            When("각각 JPA 엔티티로 변환하면") {
                val entity1 = MemberMapper.toEntity(member1)
                val entity2 = MemberMapper.toEntity(member2)

                Then("각 회원의 정보가 독립적으로 정확히 매핑되어야 한다") {
                    entity1.id shouldBe member1.entityId.value
                    entity1.email shouldBe "user1@example.com"

                    entity2.id shouldBe member2.entityId.value
                    entity2.email shouldBe "user2@example.com"

                    entity1.id shouldNotBe entity2.id
                }
            }
        }
    })

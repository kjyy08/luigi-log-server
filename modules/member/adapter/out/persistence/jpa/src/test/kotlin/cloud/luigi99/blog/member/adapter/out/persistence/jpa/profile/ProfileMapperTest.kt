package cloud.luigi99.blog.member.adapter.out.persistence.jpa.profile

import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.ContactEmail
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.ProfileId
import cloud.luigi99.blog.member.domain.profile.vo.TechStack
import cloud.luigi99.blog.member.domain.profile.vo.Url
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import java.util.UUID

/**
 * ProfileMapper 테스트
 *
 * 프로필 도메인 모델과 JPA 엔티티 간의 변환 로직을 검증합니다.
 */
class ProfileMapperTest :
    BehaviorSpec({

        Given("최소 정보만 있는 프로필 도메인 모델이 주어졌을 때") {
            val profileId = ProfileId.generate()
            val profile =
                Profile.from(
                    entityId = profileId,
                    nickname = Nickname("최소닉네임"),
                    bio = null,
                    profileImageUrl = null,
                    jobTitle = null,
                    techStack = TechStack(emptyList()),
                    githubUrl = null,
                    contactEmail = null,
                    websiteUrl = null,
                    createdAt = LocalDateTime.of(2025, 1, 1, 10, 0),
                    updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30),
                )

            When("JPA 엔티티로 변환하면") {
                val entity = ProfileMapper.toEntity(profile)

                Then("필수 필드와 선택 필드가 정확하게 매핑되어야 한다") {
                    entity.id shouldBe profileId.value
                    entity.nickname shouldBe "최소닉네임"
                    entity.bio shouldBe null
                    entity.profileImageUrl shouldBe null
                    entity.jobTitle shouldBe null
                    entity.techStack shouldBe emptyList()
                    entity.githubUrl shouldBe null
                    entity.contactEmail shouldBe null
                    entity.websiteUrl shouldBe null
                    entity.createdAt shouldBe LocalDateTime.of(2025, 1, 1, 10, 0)
                    entity.updatedAt shouldBe LocalDateTime.of(2025, 1, 2, 15, 30)
                }
            }
        }

        Given("모든 정보가 있는 프로필 도메인 모델이 주어졌을 때") {
            val profileId = ProfileId.generate()
            val profile =
                Profile.from(
                    entityId = profileId,
                    nickname = Nickname("풀스택개발자"),
                    bio = Bio("안녕하세요, 백엔드 개발자입니다."),
                    profileImageUrl = Url("https://example.com/profile.jpg"),
                    jobTitle = JobTitle("Senior Backend Developer"),
                    techStack = TechStack(listOf("Kotlin", "Spring Boot", "DDD", "TDD")),
                    githubUrl = Url("https://github.com/developer"),
                    contactEmail = ContactEmail("contact@developer.com"),
                    websiteUrl = Url("https://developer.blog"),
                    createdAt = LocalDateTime.of(2025, 1, 5, 9, 0),
                    updatedAt = LocalDateTime.of(2025, 1, 10, 14, 0),
                )

            When("JPA 엔티티로 변환하면") {
                val entity = ProfileMapper.toEntity(profile)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    entity.id shouldBe profileId.value
                    entity.nickname shouldBe "풀스택개발자"
                    entity.bio shouldBe "안녕하세요, 백엔드 개발자입니다."
                    entity.profileImageUrl shouldBe "https://example.com/profile.jpg"
                    entity.jobTitle shouldBe "Senior Backend Developer"
                    entity.techStack shouldBe listOf("Kotlin", "Spring Boot", "DDD", "TDD")
                    entity.githubUrl shouldBe "https://github.com/developer"
                    entity.contactEmail shouldBe "contact@developer.com"
                    entity.websiteUrl shouldBe "https://developer.blog"
                    entity.createdAt shouldBe LocalDateTime.of(2025, 1, 5, 9, 0)
                    entity.updatedAt shouldBe LocalDateTime.of(2025, 1, 10, 14, 0)
                }
            }
        }

        Given("최소 정보만 있는 JPA 엔티티가 주어졌을 때") {
            val entityId = UUID.randomUUID()
            val jpaEntity =
                ProfileJpaEntity
                    .from(
                        entityId = entityId,
                        nickname = "간단닉네임",
                        bio = null,
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = emptyList(),
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    ).apply {
                        createdAt = LocalDateTime.of(2025, 2, 1, 11, 0)
                        updatedAt = LocalDateTime.of(2025, 2, 5, 16, 0)
                    }

            When("도메인 모델로 변환하면") {
                val domain = ProfileMapper.toDomain(jpaEntity)

                Then("필수 필드와 선택 필드가 정확하게 매핑되어야 한다") {
                    domain.entityId.value shouldBe entityId
                    domain.nickname.value shouldBe "간단닉네임"
                    domain.bio shouldBe null
                    domain.profileImageUrl shouldBe null
                    domain.jobTitle shouldBe null
                    domain.techStack.values shouldBe emptyList()
                    domain.githubUrl shouldBe null
                    domain.contactEmail shouldBe null
                    domain.websiteUrl shouldBe null
                    domain.createdAt shouldBe LocalDateTime.of(2025, 2, 1, 11, 0)
                    domain.updatedAt shouldBe LocalDateTime.of(2025, 2, 5, 16, 0)
                }
            }
        }

        Given("모든 정보가 있는 JPA 엔티티가 주어졌을 때") {
            val entityId = UUID.randomUUID()
            val jpaEntity =
                ProfileJpaEntity
                    .from(
                        entityId = entityId,
                        nickname = "전문가",
                        bio = "10년 경력의 개발자",
                        profileImageUrl = "https://cdn.example.com/avatar.png",
                        jobTitle = "Tech Lead",
                        techStack = listOf("Java", "Kotlin", "AWS", "Kubernetes"),
                        githubUrl = "https://github.com/expert",
                        contactEmail = "expert@company.com",
                        websiteUrl = "https://expert.tech",
                    ).apply {
                        createdAt = LocalDateTime.of(2025, 3, 1, 8, 0)
                        updatedAt = LocalDateTime.of(2025, 3, 15, 20, 0)
                    }

            When("도메인 모델로 변환하면") {
                val domain = ProfileMapper.toDomain(jpaEntity)

                Then("모든 필드가 정확하게 매핑되어야 한다") {
                    domain.entityId.value shouldBe entityId
                    domain.nickname.value shouldBe "전문가"
                    domain.bio?.value shouldBe "10년 경력의 개발자"
                    domain.profileImageUrl?.value shouldBe "https://cdn.example.com/avatar.png"
                    domain.jobTitle?.value shouldBe "Tech Lead"
                    domain.techStack.values shouldBe listOf("Java", "Kotlin", "AWS", "Kubernetes")
                    domain.githubUrl?.value shouldBe "https://github.com/expert"
                    domain.contactEmail?.value shouldBe "expert@company.com"
                    domain.websiteUrl?.value shouldBe "https://expert.tech"
                    domain.createdAt shouldBe LocalDateTime.of(2025, 3, 1, 8, 0)
                    domain.updatedAt shouldBe LocalDateTime.of(2025, 3, 15, 20, 0)
                }
            }
        }

        Given("도메인 모델을 JPA 엔티티로 변환한 후") {
            val profile =
                Profile.create(
                    nickname = Nickname("원본닉네임"),
                    bio = Bio("원본 소개"),
                    techStack = TechStack(listOf("Spring", "Kotlin")),
                )
            val entity = ProfileMapper.toEntity(profile)

            When("다시 도메인 모델로 변환하면") {
                val converted = ProfileMapper.toDomain(entity)

                Then("원본 데이터가 손실 없이 복원되어야 한다") {
                    converted.entityId shouldBe profile.entityId
                    converted.nickname shouldBe profile.nickname
                    converted.bio shouldBe profile.bio
                    converted.techStack.values shouldBe profile.techStack.values
                }
            }
        }

        Given("기술 스택이 여러 개인 프로필이 주어졌을 때") {
            val techList = listOf("Kotlin", "Java", "Python", "Go", "Rust")
            val profile =
                Profile.create(
                    nickname = Nickname("멀티언어개발자"),
                    techStack = TechStack(techList),
                )

            When("JPA 엔티티로 변환하면") {
                val entity = ProfileMapper.toEntity(profile)

                Then("기술 스택 리스트가 순서대로 보존되어야 한다") {
                    entity.techStack shouldBe techList
                }
            }

            When("JPA 엔티티를 다시 도메인으로 변환하면") {
                val entity = ProfileMapper.toEntity(profile)
                val converted = ProfileMapper.toDomain(entity)

                Then("기술 스택이 원본과 동일해야 한다") {
                    converted.techStack.values shouldBe techList
                }
            }
        }

        Given("여러 프로필 정보가 있을 때") {
            val profile1 =
                Profile.create(
                    nickname = Nickname("개발자1"),
                    bio = Bio("첫 번째 개발자"),
                )
            val profile2 =
                Profile.create(
                    nickname = Nickname("개발자2"),
                    bio = Bio("두 번째 개발자"),
                )

            When("각각 JPA 엔티티로 변환하면") {
                val entity1 = ProfileMapper.toEntity(profile1)
                val entity2 = ProfileMapper.toEntity(profile2)

                Then("각 프로필의 정보가 독립적으로 정확히 매핑되어야 한다") {
                    entity1.id shouldBe profile1.entityId.value
                    entity1.nickname shouldBe "개발자1"
                    entity1.bio shouldBe "첫 번째 개발자"

                    entity2.id shouldBe profile2.entityId.value
                    entity2.nickname shouldBe "개발자2"
                    entity2.bio shouldBe "두 번째 개발자"

                    entity1.id shouldNotBe entity2.id
                }
            }
        }
    })

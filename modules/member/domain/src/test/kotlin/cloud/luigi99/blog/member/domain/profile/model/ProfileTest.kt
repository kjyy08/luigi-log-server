package cloud.luigi99.blog.member.domain.profile.model

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

class ProfileTest :
    BehaviorSpec({

        Given("필수 닉네임만으로 프로필을 생성할 때") {
            val nickname = Nickname("개발자")

            When("create()를 호출하면") {
                val profile = Profile.create(nickname)

                Then("프로필 ID가 생성된다") {
                    profile.entityId shouldNotBe null
                }

                Then("닉네임이 설정된다") {
                    profile.nickname shouldBe nickname
                }

                Then("선택 필드들은 null 또는 기본값이다") {
                    profile.bio shouldBe null
                    profile.profileImageUrl shouldBe null
                    profile.jobTitle shouldBe null
                    profile.techStack.isEmpty() shouldBe true
                    profile.githubUrl shouldBe null
                    profile.contactEmail shouldBe null
                    profile.websiteUrl shouldBe null
                }
            }
        }

        Given("모든 필드를 포함하여 프로필을 생성할 때") {
            val nickname = Nickname("개발자")
            val bio = Bio("백엔드 개발자입니다.")
            val profileImageUrl = Url("https://example.com/profile.jpg")
            val jobTitle = JobTitle("Senior Developer")
            val techStack = TechStack(listOf("Kotlin", "Spring Boot"))
            val githubUrl = Url("https://github.com/developer")
            val contactEmail = ContactEmail("contact@example.com")
            val websiteUrl = Url("https://developer.com")

            When("create()를 호출하면") {
                val profile =
                    Profile.create(
                        nickname = nickname,
                        bio = bio,
                        profileImageUrl = profileImageUrl,
                        jobTitle = jobTitle,
                        techStack = techStack,
                        githubUrl = githubUrl,
                        contactEmail = contactEmail,
                        websiteUrl = websiteUrl,
                    )

                Then("모든 필드가 올바르게 설정된다") {
                    profile.nickname shouldBe nickname
                    profile.bio shouldBe bio
                    profile.profileImageUrl shouldBe profileImageUrl
                    profile.jobTitle shouldBe jobTitle
                    profile.techStack shouldBe techStack
                    profile.githubUrl shouldBe githubUrl
                    profile.contactEmail shouldBe contactEmail
                    profile.websiteUrl shouldBe websiteUrl
                }
            }
        }

        Given("생성된 프로필이 있을 때") {
            val profile =
                Profile.create(
                    nickname = Nickname("개발자"),
                    bio = Bio("백엔드 개발자입니다."),
                    techStack = TechStack(listOf("Kotlin")),
                )

            When("닉네임만 변경하면") {
                val newNickname = Nickname("시니어 개발자")
                val updatedProfile = profile.update(nickname = newNickname)

                Then("닉네임이 변경된다") {
                    updatedProfile.nickname shouldBe newNickname
                }

                Then("다른 필드들은 유지된다") {
                    updatedProfile.entityId shouldBe profile.entityId
                    updatedProfile.bio shouldBe profile.bio
                    updatedProfile.techStack shouldBe profile.techStack
                }
            }

            When("여러 필드를 동시에 변경하면") {
                val newNickname = Nickname("시니어 개발자")
                val newBio = Bio("10년차 백엔드 개발자입니다.")
                val newTechStack = TechStack(listOf("Kotlin", "Spring Boot", "PostgreSQL"))

                val updatedProfile =
                    profile.update(
                        nickname = newNickname,
                        bio = newBio,
                        techStack = newTechStack,
                    )

                Then("모든 변경사항이 반영된다") {
                    updatedProfile.nickname shouldBe newNickname
                    updatedProfile.bio shouldBe newBio
                    updatedProfile.techStack shouldBe newTechStack
                }

                Then("엔티티 ID는 유지된다") {
                    updatedProfile.entityId shouldBe profile.entityId
                }
            }
        }

        Given("영속성 계층에서 로드할 데이터가 있을 때") {
            val profileId = ProfileId.generate()
            val nickname = Nickname("개발자")
            val bio = Bio("백엔드 개발자입니다.")
            val profileImageUrl = Url("https://example.com/profile.jpg")
            val jobTitle = JobTitle("Senior Developer")
            val techStack = TechStack(listOf("Kotlin", "Spring Boot"))
            val githubUrl = Url("https://github.com/developer")
            val contactEmail = ContactEmail("contact@example.com")
            val websiteUrl = Url("https://developer.com")
            val createdAt = LocalDateTime.now().minusDays(30)
            val updatedAt = LocalDateTime.now().minusDays(1)

            When("from()을 사용하여 Profile을 재구성하면") {
                val profile =
                    Profile.from(
                        entityId = profileId,
                        nickname = nickname,
                        bio = bio,
                        profileImageUrl = profileImageUrl,
                        jobTitle = jobTitle,
                        techStack = techStack,
                        githubUrl = githubUrl,
                        contactEmail = contactEmail,
                        websiteUrl = websiteUrl,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )

                Then("모든 속성이 올바르게 설정된다") {
                    profile.entityId shouldBe profileId
                    profile.nickname shouldBe nickname
                    profile.bio shouldBe bio
                    profile.profileImageUrl shouldBe profileImageUrl
                    profile.jobTitle shouldBe jobTitle
                    profile.techStack shouldBe techStack
                    profile.githubUrl shouldBe githubUrl
                    profile.contactEmail shouldBe contactEmail
                    profile.websiteUrl shouldBe websiteUrl
                    profile.createdAt shouldBe createdAt
                    profile.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("프로필의 불변성 테스트") {
            val originalProfile =
                Profile.create(
                    nickname = Nickname("개발자"),
                    bio = Bio("원래 소개"),
                )
            val originalNickname = originalProfile.nickname
            val originalBio = originalProfile.bio

            When("닉네임을 변경하면") {
                val updatedProfile =
                    originalProfile.update(
                        nickname = Nickname("새 닉네임"),
                    )

                Then("원본 프로필 객체는 변경되지 않는다") {
                    originalProfile.nickname shouldBe originalNickname
                    originalProfile.bio shouldBe originalBio
                }

                Then("새로운 프로필 객체가 반환된다") {
                    updatedProfile.nickname.value shouldBe "새 닉네임"
                }

                Then("엔티티 ID는 동일하게 유지된다") {
                    updatedProfile.entityId shouldBe originalProfile.entityId
                }
            }
        }

        Given("update() 기본값 동작 테스트") {
            val profile =
                Profile.create(
                    nickname = Nickname("개발자"),
                    bio = Bio("백엔드 개발자"),
                    techStack = TechStack(listOf("Kotlin")),
                    jobTitle = JobTitle("Developer"),
                )

            When("파라미터를 명시하지 않고 update()를 호출하면") {
                val updatedProfile = profile.update()

                Then("모든 필드가 원본과 동일하게 유지된다") {
                    updatedProfile.nickname shouldBe profile.nickname
                    updatedProfile.bio shouldBe profile.bio
                    updatedProfile.techStack shouldBe profile.techStack
                    updatedProfile.jobTitle shouldBe profile.jobTitle
                }
            }

            When("일부 필드만 null로 변경하면") {
                val updatedProfile =
                    profile.update(
                        bio = null,
                        jobTitle = null,
                    )

                Then("명시적으로 null을 전달한 필드는 null이 된다") {
                    updatedProfile.bio shouldBe null
                    updatedProfile.jobTitle shouldBe null
                }

                Then("다른 필드는 유지된다") {
                    updatedProfile.nickname shouldBe profile.nickname
                    updatedProfile.techStack shouldBe profile.techStack
                }
            }
        }

        Given("URL 필드 테스트") {
            val githubUrl = Url("https://github.com/developer")
            val websiteUrl = Url("https://developer.com")
            val profileImageUrl = Url("https://example.com/image.jpg")

            When("모든 URL을 포함하여 프로필을 생성하면") {
                val profile =
                    Profile.create(
                        nickname = Nickname("개발자"),
                        githubUrl = githubUrl,
                        websiteUrl = websiteUrl,
                        profileImageUrl = profileImageUrl,
                    )

                Then("모든 URL이 올바르게 설정된다") {
                    profile.githubUrl shouldBe githubUrl
                    profile.websiteUrl shouldBe websiteUrl
                    profile.profileImageUrl shouldBe profileImageUrl
                }
            }
        }

        Given("기술 스택 변경 테스트") {
            val profile =
                Profile.create(
                    nickname = Nickname("개발자"),
                    techStack = TechStack(listOf("Java")),
                )

            When("기술 스택을 변경하면") {
                val newTechStack = TechStack(listOf("Kotlin", "Spring Boot", "PostgreSQL"))
                val updatedProfile = profile.update(techStack = newTechStack)

                Then("기술 스택이 변경된다") {
                    updatedProfile.techStack shouldBe newTechStack
                    updatedProfile.techStack.values shouldBe listOf("Kotlin", "Spring Boot", "PostgreSQL")
                }
            }

            When("기술 스택을 빈 목록으로 변경하면") {
                val emptyTechStack = TechStack(emptyList())
                val updatedProfile = profile.update(techStack = emptyTechStack)

                Then("기술 스택이 빈 목록이 된다") {
                    updatedProfile.techStack.isEmpty() shouldBe true
                }
            }
        }
    })

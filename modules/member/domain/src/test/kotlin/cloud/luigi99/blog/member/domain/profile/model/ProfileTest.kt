package cloud.luigi99.blog.member.domain.profile.model

import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.Company
import cloud.luigi99.blog.member.domain.profile.vo.ContactEmail
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Location
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.ProfileId
import cloud.luigi99.blog.member.domain.profile.vo.Readme
import cloud.luigi99.blog.member.domain.profile.vo.Url
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
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
                    profile.readme shouldBe null
                    profile.company shouldBe null
                    profile.location shouldBe null
                    profile.jobTitle shouldBe null
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
            val readme = Readme("# README\n프로필 정보")
            val company = Company("테크 회사")
            val location = Location("서울, 대한민국")
            val jobTitle = JobTitle("Senior Developer")
            val githubUrl = Url("https://github.com/developer")
            val contactEmail = ContactEmail("contact@example.com")
            val websiteUrl = Url("https://developer.com")

            When("create()를 호출하면") {
                val profile =
                    Profile.create(
                        nickname = nickname,
                        bio = bio,
                        profileImageUrl = profileImageUrl,
                        readme = readme,
                        company = company,
                        location = location,
                        jobTitle = jobTitle,
                        githubUrl = githubUrl,
                        contactEmail = contactEmail,
                        websiteUrl = websiteUrl,
                    )

                Then("모든 필드가 올바르게 설정된다") {
                    profile.nickname shouldBe nickname
                    profile.bio shouldBe bio
                    profile.profileImageUrl shouldBe profileImageUrl
                    profile.readme shouldBe readme
                    profile.company shouldBe company
                    profile.location shouldBe location
                    profile.jobTitle shouldBe jobTitle
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
                    readme = Readme("# 소개\n개발자 소개"),
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
                    updatedProfile.readme shouldBe profile.readme
                }
            }

            When("여러 필드를 동시에 변경하면") {
                val newNickname = Nickname("시니어 개발자")
                val newBio = Bio("10년차 백엔드 개발자입니다.")
                val newReadme = Readme("# 시니어 개발자\n10년차 개발자")
                val newCompany = Company("글로벌 기업")
                val newLocation = Location("서울")

                val updatedProfile =
                    profile.update(
                        nickname = newNickname,
                        bio = newBio,
                        readme = newReadme,
                        company = newCompany,
                        location = newLocation,
                    )

                Then("모든 변경사항이 반영된다") {
                    updatedProfile.nickname shouldBe newNickname
                    updatedProfile.bio shouldBe newBio
                    updatedProfile.readme shouldBe newReadme
                    updatedProfile.company shouldBe newCompany
                    updatedProfile.location shouldBe newLocation
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
            val readme = Readme("# README\n프로필")
            val company = Company("테크 기업")
            val location = Location("서울")
            val jobTitle = JobTitle("Senior Developer")
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
                        readme = readme,
                        company = company,
                        location = location,
                        jobTitle = jobTitle,
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
                    profile.readme shouldBe readme
                    profile.company shouldBe company
                    profile.location shouldBe location
                    profile.jobTitle shouldBe jobTitle
                    profile.githubUrl shouldBe githubUrl
                    profile.contactEmail shouldBe contactEmail
                    profile.websiteUrl shouldBe websiteUrl
                    profile.createdAt shouldBe createdAt
                    profile.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("프로필 업데이트가 현재 인스턴스를 변경하는지 테스트") {
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

                Then("원본 프로필 객체 자체가 변경된다") {
                    originalProfile.nickname shouldNotBe originalNickname
                    originalProfile.bio shouldBe originalBio
                    originalProfile.nickname.value shouldBe "새 닉네임"
                }

                Then("현재 프로필 객체가 반환된다") {
                    updatedProfile shouldBeSameInstanceAs originalProfile
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
                    readme = Readme("# 개발자\n소개"),
                    jobTitle = JobTitle("Developer"),
                )

            When("파라미터를 명시하지 않고 update()를 호출하면") {
                val updatedProfile = profile.update()

                Then("모든 필드가 원본과 동일하게 유지된다") {
                    updatedProfile.nickname shouldBe profile.nickname
                    updatedProfile.bio shouldBe profile.bio
                    updatedProfile.readme shouldBe profile.readme
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
                    updatedProfile.readme shouldBe profile.readme
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

        Given("README 변경 테스트") {
            val profile =
                Profile.create(
                    nickname = Nickname("개발자"),
                    readme = Readme("# 소개\n간단한 소개"),
                )

            When("README를 변경하면") {
                val newReadme = Readme("# 개발자 소개\n상세한 소개 내용입니다.")
                val updatedProfile = profile.update(readme = newReadme)

                Then("README가 변경된다") {
                    updatedProfile.readme shouldBe newReadme
                    updatedProfile.readme?.value shouldBe "# 개발자 소개\n상세한 소개 내용입니다."
                }
            }

            When("README를 null로 변경하면") {
                val updatedProfile = profile.update(readme = null)

                Then("README가 null이 된다") {
                    updatedProfile.readme shouldBe null
                }
            }
        }
    })

package cloud.luigi99.blog.member.domain.member.model

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime

/**
 * Member 애그리거트 도메인 로직 테스트
 *
 * 이 테스트는 순수 도메인 로직만 검증합니다.
 * 도메인 이벤트 발행은 Application 레이어의 통합 테스트에서 검증합니다.
 */
class MemberTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("유효한 이메일과 사용자 이름이 주어졌을 때") {
            val email = Email("user@example.com")
            val username = Username("john_doe")

            When("회원을 등록하면") {
                val member = Member.register(email, username)

                Then("회원 ID가 생성된다") {
                    member.entityId shouldNotBe null
                }

                Then("이메일이 올바르게 설정된다") {
                    member.email shouldBe email
                }

                Then("사용자 이름이 올바르게 설정된다") {
                    member.username shouldBe username
                }

                Then("프로필은 null이다") {
                    member.profile shouldBe null
                }
            }
        }

        Given("프로필과 함께 회원을 등록할 때") {
            val email = Email("user@example.com")
            val username = Username("john_doe")
            val profile = Profile.create(Nickname("John"))

            When("프로필을 포함하여 회원을 등록하면") {
                val member = Member.register(email, username, profile)

                Then("프로필이 설정된다") {
                    member.profile shouldBe profile
                }

                Then("다른 필드들도 올바르게 설정된다") {
                    member.entityId shouldNotBe null
                    member.email shouldBe email
                    member.username shouldBe username
                }
            }
        }

        Given("등록된 회원이 있을 때") {
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                )

            When("사용자 이름을 변경하면") {
                val newUsername = Username("jane_doe")
                val updatedMember = member.updateUsername(newUsername)

                Then("새로운 사용자 이름이 설정된다") {
                    updatedMember.username shouldBe newUsername
                }

                Then("다른 속성들은 유지된다") {
                    updatedMember.entityId shouldBe member.entityId
                    updatedMember.email shouldBe member.email
                    updatedMember.profile shouldBe member.profile
                }
            }
        }

        Given("등록된 회원이 프로필 업데이트를 할 때") {
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                )

            When("프로필을 업데이트하면") {
                val newProfile = Profile.create(Nickname("New Nickname"))
                val updatedMember = member.updateProfile(newProfile)

                Then("새로운 프로필이 설정된다") {
                    updatedMember.profile shouldBe newProfile
                }

                Then("다른 속성들은 유지된다") {
                    updatedMember.entityId shouldBe member.entityId
                    updatedMember.email shouldBe member.email
                    updatedMember.username shouldBe member.username
                }
            }
        }

        Given("등록된 회원이 탈퇴를 요청할 때") {
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                )

            When("회원 탈퇴를 처리하면") {
                val withdrawnMember = member.withdraw()

                Then("회원 정보는 유지된다") {
                    withdrawnMember shouldBe member
                    withdrawnMember.entityId shouldBe member.entityId
                    withdrawnMember.email shouldBe member.email
                    withdrawnMember.username shouldBe member.username
                }
            }
        }

        Given("영속성 계층에서 로드할 데이터가 있을 때") {
            val memberId = MemberId.generate()
            val email = Email("user@example.com")
            val username = Username("john_doe")
            val profile = Profile.create(Nickname("John"))
            val createdAt = LocalDateTime.now().minusDays(7)
            val updatedAt = LocalDateTime.now().minusDays(1)

            When("from()을 사용하여 Member를 재구성하면") {
                val member =
                    Member.from(
                        entityId = memberId,
                        email = email,
                        username = username,
                        profile = profile,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )

                Then("모든 속성이 올바르게 설정된다") {
                    member.entityId shouldBe memberId
                    member.email shouldBe email
                    member.username shouldBe username
                    member.profile shouldBe profile
                    member.createdAt shouldBe createdAt
                    member.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("회원의 불변성을 테스트할 때") {
            val originalMember =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                )
            val originalEntityId = originalMember.entityId
            val originalEmail = originalMember.email
            val originalUsername = originalMember.username

            When("사용자 이름을 변경하면") {
                val updatedMember = originalMember.updateUsername(Username("jane_doe"))

                Then("원본 회원 객체는 변경되지 않는다") {
                    originalMember.username shouldBe originalUsername
                    originalMember.username.value shouldBe "john_doe"
                }

                Then("새로운 회원 객체가 반환된다") {
                    updatedMember.username.value shouldBe "jane_doe"
                }

                Then("엔티티 ID는 동일하게 유지된다") {
                    updatedMember.entityId shouldBe originalEntityId
                    updatedMember.email shouldBe originalEmail
                }
            }
        }

        Given("연속된 업데이트를 수행할 때") {
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                )
            val originalEntityId = member.entityId

            When("사용자 이름과 프로필을 연속으로 업데이트하면") {
                val updatedMember =
                    member
                        .updateUsername(Username("new_name"))
                        .updateProfile(Profile.create(Nickname("New")))

                Then("모든 변경사항이 반영된다") {
                    updatedMember.username.value shouldBe "new_name"
                    updatedMember.profile shouldNotBe null
                    updatedMember.profile
                        ?.nickname
                        ?.value shouldBe "New"
                }

                Then("엔티티 ID는 유지된다") {
                    updatedMember.entityId shouldBe originalEntityId
                }
            }
        }

        Given("null 프로필을 가진 회원이 있을 때") {
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                    profile = null,
                )

            When("프로필을 새로 추가하면") {
                val newProfile = Profile.create(Nickname("John Doe"))
                val updatedMember = member.updateProfile(newProfile)

                Then("프로필이 설정된다") {
                    updatedMember.profile shouldBe newProfile
                }
            }
        }

        Given("프로필을 가진 회원이 있을 때") {
            val initialProfile = Profile.create(Nickname("Initial"))
            val member =
                Member.register(
                    Email("user@example.com"),
                    Username("john_doe"),
                    profile = initialProfile,
                )

            When("프로필을 다른 프로필로 교체하면") {
                val newProfile = Profile.create(Nickname("Updated"))
                val updatedMember = member.updateProfile(newProfile)

                Then("새로운 프로필로 교체된다") {
                    updatedMember.profile shouldBe newProfile
                    updatedMember.profile
                        ?.nickname
                        ?.value shouldBe "Updated"
                }

                Then("이전 프로필과는 다르다") {
                    updatedMember.profile shouldNotBe initialProfile
                }
            }
        }
    })

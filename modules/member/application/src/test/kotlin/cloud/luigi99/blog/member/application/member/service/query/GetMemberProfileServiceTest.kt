package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.Company
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Location
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.Readme
import cloud.luigi99.blog.member.domain.profile.vo.Url
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class GetMemberProfileServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("프로필이 있는 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMemberProfileService(memberRepository)

            val memberId = MemberId.generate()
            val profile =
                Profile.create(
                    nickname = Nickname("TestNick"),
                    bio = Bio("테스트 자기소개"),
                    profileImageUrl = Url("https://example.com/profile.jpg"),
                    readme = Readme("# 소개\n개발자"),
                    company = Company("테크 회사"),
                    location = Location("서울"),
                    jobTitle = JobTitle("Developer"),
                    githubUrl = Url("https://github.com/testuser"),
                    contactEmail = null,
                    websiteUrl = Url("https://testuser.com"),
                )
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = profile,
                    createdAt = null,
                    updatedAt = null,
                )

            When("회원 프로필을 조회하면") {
                val query =
                    GetMemberProfileUseCase.Query(
                        username = "testuser",
                    )

                every { memberRepository.findByUsername(Username("testuser")) } returns member

                val response = service.execute(query)

                Then("회원 정보가 반환된다") {
                    response.memberId shouldBe memberId.toString()
                    response.email shouldBe "test@example.com"
                    response.username shouldBe "testuser"
                }

                Then("프로필 정보가 반환된다") {
                    response.profile shouldNotBe null
                    response.profile?.nickname shouldBe "TestNick"
                    response.profile?.bio shouldBe "테스트 자기소개"
                    response.profile?.profileImageUrl shouldBe "https://example.com/profile.jpg"
                    response.profile?.readme shouldBe "# 소개\n개발자"
                    response.profile?.company shouldBe "테크 회사"
                    response.profile?.location shouldBe "서울"
                    response.profile?.jobTitle shouldBe "Developer"
                    response.profile?.githubUrl shouldBe "https://github.com/testuser"
                    response.profile?.websiteUrl shouldBe "https://testuser.com"
                }
            }
        }

        Given("프로필이 없는 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMemberProfileService(memberRepository)

            val memberId = MemberId.generate()
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )

            When("회원 프로필을 조회하면") {
                val query =
                    GetMemberProfileUseCase.Query(
                        username = "testuser",
                    )

                every { memberRepository.findByUsername(Username("testuser")) } returns member

                val response = service.execute(query)

                Then("회원 정보는 반환된다") {
                    response.memberId shouldBe memberId.toString()
                    response.email shouldBe "test@example.com"
                    response.username shouldBe "testuser"
                }

                Then("프로필은 null이다") {
                    response.profile shouldBe null
                }
            }
        }

        Given("존재하지 않는 회원을 조회할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMemberProfileService(memberRepository)

            When("존재하지 않는 회원 ID로 조회하면") {
                val query =
                    GetMemberProfileUseCase.Query(
                        username = "nonexistent",
                    )

                every { memberRepository.findByUsername(any()) } returns null

                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }

        Given("일부 프로필 필드만 있는 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMemberProfileService(memberRepository)

            val memberId = MemberId.generate()
            val profile =
                Profile.create(
                    nickname = Nickname("MinimalNick"),
                    bio = null,
                    profileImageUrl = null,
                    readme = null,
                    company = null,
                    location = null,
                    jobTitle = null,
                    githubUrl = null,
                    contactEmail = null,
                    websiteUrl = null,
                )
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = profile,
                    createdAt = null,
                    updatedAt = null,
                )

            When("회원 프로필을 조회하면") {
                val query =
                    GetMemberProfileUseCase.Query(
                        username = "testuser",
                    )

                every { memberRepository.findByUsername(Username("testuser")) } returns member

                val response = service.execute(query)

                Then("필수 필드만 값이 있다") {
                    response.profile?.nickname shouldBe "MinimalNick"
                    response.profile?.bio shouldBe null
                    response.profile?.profileImageUrl shouldBe null
                    response.profile?.readme shouldBe null
                    response.profile?.company shouldBe null
                    response.profile?.location shouldBe null
                    response.profile?.jobTitle shouldBe null
                    response.profile?.githubUrl shouldBe null
                    response.profile?.contactEmail shouldBe null
                    response.profile?.websiteUrl shouldBe null
                }
            }
        }
    })

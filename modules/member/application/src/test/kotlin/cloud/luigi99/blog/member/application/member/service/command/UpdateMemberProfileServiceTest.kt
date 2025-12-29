package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.command.UpdateMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.TechStack
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify

class UpdateMemberProfileServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("프로필이 있는 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = UpdateMemberProfileService(memberRepository)

            val memberId = MemberId.generate()
            val existingProfile =
                Profile.create(
                    nickname = Nickname("OldNick"),
                    bio = null,
                    techStack = TechStack(emptyList()),
                )
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = existingProfile,
                    createdAt = null,
                    updatedAt = null,
                )

            When("프로필을 업데이트하면") {
                val command =
                    UpdateMemberProfileUseCase.Command(
                        memberId = memberId.toString(),
                        nickname = "NewNick",
                        bio = "새로운 자기소개",
                        profileImageUrl = null,
                        jobTitle = "Senior Developer",
                        techStack = listOf("Kotlin", "Spring Boot"),
                        githubUrl = "https://github.com/testuser",
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { memberRepository.findById(memberId) } returns member
                val savedMemberSlot = slot<Member>()
                every { memberRepository.save(capture(savedMemberSlot)) } answers { firstArg() }

                val response = service.execute(command)

                Then("프로필 ID가 반환된다") {
                    response.profileId shouldNotBe null
                }

                Then("닉네임이 업데이트된다") {
                    response.nickname shouldBe "NewNick"
                }

                Then("자기소개가 업데이트된다") {
                    response.bio shouldBe "새로운 자기소개"
                }

                Then("직함이 업데이트된다") {
                    response.jobTitle shouldBe "Senior Developer"
                }

                Then("기술 스택이 업데이트된다") {
                    response.techStack shouldBe listOf("Kotlin", "Spring Boot")
                }

                Then("GitHub URL이 업데이트된다") {
                    response.githubUrl shouldBe "https://github.com/testuser"
                }

                Then("Repository save가 호출된다") {
                    verify(exactly = 1) { memberRepository.save(any()) }
                }

                Then("저장된 회원의 프로필이 업데이트되었다") {
                    val savedMember = savedMemberSlot.captured
                    savedMember.profile
                        ?.nickname
                        ?.value shouldBe "NewNick"
                    savedMember.profile
                        ?.bio
                        ?.value shouldBe "새로운 자기소개"
                }
            }
        }

        Given("프로필이 없는 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = UpdateMemberProfileService(memberRepository)

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

            When("프로필을 새로 생성하면") {
                val command =
                    UpdateMemberProfileUseCase.Command(
                        memberId = memberId.toString(),
                        nickname = "FirstNick",
                        bio = "첫 자기소개",
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = listOf("Kotlin"),
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { memberRepository.findById(memberId) } returns member
                val savedMemberSlot = slot<Member>()
                every { memberRepository.save(capture(savedMemberSlot)) } answers { firstArg() }

                val response = service.execute(command)

                Then("새 프로필이 생성된다") {
                    val savedMember = savedMemberSlot.captured
                    savedMember.profile shouldNotBe null
                }

                Then("닉네임이 설정된다") {
                    response.nickname shouldBe "FirstNick"
                }

                Then("자기소개가 설정된다") {
                    response.bio shouldBe "첫 자기소개"
                }

                Then("기술 스택이 설정된다") {
                    response.techStack shouldBe listOf("Kotlin")
                }
            }
        }

        Given("프로필이 없는 회원이 닉네임 없이 프로필을 생성하려 할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = UpdateMemberProfileService(memberRepository)

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

            When("닉네임 없이 프로필 업데이트를 시도하면") {
                val command =
                    UpdateMemberProfileUseCase.Command(
                        memberId = memberId.toString(),
                        nickname = null,
                        bio = "자기소개",
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = null,
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { memberRepository.findById(memberId) } returns member

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("존재하지 않는 회원 ID로 프로필 업데이트를 시도할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = UpdateMemberProfileService(memberRepository)

            When("존재하지 않는 회원 ID로 업데이트하면") {
                val command =
                    UpdateMemberProfileUseCase.Command(
                        memberId = MemberId.generate().toString(),
                        nickname = "NewNick",
                        bio = null,
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = null,
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { memberRepository.findById(any()) } returns null

                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("프로필의 일부 필드만 업데이트할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = UpdateMemberProfileService(memberRepository)

            val memberId = MemberId.generate()
            val existingProfile =
                Profile.create(
                    nickname = Nickname("OldNick"),
                    bio = null,
                    jobTitle = null,
                    techStack = TechStack(emptyList()),
                )
            val member =
                Member.from(
                    entityId = memberId,
                    email = Email("test@example.com"),
                    username = Username("testuser"),
                    profile = existingProfile,
                    createdAt = null,
                    updatedAt = null,
                )

            When("닉네임만 업데이트하면") {
                val command =
                    UpdateMemberProfileUseCase.Command(
                        memberId = memberId.toString(),
                        nickname = "UpdatedNick",
                        bio = null,
                        profileImageUrl = null,
                        jobTitle = null,
                        techStack = null,
                        githubUrl = null,
                        contactEmail = null,
                        websiteUrl = null,
                    )

                every { memberRepository.findById(memberId) } returns member
                every { memberRepository.save(any()) } answers { firstArg() }

                val response = service.execute(command)

                Then("닉네임만 변경된다") {
                    response.nickname shouldBe "UpdatedNick"
                }

                Then("다른 필드는 기존 값이 유지된다") {
                    response.bio shouldBe null
                    response.jobTitle shouldBe null
                }
            }
        }
    })

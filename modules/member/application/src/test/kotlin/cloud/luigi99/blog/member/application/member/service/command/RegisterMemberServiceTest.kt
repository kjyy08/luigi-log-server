package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.command.RegisterMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.model.Member
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
import io.mockk.slot
import io.mockk.verify

/**
 * RegisterMemberService 테스트
 *
 * Application 레이어 테스트로 다음을 검증합니다:
 * - UseCase 실행 결과
 * - Repository 호출 검증
 * - 도메인 객체 생성 및 저장
 */
class RegisterMemberServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("회원 등록 서비스가 주어졌을 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = RegisterMemberService(memberRepository)

            When("유효한 이메일과 사용자 이름으로 회원을 등록하면") {
                val command =
                    RegisterMemberUseCase.Command(
                        email = "user@example.com",
                        username = "john_doe",
                    )

                val savedMemberSlot = slot<Member>()
                val savedMember =
                    Member.from(
                        entityId = MemberId.generate(),
                        email = Email("user@example.com"),
                        username = Username("john_doe"),
                        profile = Profile.create(Nickname("john_doe")),
                        createdAt = null,
                        updatedAt = null,
                    )

                every { memberRepository.save(capture(savedMemberSlot)) } returns savedMember

                val response = service.execute(command)

                Then("회원 ID가 반환된다") {
                    response.memberId shouldNotBe null
                }

                Then("이메일이 올바르게 반환된다") {
                    response.email shouldBe "user@example.com"
                }

                Then("사용자 이름이 올바르게 반환된다") {
                    response.username shouldBe "john_doe"
                }

                Then("Repository의 save가 호출된다") {
                    verify(exactly = 1) { memberRepository.save(any()) }
                }

                Then("저장된 회원은 프로필을 가지고 있다") {
                    val capturedMember = savedMemberSlot.captured
                    capturedMember.profile shouldNotBe null
                    capturedMember.profile
                        ?.nickname
                        ?.value shouldBe "john_doe"
                }
            }
        }

        Given("회원 등록 시 프로필이 자동 생성되는지 검증") {
            val memberRepository = mockk<MemberRepository>()
            val service = RegisterMemberService(memberRepository)

            When("사용자 이름으로 회원을 등록하면") {
                val command =
                    RegisterMemberUseCase.Command(
                        email = "test@example.com",
                        username = "testuser",
                    )

                val memberSlot = slot<Member>()
                every { memberRepository.save(capture(memberSlot)) } answers {
                    firstArg()
                }

                service.execute(command)

                Then("프로필이 자동으로 생성된다") {
                    val member = memberSlot.captured
                    member.profile shouldNotBe null
                }

                Then("프로필의 닉네임은 사용자 이름과 동일하다") {
                    val member = memberSlot.captured
                    member.profile
                        ?.nickname
                        ?.value shouldBe "testuser"
                }
            }
        }

        Given("여러 회원을 등록할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = RegisterMemberService(memberRepository)

            When("서로 다른 이메일로 회원들을 등록하면") {
                val users =
                    listOf(
                        "user1@example.com" to "user1",
                        "user2@example.com" to "user2",
                        "user3@example.com" to "user3",
                    )

                every { memberRepository.save(any()) } answers { firstArg() }

                val responses =
                    users.map { (email, username) ->
                        service.execute(
                            RegisterMemberUseCase.Command(
                                email = email,
                                username = username,
                            ),
                        )
                    }

                Then("모든 회원이 정상적으로 등록된다") {
                    responses.size shouldBe 3
                }

                Then("각 회원의 정보가 올바르게 반환된다") {
                    responses[0].email shouldBe "user1@example.com"
                    responses[0].username shouldBe "user1"

                    responses[1].email shouldBe "user2@example.com"
                    responses[1].username shouldBe "user2"

                    responses[2].email shouldBe "user3@example.com"
                    responses[2].username shouldBe "user3"
                }

                Then("Repository save가 3번 호출된다") {
                    verify(exactly = 3) { memberRepository.save(any()) }
                }
            }
        }

        Given("도메인 객체 생성 검증") {
            val memberRepository = mockk<MemberRepository>()
            val service = RegisterMemberService(memberRepository)

            When("회원을 등록하면") {
                val command =
                    RegisterMemberUseCase.Command(
                        email = "domain@example.com",
                        username = "domainuser",
                    )

                val memberSlot = slot<Member>()
                every { memberRepository.save(capture(memberSlot)) } answers { firstArg() }

                service.execute(command)

                Then("도메인 Email 값 객체가 생성된다") {
                    val member = memberSlot.captured
                    member.email.value shouldBe "domain@example.com"
                }

                Then("도메인 Username 값 객체가 생성된다") {
                    val member = memberSlot.captured
                    member.username.value shouldBe "domainuser"
                }

                Then("도메인 MemberId가 생성된다") {
                    val member = memberSlot.captured
                    member.entityId shouldNotBe null
                }

                Then("도메인 Profile이 생성된다") {
                    val member = memberSlot.captured
                    member.profile shouldNotBe null
                    member.profile?.entityId shouldNotBe null
                }
            }
        }
    })

package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetCurrentMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class GetCurrentMemberServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetCurrentMemberService(memberRepository)

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

            When("현재 회원 정보를 조회하면") {
                val query =
                    GetCurrentMemberUseCase.Query(
                        memberId = memberId.toString(),
                    )

                every { memberRepository.findById(memberId) } returns member

                val response = service.execute(query)

                Then("회원 ID가 반환된다") {
                    response.memberId shouldBe memberId.toString()
                }

                Then("이메일이 반환된다") {
                    response.email shouldBe "test@example.com"
                }

                Then("사용자 이름이 반환된다") {
                    response.username shouldBe "testuser"
                }

                Then("Repository findById가 호출된다") {
                    verify(exactly = 1) { memberRepository.findById(memberId) }
                }
            }
        }

        Given("존재하지 않는 회원을 조회할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetCurrentMemberService(memberRepository)

            When("존재하지 않는 회원 ID로 조회하면") {
                val query =
                    GetCurrentMemberUseCase.Query(
                        memberId = MemberId.generate().toString(),
                    )

                every { memberRepository.findById(any()) } returns null

                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }

        Given("여러 회원을 조회할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetCurrentMemberService(memberRepository)

            When("각각의 회원 정보를 조회하면") {
                val members =
                    listOf(
                        Triple(MemberId.generate(), "user1@example.com", "user1"),
                        Triple(MemberId.generate(), "user2@example.com", "user2"),
                        Triple(MemberId.generate(), "user3@example.com", "user3"),
                    )

                members.forEach { (memberId, email, username) ->
                    val member =
                        Member.from(
                            entityId = memberId,
                            email = Email(email),
                            username = Username(username),
                            profile = null,
                            createdAt = null,
                            updatedAt = null,
                        )
                    every { memberRepository.findById(memberId) } returns member
                }

                val responses =
                    members.map { (memberId, _, _) ->
                        service.execute(GetCurrentMemberUseCase.Query(memberId.toString()))
                    }

                Then("모든 회원 정보가 올바르게 반환된다") {
                    responses[0].email shouldBe "user1@example.com"
                    responses[0].username shouldBe "user1"

                    responses[1].email shouldBe "user2@example.com"
                    responses[1].username shouldBe "user2"

                    responses[2].email shouldBe "user3@example.com"
                    responses[2].username shouldBe "user3"
                }
            }
        }
    })

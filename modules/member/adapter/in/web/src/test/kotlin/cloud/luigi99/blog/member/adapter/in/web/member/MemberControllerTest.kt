package cloud.luigi99.blog.member.adapter.`in`.web.member

import cloud.luigi99.blog.member.adapter.`in`.web.member.dto.MemberResponse
import cloud.luigi99.blog.member.application.member.port.`in`.command.DeleteMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetCurrentMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.util.UUID

/**
 * MemberController 테스트
 *
 * REST API 엔드포인트의 요청/응답 처리를 검증합니다.
 */
class MemberControllerTest :
    BehaviorSpec({

        val memberQueryFacade = mockk<MemberQueryFacade>()
        val memberCommandFacade = mockk<MemberCommandFacade>()
        val controller = MemberController(memberQueryFacade, memberCommandFacade)

        Given("회원이 로그인한 상태에서") {
            val memberId = UUID.randomUUID().toString()
            val getCurrentMemberUseCase = mockk<GetCurrentMemberUseCase>()

            every { memberQueryFacade.getCurrentMember() } returns getCurrentMemberUseCase

            When("자신의 회원 정보 조회를 요청하면") {
                val expectedResponse =
                    GetCurrentMemberUseCase.Response(
                        memberId = memberId,
                        email = "test@example.com",
                        username = "testuser",
                    )

                every { getCurrentMemberUseCase.execute(any()) } returns expectedResponse

                val response = controller.getCurrentMember(memberId)

                Then("성공 응답과 함께 회원 정보가 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body?.data shouldBe
                        MemberResponse(
                            memberId = memberId,
                            email = "test@example.com",
                            username = "testuser",
                        )
                }
            }
        }

        Given("회원이 서비스를 탈퇴하려고 할 때") {
            val memberId = UUID.randomUUID().toString()
            val deleteMemberUseCase = mockk<DeleteMemberUseCase>()

            every { memberCommandFacade.deleteMember() } returns deleteMemberUseCase

            When("회원 탈퇴를 요청하면") {
                justRun { deleteMemberUseCase.execute(any()) }

                val response = controller.deleteMember(memberId)

                Then("탈퇴가 완료되고 빈 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.NO_CONTENT
                    response.body shouldBe null
                }
            }
        }

        Given("여러 회원이 각자 정보를 조회하려고 할 때") {
            val getCurrentMemberUseCase = mockk<GetCurrentMemberUseCase>()
            every { memberQueryFacade.getCurrentMember() } returns getCurrentMemberUseCase

            When("서로 다른 회원이 자신의 정보 조회를 요청하면") {
                val memberId1 = UUID.randomUUID().toString()
                val memberId2 = UUID.randomUUID().toString()

                every { getCurrentMemberUseCase.execute(GetCurrentMemberUseCase.Query(memberId1)) } returns
                    GetCurrentMemberUseCase.Response(
                        memberId = memberId1,
                        email = "user1@example.com",
                        username = "user1",
                    )

                every { getCurrentMemberUseCase.execute(GetCurrentMemberUseCase.Query(memberId2)) } returns
                    GetCurrentMemberUseCase.Response(
                        memberId = memberId2,
                        email = "user2@example.com",
                        username = "user2",
                    )

                val response1 = controller.getCurrentMember(memberId1)
                val response2 = controller.getCurrentMember(memberId2)

                Then("각 회원에게 맞는 정보가 정확히 반환되어야 한다") {
                    response1.body
                        ?.data
                        ?.memberId shouldBe memberId1
                    response1.body
                        ?.data
                        ?.email shouldBe "user1@example.com"

                    response2.body
                        ?.data
                        ?.memberId shouldBe memberId2
                    response2.body
                        ?.data
                        ?.email shouldBe "user2@example.com"
                }
            }
        }
    })

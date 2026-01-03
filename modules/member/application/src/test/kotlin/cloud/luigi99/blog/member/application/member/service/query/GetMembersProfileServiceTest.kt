package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMembersProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class GetMembersProfileServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("여러 회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMembersProfileService(memberRepository)

            val member1 =
                Member.from(
                    entityId = MemberId.generate(),
                    email = Email("user1@example.com"),
                    username = Username("user1"),
                    profile = Profile.create(Nickname("User One")),
                    createdAt = null,
                    updatedAt = null,
                )

            val member2 =
                Member.from(
                    entityId = MemberId.generate(),
                    email = Email("user2@example.com"),
                    username = Username("user2"),
                    profile = Profile.create(Nickname("User Two")),
                    createdAt = null,
                    updatedAt = null,
                )

            When("여러 회원의 프로필을 조회하면") {
                val query =
                    GetMembersProfileUseCase.Query(
                        memberIds = listOf(member1.entityId.toString(), member2.entityId.toString()),
                    )

                every { memberRepository.findAllById(any()) } returns listOf(member1, member2)

                val response = service.execute(query)

                Then("요청한 모든 회원의 정보가 반환된다") {
                    response.members shouldHaveSize 2
                }

                Then("각 회원의 정보가 정확히 매핑된다") {
                    val result1 = response.members.find { it.memberId == member1.entityId.toString() }!!
                    result1.email shouldBe "user1@example.com"
                    result1.profile?.nickname shouldBe "User One"

                    val result2 = response.members.find { it.memberId == member2.entityId.toString() }!!
                    result2.email shouldBe "user2@example.com"
                    result2.profile?.nickname shouldBe "User Two"
                }
            }
        }

        Given("일부 회원만 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val service = GetMembersProfileService(memberRepository)

            val existingMember =
                Member.from(
                    entityId = MemberId.generate(),
                    email = Email("existing@example.com"),
                    username = Username("existing"),
                    profile = null,
                    createdAt = null,
                    updatedAt = null,
                )

            When("존재하는 회원과 존재하지 않는 회원을 함께 조회하면") {
                val query =
                    GetMembersProfileUseCase.Query(
                        memberIds = listOf(existingMember.entityId.toString(), MemberId.generate().toString()),
                    )

                every { memberRepository.findAllById(any()) } returns listOf(existingMember)

                val response = service.execute(query)

                Then("존재하는 회원의 정보만 반환된다") {
                    response.members shouldHaveSize 1
                    response.members[0].memberId shouldBe existingMember.entityId.toString()
                }
            }
        }
    })

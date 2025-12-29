package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.member.application.member.port.`in`.command.DeleteMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify

class DeleteMemberServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("회원이 존재할 때") {
            val memberRepository = mockk<MemberRepository>()
            val domainEventPublisher = mockk<DomainEventPublisher>()
            val service = DeleteMemberService(memberRepository, domainEventPublisher)

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

            When("회원 탈퇴를 요청하면") {
                val command =
                    DeleteMemberUseCase.Command(
                        memberId = memberId.toString(),
                    )

                every { memberRepository.findById(memberId) } returns member
                every { memberRepository.deleteById(memberId) } just runs
                every { domainEventPublisher.publish(any()) } just runs

                service.execute(command)

                Then("회원이 조회된다") {
                    verify(exactly = 1) { memberRepository.findById(memberId) }
                }

                Then("회원이 삭제된다") {
                    verify(exactly = 1) { memberRepository.deleteById(memberId) }
                }
            }
        }

        Given("존재하지 않는 회원 ID로 탈퇴를 시도할 때") {
            val memberRepository = mockk<MemberRepository>()
            val domainEventPublisher = mockk<DomainEventPublisher>()
            val service = DeleteMemberService(memberRepository, domainEventPublisher)

            When("존재하지 않는 회원 ID로 탈퇴를 요청하면") {
                val command =
                    DeleteMemberUseCase.Command(
                        memberId = MemberId.generate().toString(),
                    )

                every { memberRepository.findById(any()) } returns null

                Then("MemberNotFoundException이 발생한다") {
                    shouldThrow<MemberNotFoundException> {
                        service.execute(command)
                    }
                }

                Then("deleteById는 호출되지 않는다") {
                    verify(exactly = 0) { memberRepository.deleteById(any()) }
                }
            }
        }

        Given("여러 회원을 탈퇴시킬 때") {
            val memberRepository = mockk<MemberRepository>()
            val domainEventPublisher = mockk<DomainEventPublisher>()
            val service = DeleteMemberService(memberRepository, domainEventPublisher)

            When("여러 회원의 탈퇴를 순차적으로 처리하면") {
                val memberIds = List(3) { MemberId.generate() }

                memberIds.forEach { memberId ->
                    val member =
                        Member.from(
                            entityId = memberId,
                            email = Email("user$memberId@example.com"),
                            username = Username("user$memberId"),
                            profile = null,
                            createdAt = null,
                            updatedAt = null,
                        )

                    every { memberRepository.findById(memberId) } returns member
                    every { memberRepository.deleteById(memberId) } just runs
                    every { domainEventPublisher.publish(any()) } just runs

                    service.execute(DeleteMemberUseCase.Command(memberId.toString()))
                }

                Then("모든 회원이 삭제된다") {
                    memberIds.forEach { memberId ->
                        verify(exactly = 1) { memberRepository.deleteById(memberId) }
                    }
                }
            }
        }
    })

package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.member.application.member.port.`in`.command.DeleteMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 회원 탈퇴 서비스
 *
 * 회원 삭제 후 도메인 이벤트를 발행하여 관련 데이터를 삭제합니다.
 */
@Service
class DeleteMemberService(
    private val memberRepository: MemberRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : DeleteMemberUseCase {
    @Transactional
    override fun execute(command: DeleteMemberUseCase.Command) {
        val memberId = MemberId.from(command.memberId)

        log.info { "Deleting member: $memberId" }

        val member =
            memberRepository.findById(memberId)
                ?: throw MemberNotFoundException()

        member.withdraw()
        memberRepository.deleteById(memberId)

        member.getEvents().forEach { event ->
            domainEventPublisher.publish(event)
        }

        log.info { "Successfully deleted member: $memberId" }
    }
}

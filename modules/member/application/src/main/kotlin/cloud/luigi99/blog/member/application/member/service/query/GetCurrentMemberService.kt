package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.member.application.member.port.`in`.query.GetCurrentMemberUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class GetCurrentMemberService(private val memberRepository: MemberRepository) : GetCurrentMemberUseCase {
    override fun execute(query: GetCurrentMemberUseCase.Query): GetCurrentMemberUseCase.Response {
        log.debug { "Fetching member profile for ID: ${query.memberId}" }

        val member =
            memberRepository.findById(MemberId.from(query.memberId))
                ?: throw MemberNotFoundException("Member not found with ID: ${query.memberId}")

        return GetCurrentMemberUseCase.Response(
            memberId =
                member.entityId.value
                    .toString(),
            email = member.email.value,
            username = member.username.value,
        )
    }
}

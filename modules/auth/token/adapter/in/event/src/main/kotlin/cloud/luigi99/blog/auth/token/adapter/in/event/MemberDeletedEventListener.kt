package cloud.luigi99.blog.auth.token.adapter.`in`.event

import cloud.luigi99.blog.auth.token.application.port.`in`.command.RevokeTokenUseCase
import cloud.luigi99.blog.auth.token.application.port.`in`.command.TokenCommandFacade
import cloud.luigi99.blog.member.domain.member.event.MemberDeletedEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 회원 탈퇴 이벤트 리스너
 *
 * 회원이 탈퇴하면 해당 회원의 리프레시 토큰을 무효화합니다.
 */
@Component
class MemberDeletedEventListener(private val tokenCommandFacade: TokenCommandFacade) {
    @EventListener
    @Transactional
    fun handleMemberDeleted(event: MemberDeletedEvent) {
        log.info { "Handling MemberDeletedEvent for member: ${event.memberId}" }

        tokenCommandFacade.revoke().execute(
            RevokeTokenUseCase.Command(
                event.memberId.value
                    .toString(),
            ),
        )

        log.info { "Successfully deleted token for member: ${event.memberId}" }
    }
}

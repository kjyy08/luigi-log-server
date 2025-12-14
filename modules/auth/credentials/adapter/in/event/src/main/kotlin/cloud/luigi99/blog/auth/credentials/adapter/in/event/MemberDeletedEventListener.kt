package cloud.luigi99.blog.auth.credentials.adapter.`in`.event

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CredentialsCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.DeleteCredentialsUseCase
import cloud.luigi99.blog.member.domain.member.event.MemberDeletedEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 회원 탈퇴 이벤트 리스너
 *
 * 회원이 탈퇴하면 해당 회원의 인증 정보를 삭제합니다.
 */
@Component
class MemberDeletedEventListener(private val credentialsCommandFacade: CredentialsCommandFacade) {
    @EventListener
    @Transactional
    fun handleMemberDeleted(event: MemberDeletedEvent) {
        log.info { "Handling MemberDeletedEvent for member: ${event.memberId}" }

        credentialsCommandFacade.delete().execute(
            DeleteCredentialsUseCase.Command(
                event.memberId.value
                    .toString(),
            ),
        )

        log.info { "Successfully deleted credentials for member: ${event.memberId}" }
    }
}

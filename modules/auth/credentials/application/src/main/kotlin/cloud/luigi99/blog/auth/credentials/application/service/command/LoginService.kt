package cloud.luigi99.blog.auth.credentials.application.service.command

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.LoginUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.MemberClient
import cloud.luigi99.blog.auth.credentials.application.port.out.MemberCredentialsRepository
import cloud.luigi99.blog.auth.credentials.domain.enums.OAuthProvider
import cloud.luigi99.blog.auth.credentials.domain.model.MemberCredentials
import cloud.luigi99.blog.auth.credentials.domain.vo.OAuthInfo
import cloud.luigi99.blog.common.application.port.out.DomainEventPublisher
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 로그인 유스케이스 구현체
 *
 * OAuth2 인증 정보를 바탕으로 회원 자격증명을 처리합니다.
 * 회원 생성은 member 모듈에(Gateway 경유), 토큰 발급은 token 모듈에 위임합니다.
 */
@Service
class LoginService(
    private val memberCredentialsRepository: MemberCredentialsRepository,
    private val memberClient: MemberClient,
    private val domainEventPublisher: DomainEventPublisher,
) : LoginUseCase {
    /**
     * 로그인 명령을 실행합니다.
     *
     * 기존 회원이면 자격증명 업데이트를, 신규 회원이면 회원 생성 후 자격증명 생성을 수행합니다.
     *
     * @param command 로그인 요청 명령 객체
     * @return 로그인 응답 객체
     */
    @Transactional
    override fun execute(command: LoginUseCase.Command): LoginUseCase.Response {
        log.info { "Processing OAuth authentication for email: ${command.email}" }

        val oauthInfo =
            OAuthInfo(
                provider = OAuthProvider.from(command.provider),
                providerId = command.providerId,
            )

        val existingCredentials = memberCredentialsRepository.findByOAuthInfo(oauthInfo)

        val credentials =
            existingCredentials?.also {
                it.updateLastLogin()
            } ?: newMember(oauthInfo, command)

        memberCredentialsRepository.save(credentials)

        credentials.getEvents().forEach { domainEventPublisher.publish(it) }

        log.info { "Successfully authenticated member ${credentials.memberId}" }

        return LoginUseCase.Response(
            memberId =
                credentials.memberId.value
                    .toString(),
            email = command.email,
            username = command.username,
            role = credentials.role,
        )
    }

    private fun newMember(oauthInfo: OAuthInfo, command: LoginUseCase.Command): MemberCredentials {
        val response =
            memberClient.execute(
                MemberClient.Request(
                    email = command.email,
                    username = command.username,
                ),
            )

        val newCredentials =
            MemberCredentials.create(
                memberId = MemberId.from(response.memberId),
                oauthInfo = oauthInfo,
            )
        newCredentials.updateLastLogin()

        return newCredentials
    }
}

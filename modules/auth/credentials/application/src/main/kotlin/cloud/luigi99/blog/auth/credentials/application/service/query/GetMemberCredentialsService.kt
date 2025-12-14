package cloud.luigi99.blog.auth.credentials.application.service.query

import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.GetMemberCredentialsUseCase
import cloud.luigi99.blog.auth.credentials.application.port.out.MemberCredentialsRepository
import cloud.luigi99.blog.auth.credentials.domain.exception.MemberCredentialsException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.springframework.stereotype.Service

@Service
class GetMemberCredentialsService(private val memberCredentialsRepository: MemberCredentialsRepository) :
    GetMemberCredentialsUseCase {
    override fun execute(query: GetMemberCredentialsUseCase.Query): GetMemberCredentialsUseCase.Response {
        val memberId = MemberId.from(query.memberId)
        val credentials =
            memberCredentialsRepository.findByMemberId(memberId)
                ?: throw MemberCredentialsException()

        return GetMemberCredentialsUseCase.Response(
            credentialsId =
                credentials.entityId.value
                    .toString(),
            memberId =
                credentials.memberId.value
                    .toString(),
            oauthProvider = credentials.oauthInfo.provider.name,
            oauthProviderId = credentials.oauthInfo.providerId,
            role = credentials.role,
            lastLoginAt = credentials.lastLoginAt?.value,
        )
    }
}

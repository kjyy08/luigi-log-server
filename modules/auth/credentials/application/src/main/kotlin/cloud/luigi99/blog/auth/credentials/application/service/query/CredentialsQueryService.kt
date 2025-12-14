package cloud.luigi99.blog.auth.credentials.application.service.query

import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.CredentialsQueryFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.GetMemberCredentialsUseCase
import org.springframework.stereotype.Service

@Service
class CredentialsQueryService(private val getMemberCredentialsUseCase: GetMemberCredentialsUseCase) :
    CredentialsQueryFacade {
    override fun getMemberCredentials(): GetMemberCredentialsUseCase = getMemberCredentialsUseCase
}

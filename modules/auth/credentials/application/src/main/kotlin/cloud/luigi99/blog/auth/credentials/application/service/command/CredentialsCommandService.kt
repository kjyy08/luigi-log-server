package cloud.luigi99.blog.auth.credentials.application.service.command

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CredentialsCommandFacade
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.DeleteCredentialsUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.LoginUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.UpdateCredentialsUseCase
import org.springframework.stereotype.Service

/**
 * 자격증명 관리 Facade 구현체
 *
 * 로그인, 인증 정보 업데이트, 인증 정보 삭제 유스케이스를 제공합니다.
 */
@Service
class CredentialsCommandService(
    private val loginUseCase: LoginUseCase,
    private val updateCredentialsUseCase: UpdateCredentialsUseCase,
    private val deleteCredentialsUseCase: DeleteCredentialsUseCase,
) : CredentialsCommandFacade {
    override fun login(): LoginUseCase = loginUseCase

    override fun update(): UpdateCredentialsUseCase = updateCredentialsUseCase

    override fun delete(): DeleteCredentialsUseCase = deleteCredentialsUseCase
}

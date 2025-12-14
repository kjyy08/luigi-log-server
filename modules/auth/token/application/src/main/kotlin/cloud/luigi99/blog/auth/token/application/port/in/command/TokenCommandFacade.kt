package cloud.luigi99.blog.auth.token.application.port.`in`.command

interface TokenCommandFacade {
    fun issue(): IssueTokenUseCase

    fun reissue(): ReissueTokenUseCase

    fun revoke(): RevokeTokenUseCase
}

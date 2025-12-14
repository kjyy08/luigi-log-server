package cloud.luigi99.blog.auth.token.application.port.`in`.query

interface TokenQueryFacade {
    fun validate(): ValidateTokenUseCase
}

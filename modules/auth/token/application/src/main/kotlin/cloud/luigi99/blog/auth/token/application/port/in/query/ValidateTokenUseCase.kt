package cloud.luigi99.blog.auth.token.application.port.`in`.query

interface ValidateTokenUseCase {
    fun execute(query: Query): Response

    data class Query(val token: String)

    data class Response(val isValid: Boolean, val memberId: String?, val expiration: Long?)
}

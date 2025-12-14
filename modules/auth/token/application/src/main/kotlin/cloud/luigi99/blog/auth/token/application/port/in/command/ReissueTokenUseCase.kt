package cloud.luigi99.blog.auth.token.application.port.`in`.command

interface ReissueTokenUseCase {
    fun execute(command: Command): Response

    data class Command(val refreshToken: String)

    data class Response(
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiration: Long,
        val refreshTokenExpiration: Long,
    )
}

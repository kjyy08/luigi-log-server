package cloud.luigi99.blog.auth.token.application.port.`in`.command

interface IssueTokenUseCase {
    fun execute(command: Command): Response

    data class Command(val memberId: String)

    data class Response(
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiration: Long,
        val refreshTokenExpiration: Long,
    )
}

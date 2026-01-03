package cloud.luigi99.blog.auth.credentials.application.port.`in`.command

import cloud.luigi99.blog.auth.credentials.domain.enums.Role

interface LoginUseCase {
    fun execute(command: Command): Response

    data class Command(
        val email: String,
        val username: String,
        val provider: String,
        val providerId: String,
        val profileImgUrl: String?,
    )

    data class Response(
        val memberId: String,
        val email: String,
        val username: String,
        val role: Role,
        val profileImgUrl: String?,
    )
}

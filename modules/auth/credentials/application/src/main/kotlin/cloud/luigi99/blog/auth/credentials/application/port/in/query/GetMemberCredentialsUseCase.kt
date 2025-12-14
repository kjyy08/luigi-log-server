package cloud.luigi99.blog.auth.credentials.application.port.`in`.query

import cloud.luigi99.blog.auth.credentials.domain.enums.Role
import java.time.LocalDateTime

interface GetMemberCredentialsUseCase {
    fun execute(query: Query): Response

    data class Query(val memberId: String)

    data class Response(
        val credentialsId: String,
        val memberId: String,
        val oauthProvider: String,
        val oauthProviderId: String,
        val role: Role,
        val lastLoginAt: LocalDateTime?,
    )
}

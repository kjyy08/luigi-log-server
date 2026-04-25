package cloud.luigi99.blog.auth.credentials.application.port.`in`.query

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import java.time.LocalDateTime
import java.util.UUID

interface ListApiKeysUseCase {
    fun execute(query: Query): Response

    data class Query(val ownerMemberId: String)

    data class Response(val apiKeys: List<ApiKeySummary>)

    data class ApiKeySummary(
        val id: UUID,
        val name: String,
        val prefix: String,
        val scopes: Set<ApiKeyScope>,
        val status: ApiKeyStatus,
        val expiresAt: LocalDateTime?,
        val lastUsedAt: LocalDateTime?,
        val createdAt: LocalDateTime,
    )
}

interface AuthenticateApiKeyUseCase {
    fun execute(query: Query): Response?

    data class Query(
        val secretKey: String,
        val path: String?,
    )

    data class Response(
        val ownerMemberId: String,
        val authorities: List<String>,
    )
}

interface ApiKeyQueryFacade {
    fun listApiKeys(): ListApiKeysUseCase

    fun authenticateApiKey(): AuthenticateApiKeyUseCase
}

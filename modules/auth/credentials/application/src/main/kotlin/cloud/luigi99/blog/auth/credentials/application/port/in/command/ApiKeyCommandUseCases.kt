package cloud.luigi99.blog.auth.credentials.application.port.`in`.command

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import java.time.LocalDateTime
import java.util.UUID

interface CreateApiKeyUseCase {
    fun execute(command: Command): Response

    data class Command(
        val ownerMemberId: String,
        val name: String,
        val scopes: Set<String>,
        val expiresAt: LocalDateTime?,
    )

    data class Response(
        val id: UUID,
        val name: String,
        val prefix: String,
        val scopes: Set<ApiKeyScope>,
        val status: ApiKeyStatus,
        val expiresAt: LocalDateTime?,
        val lastUsedAt: LocalDateTime?,
        val createdAt: LocalDateTime,
        val secretKey: String,
    )
}

interface RevokeApiKeyUseCase {
    fun execute(command: Command)

    data class Command(val ownerMemberId: String, val apiKeyId: UUID)
}

interface ApiKeyCommandFacade {
    fun createApiKey(): CreateApiKeyUseCase

    fun revokeApiKey(): RevokeApiKeyUseCase
}

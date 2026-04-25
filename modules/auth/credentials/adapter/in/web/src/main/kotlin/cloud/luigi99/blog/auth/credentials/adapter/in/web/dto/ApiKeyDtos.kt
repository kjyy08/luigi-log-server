package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.dto

import cloud.luigi99.blog.auth.credentials.application.port.`in`.command.CreateApiKeyUseCase
import cloud.luigi99.blog.auth.credentials.application.port.`in`.query.ListApiKeysUseCase
import java.time.LocalDateTime
import java.util.UUID

data class CreateApiKeyRequest(
    val name: String,
    val scopes: Set<String>,
    val expiresAt: LocalDateTime? = null,
)

data class ApiKeyResponse(
    val id: UUID,
    val name: String,
    val prefix: String,
    val scopes: Set<String>,
    val status: String,
    val expiresAt: LocalDateTime?,
    val lastUsedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
)

data class CreateApiKeyResponse(
    val id: UUID,
    val name: String,
    val prefix: String,
    val scopes: Set<String>,
    val status: String,
    val expiresAt: LocalDateTime?,
    val lastUsedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val secretKey: String,
) {
    companion object {
        fun from(response: CreateApiKeyUseCase.Response): CreateApiKeyResponse =
            CreateApiKeyResponse(
                id = response.id,
                name = response.name,
                prefix = response.prefix,
                scopes = response.scopes.map { it.value }.toSet(),
                status = response.status.name,
                expiresAt = response.expiresAt,
                lastUsedAt = response.lastUsedAt,
                createdAt = response.createdAt,
                secretKey = response.secretKey,
            )
    }
}

data class ApiKeyListResponse(val apiKeys: List<ApiKeyResponse>) {
    companion object {
        fun from(response: ListApiKeysUseCase.Response): ApiKeyListResponse =
            ApiKeyListResponse(
                apiKeys =
                    response.apiKeys.map {
                        ApiKeyResponse(
                            id = it.id,
                            name = it.name,
                            prefix = it.prefix,
                            scopes = it.scopes.map { scope -> scope.value }.toSet(),
                            status = it.status.name,
                            expiresAt = it.expiresAt,
                            lastUsedAt = it.lastUsedAt,
                            createdAt = it.createdAt,
                        )
                    },
            )
    }
}

package cloud.luigi99.blog.auth.credentials.domain.model

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime
import java.util.UUID

class ApiKey private constructor(
    val id: UUID,
    val ownerMemberId: MemberId,
    val name: String,
    val prefix: String,
    val keyHash: String,
    val scopes: Set<ApiKeyScope>,
    var status: ApiKeyStatus,
    val expiresAt: LocalDateTime?,
    var lastUsedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
) {
    val active: Boolean
        get() = status == ApiKeyStatus.ACTIVE && !isExpired()

    fun revoke(now: LocalDateTime = LocalDateTime.now()) {
        status = ApiKeyStatus.REVOKED
        updatedAt = now
    }

    fun recordUsedAt(now: LocalDateTime = LocalDateTime.now()) {
        lastUsedAt = now
        updatedAt = now
    }

    fun isExpired(now: LocalDateTime = LocalDateTime.now()): Boolean = expiresAt?.let { !it.isAfter(now) } ?: false

    companion object {
        fun create(
            ownerMemberId: MemberId,
            name: String,
            prefix: String,
            keyHash: String,
            scopes: Set<ApiKeyScope>,
            expiresAt: LocalDateTime?,
            now: LocalDateTime = LocalDateTime.now(),
        ): ApiKey =
            ApiKey(
                id = UUID.randomUUID(),
                ownerMemberId = ownerMemberId,
                name = name,
                prefix = prefix,
                keyHash = keyHash,
                scopes = scopes,
                status = ApiKeyStatus.ACTIVE,
                expiresAt = expiresAt,
                lastUsedAt = null,
                createdAt = now,
                updatedAt = now,
            )

        fun restore(
            id: UUID,
            ownerMemberId: MemberId,
            name: String,
            prefix: String,
            keyHash: String,
            scopes: Set<ApiKeyScope>,
            status: ApiKeyStatus,
            expiresAt: LocalDateTime?,
            lastUsedAt: LocalDateTime?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ): ApiKey =
            ApiKey(
                id = id,
                ownerMemberId = ownerMemberId,
                name = name,
                prefix = prefix,
                keyHash = keyHash,
                scopes = scopes,
                status = status,
                expiresAt = expiresAt,
                lastUsedAt = lastUsedAt,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
    }
}

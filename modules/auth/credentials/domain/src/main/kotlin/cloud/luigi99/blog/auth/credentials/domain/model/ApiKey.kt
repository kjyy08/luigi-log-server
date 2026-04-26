package cloud.luigi99.blog.auth.credentials.domain.model

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyStatus
import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

class ApiKey private constructor(
    override val entityId: ApiKeyId,
    val ownerMemberId: MemberId,
    val name: String,
    val prefix: String,
    val keyHash: String,
    val scopes: Set<ApiKeyScope>,
    var status: ApiKeyStatus,
    val expiresAt: LocalDateTime?,
    var lastUsedAt: LocalDateTime?,
    override var createdAt: LocalDateTime?,
    override var updatedAt: LocalDateTime?,
) : AggregateRoot<ApiKeyId>() {
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
                entityId = ApiKeyId.generate(),
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
            entityId: ApiKeyId,
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
                entityId = entityId,
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

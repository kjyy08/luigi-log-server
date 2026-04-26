package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.apikey

import cloud.luigi99.blog.auth.credentials.domain.enums.ApiKeyScope
import cloud.luigi99.blog.auth.credentials.domain.model.ApiKey
import cloud.luigi99.blog.auth.credentials.domain.vo.ApiKeyId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

object ApiKeyMapper {
    fun toEntity(apiKey: ApiKey): ApiKeyJpaEntity =
        ApiKeyJpaEntity(
            id = apiKey.entityId.value,
            ownerMemberId = apiKey.ownerMemberId.value,
            name = apiKey.name,
            prefix = apiKey.prefix,
            keyHash = apiKey.keyHash,
            scopes = apiKey.scopes.joinToString(",") { it.value },
            status = apiKey.status,
            expiresAt = apiKey.expiresAt,
            lastUsedAt = apiKey.lastUsedAt,
            createdAt = requireNotNull(apiKey.createdAt) { "ApiKey createdAt must be set before persistence" },
            updatedAt = requireNotNull(apiKey.updatedAt) { "ApiKey updatedAt must be set before persistence" },
        )

    fun toDomain(entity: ApiKeyJpaEntity): ApiKey =
        ApiKey.restore(
            entityId = ApiKeyId(entity.id),
            ownerMemberId = MemberId(entity.ownerMemberId),
            name = entity.name,
            prefix = entity.prefix,
            keyHash = entity.keyHash,
            scopes =
                entity.scopes
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { ApiKeyScope.from(it) }
                    .toSet(),
            status = entity.status,
            expiresAt = entity.expiresAt,
            lastUsedAt = entity.lastUsedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}

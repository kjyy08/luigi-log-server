package cloud.luigi99.blog.auth.token.adapter.out.persistence.redis.authtoken

import cloud.luigi99.blog.auth.token.domain.model.AuthToken
import cloud.luigi99.blog.auth.token.domain.vo.TokenId
import cloud.luigi99.blog.member.domain.member.vo.MemberId

object AuthTokenMapper {
    fun toDomain(entity: AuthTokenRedisEntity): AuthToken =
        AuthToken.from(
            entityId = TokenId(entity.id),
            memberId =
                MemberId(
                    java.util.UUID
                        .fromString(entity.memberId),
                ),
            token = entity.token,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: AuthToken): AuthTokenRedisEntity =
        AuthTokenRedisEntity.from(
            entityId = domain.entityId,
            memberId = domain.memberId,
            token = domain.token,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}

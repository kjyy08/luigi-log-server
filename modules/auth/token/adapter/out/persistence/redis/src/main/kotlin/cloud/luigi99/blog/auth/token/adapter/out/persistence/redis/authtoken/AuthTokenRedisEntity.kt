package cloud.luigi99.blog.auth.token.adapter.out.persistence.redis.authtoken

import cloud.luigi99.blog.adapter.persistence.redis.RedisDomainEntity
import cloud.luigi99.blog.auth.token.domain.vo.TokenId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@RedisHash("auth_token")
data class AuthTokenRedisEntity(
    @Id
    val memberId: String,
    val id: UUID,
    val token: String,
    val expiresAt: LocalDateTime,
) : RedisDomainEntity<TokenId>() {
    override val entityId: TokenId get() = TokenId(id)

    companion object {
        fun from(
            entityId: TokenId,
            memberId: MemberId,
            token: String,
            expiresAt: LocalDateTime,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): AuthTokenRedisEntity {
            val now = LocalDateTime.now()
            val ttlSeconds =
                Duration
                    .between(now, expiresAt)
                    .seconds
                    .coerceAtLeast(0)

            return AuthTokenRedisEntity(
                memberId = memberId.value.toString(),
                id = entityId.value,
                token = token,
                expiresAt = expiresAt,
            ).apply {
                this.ttl = ttlSeconds
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}

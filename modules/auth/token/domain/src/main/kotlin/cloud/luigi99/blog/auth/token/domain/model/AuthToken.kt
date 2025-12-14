package cloud.luigi99.blog.auth.token.domain.model

import cloud.luigi99.blog.auth.token.domain.vo.TokenId
import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

/**
 * 리프레시 토큰(AuthToken) 도메인 엔티티
 *
 * 인증 토큰 갱신을 위한 리프레시 토큰 정보를 관리합니다.
 *
 * @property entityId 토큰 ID
 * @property memberId 회원 ID
 * @property token 토큰 값
 * @property expiresAt 만료 일시
 */
class AuthToken private constructor(
    override val entityId: TokenId,
    val memberId: MemberId,
    val token: String,
    val expiresAt: LocalDateTime,
) : AggregateRoot<TokenId>() {
    enum class TokenStatus {
        VALID,
        THEFT_DETECTED,
        EXPIRED,
    }

    companion object {
        /**
         * 새로운 리프레시 토큰을 발행합니다.
         *
         * @param memberId 회원 ID
         * @param token 토큰 값
         * @param expirationMillis 만료 시간 (밀리초)
         * @return 생성된 리프레시 토큰
         */
        fun issue(memberId: MemberId, token: String, expirationMillis: Long): AuthToken {
            val now = LocalDateTime.now()
            return AuthToken(
                entityId = TokenId.generate(),
                memberId = memberId,
                token = token,
                expiresAt = now.plusNanos(expirationMillis * 1_000_000),
            )
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: TokenId,
            memberId: MemberId,
            token: String,
            expiresAt: LocalDateTime,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): AuthToken {
            val authToken =
                AuthToken(
                    entityId = entityId,
                    memberId = memberId,
                    token = token,
                    expiresAt = expiresAt,
                )
            authToken.createdAt = createdAt
            authToken.updatedAt = updatedAt
            return authToken
        }
    }

    /**
     * 토큰 교체(Rotation) 유효성을 검증합니다.
     */
    fun validateRotation(candidateToken: String): TokenStatus {
        if (this.token != candidateToken) {
            return TokenStatus.THEFT_DETECTED
        }
        if (isExpired()) {
            return TokenStatus.EXPIRED
        }
        return TokenStatus.VALID
    }

    /**
     * 토큰 만료 여부를 확인합니다.
     */
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    /**
     * 토큰 유효성을 확인합니다.
     */
    fun isValid(): Boolean = !isExpired()
}

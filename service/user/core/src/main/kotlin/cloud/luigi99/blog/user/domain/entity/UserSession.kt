package cloud.luigi99.blog.user.domain.entity

import cloud.luigi99.blog.common.domain.BaseEntity
import cloud.luigi99.blog.user.domain.vo.UserId
import java.time.LocalDateTime
import java.util.*

/**
 * 사용자 세션 엔티티
 *
 * 사용자의 로그인 세션 정보를 관리하는 엔티티입니다.
 * JWT 리프레시 토큰과 만료 시간, 디바이스 정보를 포함합니다.
 *
 * @property entityId 세션 식별자
 * @property userId 사용자 ID
 * @property refreshToken 리프레시 토큰
 * @property expiresAt 만료 시각
 * @property deviceInfo 디바이스 정보 (User-Agent 등)
 * @property ipAddress IP 주소
 * @property createdAt 생성 시각
 * @property updatedAt 수정 시각
 */
data class UserSession(
    override val entityId: SessionId,
    val userId: UserId,
    val refreshToken: String,
    val expiresAt: LocalDateTime,
    val deviceInfo: String? = null,
    val ipAddress: String? = null,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null
) : BaseEntity<SessionId>() {

    /**
     * 세션이 만료되었는지 확인합니다.
     *
     * @return 만료되었으면 true, 그렇지 않으면 false
     */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    /**
     * 세션이 유효한지 확인합니다.
     *
     * @return 유효하면 true, 그렇지 않으면 false
     */
    fun isValid(): Boolean {
        return !isExpired()
    }

    /**
     * 세션을 갱신합니다.
     * 새로운 리프레시 토큰과 만료 시간으로 세션을 업데이트합니다.
     *
     * @param newRefreshToken 새로운 리프레시 토큰
     * @param newExpiresAt 새로운 만료 시각
     * @return 갱신된 UserSession 인스턴스
     */
    fun renew(newRefreshToken: String, newExpiresAt: LocalDateTime): UserSession {
        require(newRefreshToken.isNotBlank()) {
            "리프레시 토큰은 필수값입니다"
        }

        require(newExpiresAt.isAfter(LocalDateTime.now())) {
            "만료 시각은 현재 시각 이후여야 합니다"
        }

        return copy(
            refreshToken = newRefreshToken,
            expiresAt = newExpiresAt,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 만료까지 남은 시간을 계산합니다.
     *
     * @return 만료까지 남은 시간 (초)
     */
    fun remainingTimeInSeconds(): Long {
        val now = LocalDateTime.now()
        if (now.isAfter(expiresAt)) {
            return 0L
        }

        return java.time.Duration.between(now, expiresAt).seconds
    }

    companion object {
        /**
         * 새로운 UserSession을 생성합니다.
         *
         * @param userId 사용자 ID
         * @param refreshToken 리프레시 토큰
         * @param expiresAt 만료 시각
         * @param deviceInfo 디바이스 정보 (선택)
         * @param ipAddress IP 주소 (선택)
         * @return 새로운 UserSession 인스턴스
         */
        fun create(
            userId: UserId,
            refreshToken: String,
            expiresAt: LocalDateTime,
            deviceInfo: String? = null,
            ipAddress: String? = null
        ): UserSession {
            require(refreshToken.isNotBlank()) {
                "리프레시 토큰은 필수값입니다"
            }

            require(expiresAt.isAfter(LocalDateTime.now())) {
                "만료 시각은 현재 시각 이후여야 합니다"
            }

            return UserSession(
                entityId = SessionId.generate(),
                userId = userId,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
                deviceInfo = deviceInfo,
                ipAddress = ipAddress,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }
    }
}

/**
 * 세션 식별자 값 객체
 *
 * UserSession의 고유 식별자를 나타내는 값 객체입니다.
 *
 * @property value UUID 값
 */
data class SessionId(
    val value: UUID
) : cloud.luigi99.blog.common.domain.ValueObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SessionId) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }

    /**
     * 유효성 검증
     * UUID는 생성자에서 이미 유효성이 보장되므로 항상 유효합니다.
     *
     * @return 항상 true
     */
    override fun isValid(): Boolean {
        return true
    }

    companion object {
        /**
         * 새로운 SessionId를 생성합니다.
         *
         * @return 새로운 SessionId 인스턴스
         */
        fun generate(): SessionId {
            return SessionId(UUID.randomUUID())
        }

        /**
         * 문자열로부터 SessionId를 생성합니다.
         *
         * @param value UUID 문자열
         * @return SessionId 인스턴스
         * @throws IllegalArgumentException UUID 형식이 올바르지 않은 경우
         */
        fun from(value: String): SessionId {
            return try {
                SessionId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid SessionId format: $value", e)
            }
        }
    }
}

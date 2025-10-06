package cloud.luigi99.blog.user.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.util.*

/**
 * 사용자 식별자 값 객체
 *
 * User 애그리게이트의 고유 식별자를 나타내는 값 객체입니다.
 * UUID를 기반으로 전역적으로 고유한 식별자를 보장합니다.
 *
 * 주요 특징:
 * - 불변성: 생성 후 변경 불가
 * - 전역 고유성: UUID를 사용하여 충돌 방지
 * - 타입 안전성: String이 아닌 UserId 타입으로 명시적 구분
 *
 * @property value UUID 값
 */
data class UserId(
    val value: UUID
) : ValueObject() {

    /**
     * 값 동등성 비교
     * UUID 값이 같으면 동일한 UserId로 간주합니다.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserId) return false
        return value == other.value
    }

    /**
     * 해시코드 생성
     * UUID 값을 기반으로 해시코드를 생성합니다.
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * 문자열 표현
     * UUID 값을 문자열로 반환합니다.
     */
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
         * 새로운 UserId를 생성합니다.
         * 랜덤 UUID를 사용하여 고유한 식별자를 생성합니다.
         *
         * @return 새로운 UserId 인스턴스
         */
        fun generate(): UserId {
            return UserId(UUID.randomUUID())
        }

        /**
         * 문자열로부터 UserId를 생성합니다.
         *
         * @param value UUID 문자열
         * @return UserId 인스턴스
         * @throws IllegalArgumentException UUID 형식이 올바르지 않은 경우
         */
        fun from(value: String): UserId {
            return try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid UserId format: $value", e)
            }
        }
    }
}

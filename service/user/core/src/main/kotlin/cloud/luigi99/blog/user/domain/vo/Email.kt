package cloud.luigi99.blog.user.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 이메일 주소 값 객체
 *
 * 사용자의 이메일 주소를 나타내는 값 객체입니다.
 * 이메일 형식 유효성 검증과 도메인 추출 기능을 제공합니다.
 *
 * 주요 특징:
 * - 불변성: 생성 후 변경 불가
 * - 유효성 검증: 이메일 형식 자동 검증
 * - 정규화: 소문자로 통일
 *
 * @property value 이메일 주소 문자열
 */
data class Email(
    val value: String
) : ValueObject() {

    init {
        validate()
    }

    /**
     * 이메일 도메인을 추출합니다.
     *
     * @return 이메일 도메인 (예: "example.com")
     */
    fun domain(): String {
        return value.substringAfter("@")
    }

    /**
     * 이메일 로컬 파트를 추출합니다.
     *
     * @return 이메일 로컬 파트 (예: "user")
     */
    fun localPart(): String {
        return value.substringBefore("@")
    }

    /**
     * 값 동등성 비교
     * 이메일 값이 같으면 동일한 Email로 간주합니다.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Email) return false
        return value == other.value
    }

    /**
     * 해시코드 생성
     * 이메일 값을 기반으로 해시코드를 생성합니다.
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * 문자열 표현
     * 이메일 값을 문자열로 반환합니다.
     */
    override fun toString(): String {
        return value
    }

    /**
     * 이메일 유효성 검증
     * 이메일 형식이 올바른지 확인합니다.
     *
     * @return 유효하면 true, 그렇지 않으면 false
     */
    override fun isValid(): Boolean {
        return EMAIL_REGEX.matches(value)
    }

    companion object {
        /**
         * 이메일 형식 검증을 위한 정규식
         * RFC 5322 표준을 기반으로 한 간소화된 패턴
         */
        private val EMAIL_REGEX = Regex(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
        )

        /**
         * 문자열로부터 Email을 생성합니다.
         * 이메일 주소를 소문자로 정규화합니다.
         *
         * @param value 이메일 주소 문자열
         * @return Email 인스턴스
         * @throws IllegalArgumentException 이메일 형식이 올바르지 않은 경우
         */
        fun of(value: String): Email {
            require(value.isNotBlank()) {
                "${ErrorCode.COMMON_REQUIRED_VALUE_MISSING.description}: 이메일은 필수값입니다"
            }

            val normalizedEmail = value.trim().lowercase()
            return Email(normalizedEmail)
        }
    }
}

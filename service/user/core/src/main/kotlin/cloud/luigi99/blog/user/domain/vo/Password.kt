package cloud.luigi99.blog.user.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 비밀번호 값 객체
 *
 * 사용자의 비밀번호를 나타내는 값 객체입니다.
 * 비밀번호 정책 검증과 암호화된 값 저장 기능을 제공합니다.
 *
 * 비밀번호 정책:
 * - 최소 8자 이상
 * - 최소 1개 이상의 특수문자 포함
 * - 최소 1개 이상의 숫자 포함
 * - 최소 1개 이상의 영문자 포함
 *
 * 주요 특징:
 * - 불변성: 생성 후 변경 불가
 * - 보안성: 암호화된 값만 저장 (평문 비밀번호는 저장하지 않음)
 * - 유효성 검증: 비밀번호 정책 자동 검증
 *
 * @property value 암호화된 비밀번호
 */
data class Password (
    val value: String
) : ValueObject() {

    /**
     * 값 동등성 비교
     * 암호화된 비밀번호 값이 같으면 동일한 Password로 간주합니다.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Password) return false
        return value == other.value
    }

    /**
     * 해시코드 생성
     * 암호화된 비밀번호 값을 기반으로 해시코드를 생성합니다.
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * 문자열 표현
     * 보안을 위해 실제 값 대신 마스킹된 문자열을 반환합니다.
     */
    override fun toString(): String {
        return "********"
    }

    /**
     * 유효성 검증
     * 암호화된 비밀번호는 이미 검증을 거친 값이므로 항상 유효합니다.
     * 평문 비밀번호의 검증은 validateRawPassword() 메서드를 사용합니다.
     *
     * @return 항상 true
     */
    override fun isValid(): Boolean {
        return true
    }

    companion object {
        /**
         * 최소 비밀번호 길이
         */
        private const val MIN_LENGTH = 8

        /**
         * 특수문자 패턴
         */
        private val SPECIAL_CHAR_REGEX = Regex("[!@#\$%^&*(),.?\":{}|<>]")

        /**
         * 숫자 패턴
         */
        private val DIGIT_REGEX = Regex("\\d")

        /**
         * 영문자 패턴
         */
        private val LETTER_REGEX = Regex("[a-zA-Z]")

        /**
         * 암호화된 비밀번호로부터 Password를 생성합니다.
         * 이미 암호화된 값을 사용할 때 사용합니다 (예: DB에서 조회한 경우).
         *
         * @param encodedPassword 암호화된 비밀번호
         * @return Password 인스턴스
         */
        fun fromEncoded(encodedPassword: String): Password {
            require(encodedPassword.isNotBlank()) {
                "${ErrorCode.COMMON_REQUIRED_VALUE_MISSING.description}: 비밀번호는 필수값입니다"
            }
            return Password(encodedPassword)
        }

        /**
         * 평문 비밀번호의 유효성을 검증합니다.
         * Password 객체를 생성하기 전에 평문 비밀번호가 정책을 만족하는지 확인합니다.
         *
         * @param rawPassword 평문 비밀번호
         * @throws IllegalArgumentException 비밀번호 정책을 만족하지 않는 경우
         */
        fun validateRawPassword(rawPassword: String) {
            require(rawPassword.isNotBlank()) {
                "${ErrorCode.COMMON_REQUIRED_VALUE_MISSING.description}: 비밀번호는 필수값입니다"
            }

            require(rawPassword.length >= MIN_LENGTH) {
                "${ErrorCode.VALIDATION_INVALID_PASSWORD.description}: 비밀번호는 최소 $MIN_LENGTH 자 이상이어야 합니다"
            }

            require(SPECIAL_CHAR_REGEX.containsMatchIn(rawPassword)) {
                "${ErrorCode.VALIDATION_INVALID_PASSWORD.description}: 비밀번호는 최소 1개 이상의 특수문자를 포함해야 합니다"
            }

            require(DIGIT_REGEX.containsMatchIn(rawPassword)) {
                "${ErrorCode.VALIDATION_INVALID_PASSWORD.description}: 비밀번호는 최소 1개 이상의 숫자를 포함해야 합니다"
            }

            require(LETTER_REGEX.containsMatchIn(rawPassword)) {
                "${ErrorCode.VALIDATION_INVALID_PASSWORD.description}: 비밀번호는 최소 1개 이상의 영문자를 포함해야 합니다"
            }
        }

        /**
         * 평문 비밀번호를 암호화하여 Password를 생성합니다.
         * 비밀번호 정책 검증 후 암호화된 Password 객체를 반환합니다.
         *
         * 참고: 실제 암호화는 인프라 계층의 PasswordEncoder를 사용해야 합니다.
         * 이 메서드는 도메인 계층에서 사용하기 위한 팩토리 메서드이며,
         * 실제 사용 시 인프라 계층에서 암호화된 값을 전달받아야 합니다.
         *
         * @param rawPassword 평문 비밀번호
         * @param encode 암호화 함수 (PasswordEncoder.encode)
         * @return Password 인스턴스
         */
        fun create(rawPassword: String, encode: (String) -> String): Password {
            validateRawPassword(rawPassword)
            val encodedPassword = encode(rawPassword)
            return Password(encodedPassword)
        }
    }
}

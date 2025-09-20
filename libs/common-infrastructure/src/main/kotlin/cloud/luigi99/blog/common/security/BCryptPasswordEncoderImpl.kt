package cloud.luigi99.blog.common.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * BCrypt 알고리즘을 사용한 비밀번호 암호화 구현체
 *
 * Spring Security의 BCryptPasswordEncoder를 래핑하여
 * 프로젝트의 PasswordEncoder 인터페이스를 구현합니다.
 * - 보안 강도: 12 라운드 (높은 보안 수준)
 * - 솔트 자동 생성
 * - 시간 공격 방지
 */
@Component
class BCryptPasswordEncoderImpl : PasswordEncoder {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder(STRENGTH)

    /**
     * 원본 비밀번호를 BCrypt 해시로 암호화합니다.
     *
     * @param rawPassword 원본 비밀번호
     * @return BCrypt 해시 문자열
     */
    override fun encode(rawPassword: String): String {
        require(rawPassword.isNotBlank()) { "비밀번호는 공백일 수 없습니다." }
        require(rawPassword.length >= MIN_PASSWORD_LENGTH) {
            "비밀번호는 최소 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다."
        }
        require(rawPassword.length <= MAX_PASSWORD_LENGTH) {
            "비밀번호는 최대 ${MAX_PASSWORD_LENGTH}자 이하여야 합니다."
        }

        return bCryptPasswordEncoder.encode(rawPassword)
    }

    /**
     * 원본 비밀번호와 암호화된 비밀번호가 일치하는지 확인합니다.
     *
     * @param rawPassword 원본 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @return 일치 여부
     */
    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        require(rawPassword.isNotBlank()) { "비밀번호는 공백일 수 없습니다." }
        require(encodedPassword.isNotBlank()) { "암호화된 비밀번호는 공백일 수 없습니다." }

        return try {
            bCryptPasswordEncoder.matches(rawPassword, encodedPassword)
        } catch (e: Exception) {
            // 잘못된 해시 형식이나 기타 오류 시 false 반환
            false
        }
    }

    companion object {
        /**
         * BCrypt 암호화 강도 (라운드 수)
         * 12 라운드는 높은 보안 수준을 제공하면서도 성능을 고려한 값입니다.
         */
        private const val STRENGTH = 12

        /**
         * 최소 비밀번호 길이
         */
        private const val MIN_PASSWORD_LENGTH = 8

        /**
         * 최대 비밀번호 길이 (BCrypt 제한: 72바이트)
         */
        private const val MAX_PASSWORD_LENGTH = 72
    }
}
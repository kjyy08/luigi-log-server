package cloud.luigi99.blog.common.security

/**
 * JWT 토큰 타입을 정의하는 enum 클래스
 */
enum class TokenType(val value: String) {
    /**
     * 액세스 토큰 - API 요청 시 사용
     */
    ACCESS("access"),

    /**
     * 리프레시 토큰 - 액세스 토큰 갱신 시 사용
     */
    REFRESH("refresh");

    companion object {
        /**
         * 문자열 값으로부터 TokenType을 찾는다
         * @param value 토큰 타입 문자열
         * @return 해당하는 TokenType, 없으면 null
         */
        fun fromValue(value: String): TokenType? {
            return values().find { it.value == value }
        }

        /**
         * 문자열 값으로부터 TokenType을 찾는다 (예외 발생)
         * @param value 토큰 타입 문자열
         * @return 해당하는 TokenType
         * @throws IllegalArgumentException 유효하지 않은 토큰 타입인 경우
         */
        fun fromValueOrThrow(value: String): TokenType {
            return fromValue(value) ?: throw IllegalArgumentException("유효하지 않은 토큰 타입: $value")
        }
    }
}
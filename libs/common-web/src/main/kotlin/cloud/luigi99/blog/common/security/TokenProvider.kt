package cloud.luigi99.blog.common.security

interface TokenProvider {
    fun generateAccessToken(userId: Long, authorities: List<String>): String
    fun generateRefreshToken(userId: Long): String

    /**
     * 토큰의 기본적인 유효성을 검증 (서명, 만료 시간 등)
     */
    fun validateToken(token: String): Boolean

    /**
     * 액세스 토큰인지 검증 (타입 + 기본 유효성)
     */
    fun validateAccessToken(token: String): Boolean

    /**
     * 리프레시 토큰인지 검증 (타입 + 기본 유효성)
     */
    fun validateRefreshToken(token: String): Boolean

    /**
     * 토큰의 타입을 추출
     * @param token JWT 토큰
     * @return 토큰 타입
     * @throws IllegalArgumentException 유효하지 않은 토큰인 경우
     */
    fun getTokenType(token: String): TokenType

    fun getUserIdFromToken(token: String): Long
    fun getAuthoritiesFromToken(token: String): List<String>
    fun getExpirationFromToken(token: String): Long
    fun isTokenExpired(token: String): Boolean
}
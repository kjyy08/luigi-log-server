package cloud.luigi99.blog.common.security

interface TokenProvider {
    fun generateAccessToken(userId: Long, authorities: List<String>): String
    fun generateRefreshToken(userId: Long): String
    fun validateToken(token: String): Boolean
    fun getUserIdFromToken(token: String): Long
    fun getAuthoritiesFromToken(token: String): List<String>
    fun getExpirationFromToken(token: String): Long
    fun isTokenExpired(token: String): Boolean
}
package cloud.luigi99.blog.common.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import java.util.*

@Component
class JwtTokenProvider {
    
    @Value("\${jwt.secret:my-secret-key-for-jwt-token-generation-must-be-long}")
    private lateinit var secret: String
    
    @Value("\${jwt.access-token-validity:86400000}") // 24 hours
    private var accessTokenValidityMillis: Long = 86400000
    
    @Value("\${jwt.refresh-token-validity:2592000000}") // 30 days  
    private var refreshTokenValidityMillis: Long = 2592000000
    
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }
    
    fun generateAccessToken(username: String): String {
        return generateToken(username, accessTokenValidityMillis)
    }
    
    fun generateRefreshToken(username: String): String {
        return generateToken(username, refreshTokenValidityMillis)
    }
    
    private fun generateToken(username: String, validityMillis: Long): String {
        val now = Date()
        val validity = Date(now.time + validityMillis)
        
        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }
    
    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            claims.subject
        } catch (e: JwtException) {
            null
        }
    }
    
    fun isTokenValid(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            false
        }
    }
}
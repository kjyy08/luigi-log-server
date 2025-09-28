package cloud.luigi99.blog.common.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @field:Value("\${jwt.secret}")
    private val secretKey: String,
    @field:Value("\${jwt.access-token-validity}")
    private val accessTokenValidityInMilliseconds: Long,
    @field:Value("\${jwt.refresh-token-validity}")
    private val refreshTokenValidityInMilliseconds: Long
) : TokenProvider {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    override fun generateAccessToken(userId: Long, authorities: List<String>): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenValidityInMilliseconds)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("authorities", authorities)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    override fun generateRefreshToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenValidityInMilliseconds)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    override fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (ex: SecurityException) {
            false
        } catch (ex: MalformedJwtException) {
            false
        } catch (ex: ExpiredJwtException) {
            false
        } catch (ex: UnsupportedJwtException) {
            false
        } catch (ex: IllegalArgumentException) {
            false
        }
    }

    override fun getUserIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.subject.toLong()
    }

    override fun getAuthoritiesFromToken(token: String): List<String> {
        val claims = getClaims(token)
        val authorities = claims.get("authorities", List::class.java)
        return if (authorities is List<*>) {
            authorities.filterIsInstance<String>()
        } else {
            emptyList()
        }
    }

    override fun getExpirationFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.expiration.time
    }

    override fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationFromToken(token)
        return Date(expiration).before(Date())
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
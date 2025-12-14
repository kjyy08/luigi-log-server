package cloud.luigi99.blog.auth.token.adapter.out.token.jwt

import cloud.luigi99.blog.auth.token.application.port.out.TokenProvider
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

private val log = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    @param:Value("\${jwt.secret}") private val secret: String,
    @param:Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @param:Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) : TokenProvider {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun generateAccessToken(memberId: MemberId): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpiration)

        log.debug { "Generating access token for member: $memberId" }

        return Jwts
            .builder()
            .subject(memberId.value.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("type", "access")
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    override fun generateRefreshToken(memberId: MemberId): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpiration)

        log.debug { "Generating refresh token for member: $memberId" }

        return Jwts
            .builder()
            .subject(memberId.value.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("type", "refresh")
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    override fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: ExpiredJwtException) {
            log.warn { "JWT token expired: ${e.message}" }
            return false
        } catch (e: JwtException) {
            log.warn { "Invalid JWT token: ${e.message}" }
            return false
        } catch (e: Exception) {
            log.error(e) { "Unexpected error validating JWT token" }
            return false
        }
    }

    override fun getSubject(token: String): String = getClaims(token).subject

    override fun getExpiration(token: String): Long {
        val claims = getClaims(token)
        val expiration = claims.expiration
        val now = Date()
        return (expiration.time - now.time) / 1000
    }

    private fun getClaims(token: String): io.jsonwebtoken.Claims =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}

package cloud.luigi99.blog.auth.token.adapter.out.persistence.redis.authtoken

import cloud.luigi99.blog.auth.token.application.port.out.AuthTokenRepository
import cloud.luigi99.blog.auth.token.domain.model.AuthToken
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Repository

private val log = KotlinLogging.logger {}

/**
 * 리프레시 토큰 저장소 어댑터 구현체
 *
 * Redis 리포지토리를 사용하여 리프레시 토큰을 관리합니다.
 */
@Repository
class AuthTokenRepositoryAdapter(private val redisRepository: AuthTokenRedisRepository) : AuthTokenRepository {
    /**
     * 리프레시 토큰을 저장합니다.
     *
     * @param authToken 저장할 리프레시 토큰
     * @return 저장된 리프레시 토큰
     */
    override fun save(authToken: AuthToken): AuthToken {
        log.debug { "Saving refresh token for member: ${authToken.memberId}" }

        val entity = AuthTokenMapper.toEntity(authToken)

        redisRepository.save(entity)
        return authToken
    }

    /**
     * 회원 ID로 리프레시 토큰을 조회합니다.
     */
    override fun findByMemberId(memberId: MemberId): AuthToken? {
        log.debug { "Finding refresh token by member ID: $memberId" }
        return redisRepository
            .findById(memberId.value.toString())
            .orElse(null)
            ?.let(AuthTokenMapper::toDomain)
    }

    /**
     * 회원 ID로 리프레시 토큰을 삭제합니다.
     */
    override fun deleteByMemberId(memberId: MemberId) {
        log.debug { "Deleting refresh token by member ID: $memberId" }
        val id = memberId.value.toString()
        if (redisRepository.existsById(id)) {
            redisRepository.deleteById(id)
        } else {
            log.warn { "Token not found for memberId: $memberId during deletion" }
        }
    }
}

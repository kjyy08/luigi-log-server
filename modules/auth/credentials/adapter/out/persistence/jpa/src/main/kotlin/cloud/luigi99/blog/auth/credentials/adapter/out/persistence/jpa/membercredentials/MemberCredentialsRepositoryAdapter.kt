package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.membercredentials

import cloud.luigi99.blog.auth.credentials.application.port.out.MemberCredentialsRepository
import cloud.luigi99.blog.auth.credentials.domain.model.MemberCredentials
import cloud.luigi99.blog.auth.credentials.domain.vo.OAuthInfo
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import org.springframework.stereotype.Repository

@Repository
class MemberCredentialsRepositoryAdapter(private val jpaRepository: MemberCredentialsJpaRepository) :
    MemberCredentialsRepository {
    override fun save(credentials: MemberCredentials): MemberCredentials {
        val entity = MemberCredentialsMapper.toEntity(credentials)
        val saved = jpaRepository.save(entity)
        return MemberCredentialsMapper.toDomain(saved)
    }

    override fun findByOAuthInfo(oauthInfo: OAuthInfo): MemberCredentials? =
        jpaRepository
            .findByOauthInfoProviderAndOauthInfoProviderId(
                oauthInfo.provider,
                oauthInfo.providerId,
            )?.let { MemberCredentialsMapper.toDomain(it) }

    override fun findByMemberId(memberId: MemberId): MemberCredentials? =
        jpaRepository.findByMemberId(memberId.value)?.let {
            MemberCredentialsMapper.toDomain(it)
        }

    override fun deleteByMemberId(memberId: MemberId) {
        jpaRepository.deleteByMemberId(memberId.value)
    }
}

package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.membercredentials

import cloud.luigi99.blog.auth.credentials.domain.enums.OAuthProvider
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberCredentialsJpaRepository : JpaRepository<MemberCredentialsJpaEntity, UUID> {
    fun findByOauthInfoProviderAndOauthInfoProviderId(
        provider: OAuthProvider,
        providerId: String,
    ): MemberCredentialsJpaEntity?

    fun findByMemberId(memberId: UUID): MemberCredentialsJpaEntity?

    fun deleteByMemberId(memberId: UUID)
}

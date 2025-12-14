package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.membercredentials

import cloud.luigi99.blog.auth.credentials.domain.model.MemberCredentials
import cloud.luigi99.blog.auth.credentials.domain.vo.CredentialsId
import cloud.luigi99.blog.auth.credentials.domain.vo.LastLoginTime
import cloud.luigi99.blog.auth.credentials.domain.vo.OAuthInfo
import cloud.luigi99.blog.member.domain.member.vo.MemberId

object MemberCredentialsMapper {
    fun toDomain(entity: MemberCredentialsJpaEntity): MemberCredentials =
        MemberCredentials.from(
            entityId = CredentialsId(entity.id),
            memberId = MemberId(entity.memberId),
            oauthInfo =
                OAuthInfo(
                    provider = entity.oauthInfo.provider,
                    providerId = entity.oauthInfo.providerId,
                ),
            role = entity.role,
            lastLoginAt = entity.lastLoginAt?.let { LastLoginTime(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: MemberCredentials): MemberCredentialsJpaEntity =
        MemberCredentialsJpaEntity
            .from(
                id = domain.entityId.value,
                memberId = domain.memberId.value,
                role = domain.role,
                provider = domain.oauthInfo.provider,
                providerId = domain.oauthInfo.providerId,
            ).apply {
                this.lastLoginAt = domain.lastLoginAt?.value
                this.createdAt = domain.createdAt
                this.updatedAt = domain.updatedAt
            }
}

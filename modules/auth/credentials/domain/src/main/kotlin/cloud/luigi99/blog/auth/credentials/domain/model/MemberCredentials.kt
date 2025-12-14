package cloud.luigi99.blog.auth.credentials.domain.model

import cloud.luigi99.blog.auth.credentials.domain.enums.Role
import cloud.luigi99.blog.auth.credentials.domain.event.MemberLoggedInEvent
import cloud.luigi99.blog.auth.credentials.domain.vo.CredentialsId
import cloud.luigi99.blog.auth.credentials.domain.vo.LastLoginTime
import cloud.luigi99.blog.auth.credentials.domain.vo.OAuthInfo
import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import java.time.LocalDateTime

class MemberCredentials private constructor(
    override val entityId: CredentialsId,
    val memberId: MemberId,
    val oauthInfo: OAuthInfo,
    var role: Role,
    var lastLoginAt: LastLoginTime? = null,
) : AggregateRoot<CredentialsId>() {
    fun updateLastLogin() {
        this.lastLoginAt = LastLoginTime.now()
        this.registerEvent(MemberLoggedInEvent(this.memberId))
    }

    fun updateRole(newRole: Role) {
        this.role = newRole
    }

    companion object {
        fun create(memberId: MemberId, oauthInfo: OAuthInfo): MemberCredentials =
            MemberCredentials(
                entityId = CredentialsId.generate(),
                memberId = memberId,
                oauthInfo = oauthInfo,
                role = Role.getDefault(),
            )

        fun from(
            entityId: CredentialsId,
            memberId: MemberId,
            oauthInfo: OAuthInfo,
            role: Role,
            lastLoginAt: LastLoginTime?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): MemberCredentials {
            val credentials =
                MemberCredentials(
                    entityId = entityId,
                    memberId = memberId,
                    oauthInfo = oauthInfo,
                    role = role,
                    lastLoginAt = lastLoginAt,
                )
            credentials.createdAt = createdAt
            credentials.updatedAt = updatedAt
            return credentials
        }
    }
}

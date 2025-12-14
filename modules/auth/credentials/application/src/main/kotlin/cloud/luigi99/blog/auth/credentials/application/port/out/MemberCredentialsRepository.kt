package cloud.luigi99.blog.auth.credentials.application.port.out

import cloud.luigi99.blog.auth.credentials.domain.model.MemberCredentials
import cloud.luigi99.blog.auth.credentials.domain.vo.OAuthInfo
import cloud.luigi99.blog.member.domain.member.vo.MemberId

interface MemberCredentialsRepository {
    fun save(credentials: MemberCredentials): MemberCredentials

    fun findByOAuthInfo(oauthInfo: OAuthInfo): MemberCredentials?

    fun findByMemberId(memberId: MemberId): MemberCredentials?

    fun deleteByMemberId(memberId: MemberId)
}

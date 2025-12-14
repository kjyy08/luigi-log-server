package cloud.luigi99.blog.auth.token.application.port.out

import cloud.luigi99.blog.auth.token.domain.model.AuthToken
import cloud.luigi99.blog.member.domain.member.vo.MemberId

interface AuthTokenRepository {
    fun save(authToken: AuthToken): AuthToken

    fun findByMemberId(memberId: MemberId): AuthToken?

    fun deleteByMemberId(memberId: MemberId)
}

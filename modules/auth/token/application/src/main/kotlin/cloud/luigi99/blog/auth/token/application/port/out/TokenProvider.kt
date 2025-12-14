package cloud.luigi99.blog.auth.token.application.port.out

import cloud.luigi99.blog.member.domain.member.vo.MemberId

interface TokenProvider {
    fun generateAccessToken(memberId: MemberId): String

    fun generateRefreshToken(memberId: MemberId): String

    fun validateToken(token: String): Boolean

    fun getSubject(token: String): String

    fun getExpiration(token: String): Long
}

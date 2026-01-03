package cloud.luigi99.blog.auth.credentials.adapter.out.client.member

import cloud.luigi99.blog.auth.credentials.application.port.out.MemberClient
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.command.RegisterMemberUseCase
import org.springframework.stereotype.Component

/**
 * Auth Credentials용 Member Client Adapter
 *
 * 회원 등록을 위한 클라이언트 어댑터입니다.
 * MSA 전환 시 HTTP 클라이언트로 대체될 예정입니다.
 */
@Component("authMemberClientAdapter")
class MemberClientAdapter(private val memberCommandFacade: MemberCommandFacade) : MemberClient {
    override fun execute(request: MemberClient.Request): MemberClient.Response {
        val command =
            RegisterMemberUseCase.Command(
                email = request.email,
                username = request.username,
                profileImgUrl = request.profileImgUrl,
            )

        // MSA 전환 시 실제 API 요청 전송 코드로 변경 필요
        val response = memberCommandFacade.registerMember().execute(command)

        return MemberClient.Response(
            memberId = response.memberId,
            email = response.email,
            username = response.username,
            profileImgUrl = response.profileImgUrl,
        )
    }
}

package cloud.luigi99.blog.auth.credentials.adapter.out.client.member

import cloud.luigi99.blog.auth.credentials.application.port.out.MemberClient
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.command.RegisterMemberUseCase
import org.springframework.stereotype.Component

@Component
class MemberClientAdapter(private val memberCommandFacade: MemberCommandFacade) : MemberClient {
    override fun execute(request: MemberClient.Request): MemberClient.Response {
        val command =
            RegisterMemberUseCase.Command(
                email = request.email,
                username = request.username,
            )

        // MSA 전환 시 실제 API 요청 전송 코드로 변경 필요
        val response = memberCommandFacade.registerMember().execute(command)

        return MemberClient.Response(
            memberId = response.memberId,
            email = response.email,
            username = response.username,
        )
    }
}

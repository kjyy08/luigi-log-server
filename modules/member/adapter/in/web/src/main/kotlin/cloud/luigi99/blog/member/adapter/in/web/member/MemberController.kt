package cloud.luigi99.blog.member.adapter.`in`.web.member

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.member.adapter.`in`.web.member.dto.MemberResponse
import cloud.luigi99.blog.member.application.member.port.`in`.command.DeleteMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.command.MemberCommandFacade
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetCurrentMemberUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * 회원 컨트롤러
 *
 * 회원 정보 조회 및 회원 탈퇴 등 회원 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberQueryFacade: MemberQueryFacade,
    private val memberCommandFacade: MemberCommandFacade,
) : MemberApi {
    @GetMapping("/me")
    override fun getCurrentMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<MemberResponse>> {
        log.info { "Getting current member info for: $memberId" }

        val response =
            memberQueryFacade.getCurrentMember().execute(
                GetCurrentMemberUseCase.Query(memberId),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                MemberResponse(
                    memberId = response.memberId,
                    email = response.email,
                    username = response.username,
                ),
            ),
        )
    }

    @DeleteMapping
    override fun deleteMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting member: $memberId" }

        memberCommandFacade.deleteMember().execute(
            DeleteMemberUseCase.Command(memberId = memberId),
        )

        return ResponseEntity.noContent().build()
    }
}

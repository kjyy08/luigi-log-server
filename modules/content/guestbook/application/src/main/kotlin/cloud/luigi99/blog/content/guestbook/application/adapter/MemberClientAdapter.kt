package cloud.luigi99.blog.content.guestbook.application.adapter

import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMembersProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import org.springframework.stereotype.Component

/**
 * 회원 정보 조회 어댑터
 *
 * Member 모듈의 MemberQueryFacade를 통해 회원 정보를 조회합니다.
 */
@Component("guestbookMemberClientAdapter")
class MemberClientAdapter(private val memberQueryFacade: MemberQueryFacade) : MemberQueryPort {
    override fun getAuthor(memberId: String): MemberQueryPort.Author {
        val response =
            memberQueryFacade.getMembersProfile().execute(
                GetMembersProfileUseCase.Query(listOf(memberId)),
            )

        val memberProfile =
            response.members.firstOrNull()
                ?: return MemberQueryPort.Author(memberId, "Unknown", null, "unknown")

        return MemberQueryPort.Author(
            memberId = memberProfile.memberId,
            nickname = memberProfile.profile?.nickname ?: memberProfile.username,
            profileImageUrl = memberProfile.profile?.profileImageUrl,
            username = memberProfile.username,
        )
    }

    override fun getAuthors(memberIds: List<String>): Map<String, MemberQueryPort.Author> {
        if (memberIds.isEmpty()) return emptyMap()

        val response =
            memberQueryFacade.getMembersProfile().execute(
                GetMembersProfileUseCase.Query(memberIds),
            )

        return response.members.associate { memberProfile ->
            memberProfile.memberId to
                MemberQueryPort.Author(
                    memberId = memberProfile.memberId,
                    nickname = memberProfile.profile?.nickname ?: memberProfile.username,
                    profileImageUrl = memberProfile.profile?.profileImageUrl,
                    username = memberProfile.username,
                )
        }
    }
}

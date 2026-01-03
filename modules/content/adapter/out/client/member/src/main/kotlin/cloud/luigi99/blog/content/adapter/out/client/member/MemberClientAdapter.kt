package cloud.luigi99.blog.content.adapter.out.client.member

import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMembersProfileUseCase
import cloud.luigi99.blog.member.application.member.port.`in`.query.MemberQueryFacade
import org.springframework.stereotype.Component

@Component
class MemberClientAdapter(private val memberQueryFacade: MemberQueryFacade) : MemberClient {
    override fun getAuthor(memberId: String): MemberClient.Author {
        val response =
            memberQueryFacade.getMembersProfile().execute(
                GetMembersProfileUseCase.Query(listOf(memberId)),
            )

        val memberProfile =
            response.members.firstOrNull()
                ?: return MemberClient.Author(memberId, "Unknown", null)

        return MemberClient.Author(
            memberId = memberProfile.memberId,
            nickname = memberProfile.profile?.nickname ?: memberProfile.username,
            profileImageUrl = memberProfile.profile?.profileImageUrl,
        )
    }

    override fun getAuthors(memberIds: List<String>): Map<String, MemberClient.Author> {
        if (memberIds.isEmpty()) return emptyMap()

        val response =
            memberQueryFacade.getMembersProfile().execute(
                GetMembersProfileUseCase.Query(memberIds),
            )

        return response.members.associate { memberProfile ->
            memberProfile.memberId to
                MemberClient.Author(
                    memberId = memberProfile.memberId,
                    nickname = memberProfile.profile?.nickname ?: memberProfile.username,
                    profileImageUrl = memberProfile.profile?.profileImageUrl,
                )
        }
    }
}

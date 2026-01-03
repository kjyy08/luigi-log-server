package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMembersProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class GetMembersProfileService(private val memberRepository: MemberRepository) : GetMembersProfileUseCase {
    override fun execute(query: GetMembersProfileUseCase.Query): GetMembersProfileUseCase.Response {
        log.debug { "Fetching profiles for members: ${query.memberIds}" }

        val memberIds = query.memberIds.map { MemberId.from(it) }
        val members = memberRepository.findAllById(memberIds)

        val memberProfiles =
            members.map { member ->
                val profileResponse =
                    member.profile?.let {
                        GetMembersProfileUseCase.ProfileResponse(
                            profileId =
                                it.entityId.value
                                    .toString(),
                            nickname = it.nickname.value,
                            bio = it.bio?.value,
                            profileImageUrl = it.profileImageUrl?.value,
                            jobTitle = it.jobTitle?.value,
                            techStack = it.techStack.values,
                            githubUrl = it.githubUrl?.value,
                            contactEmail = it.contactEmail?.value,
                            websiteUrl = it.websiteUrl?.value,
                        )
                    }

                GetMembersProfileUseCase.MemberProfile(
                    memberId =
                        member.entityId.value
                            .toString(),
                    email = member.email.value,
                    username = member.username.value,
                    profile = profileResponse,
                )
            }

        return GetMembersProfileUseCase.Response(members = memberProfiles)
    }
}

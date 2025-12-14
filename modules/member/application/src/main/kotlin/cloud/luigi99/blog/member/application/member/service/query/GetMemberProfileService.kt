package cloud.luigi99.blog.member.application.member.service.query

import cloud.luigi99.blog.member.application.member.port.`in`.query.GetMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class GetMemberProfileService(private val memberRepository: MemberRepository) : GetMemberProfileUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetMemberProfileUseCase.Query): GetMemberProfileUseCase.Response {
        log.info { "Getting member with profile: ${query.memberId}" }

        val member =
            memberRepository.findById(MemberId.from(query.memberId))
                ?: throw MemberNotFoundException("Member not found: ${query.memberId}")

        val profileResponse =
            member.profile?.let {
                GetMemberProfileUseCase.ProfileResponse(
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

        return GetMemberProfileUseCase.Response(
            memberId =
                member.entityId.value
                    .toString(),
            email = member.email.value,
            username = member.username.value,
            profile = profileResponse,
        )
    }
}

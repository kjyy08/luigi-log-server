package cloud.luigi99.blog.member.application.member.service.command

import cloud.luigi99.blog.member.application.member.port.`in`.command.UpdateMemberProfileUseCase
import cloud.luigi99.blog.member.application.member.port.out.MemberRepository
import cloud.luigi99.blog.member.domain.member.exception.MemberNotFoundException
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.Company
import cloud.luigi99.blog.member.domain.profile.vo.ContactEmail
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Location
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.Readme
import cloud.luigi99.blog.member.domain.profile.vo.Url
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class UpdateMemberProfileService(private val memberRepository: MemberRepository) : UpdateMemberProfileUseCase {
    @Transactional
    override fun execute(command: UpdateMemberProfileUseCase.Command): UpdateMemberProfileUseCase.Response {
        log.info { "Updating member profile: ${command.memberId}" }

        val member =
            memberRepository.findById(MemberId.from(command.memberId))
                ?: throw MemberNotFoundException("Member not found: ${command.memberId}")

        val updatedProfile =
            if (member.profile != null) {
                member.profile!!.update(
                    nickname = command.nickname?.let { Nickname(it) } ?: member.profile!!.nickname,
                    bio = command.bio?.let { Bio(it) } ?: member.profile!!.bio,
                    profileImageUrl = command.profileImageUrl?.let { Url(it) } ?: member.profile!!.profileImageUrl,
                    readme = command.readme?.let { Readme(it) } ?: member.profile!!.readme,
                    company = command.company?.let { Company(it) } ?: member.profile!!.company,
                    location = command.location?.let { Location(it) } ?: member.profile!!.location,
                    jobTitle = command.jobTitle?.let { JobTitle(it) } ?: member.profile!!.jobTitle,
                    githubUrl = command.githubUrl?.let { Url(it) } ?: member.profile!!.githubUrl,
                    contactEmail = command.contactEmail?.let { ContactEmail(it) } ?: member.profile!!.contactEmail,
                    websiteUrl = command.websiteUrl?.let { Url(it) } ?: member.profile!!.websiteUrl,
                )
            } else {
                Profile.create(
                    nickname =
                        Nickname(
                            command.nickname ?: throw IllegalArgumentException("Nickname is required for new profile"),
                        ),
                    bio = command.bio?.let { Bio(it) },
                    profileImageUrl = command.profileImageUrl?.let { Url(it) },
                    readme = command.readme?.let { Readme(it) },
                    company = command.company?.let { Company(it) },
                    location = command.location?.let { Location(it) },
                    jobTitle = command.jobTitle?.let { JobTitle(it) },
                    githubUrl = command.githubUrl?.let { Url(it) },
                    contactEmail = command.contactEmail?.let { ContactEmail(it) },
                    websiteUrl = command.websiteUrl?.let { Url(it) },
                )
            }

        val updatedMember = member.updateProfile(updatedProfile)
        memberRepository.save(updatedMember)

        return UpdateMemberProfileUseCase.Response(
            profileId =
                updatedProfile.entityId.value
                    .toString(),
            nickname = updatedProfile.nickname.value,
            bio = updatedProfile.bio?.value,
            profileImageUrl = updatedProfile.profileImageUrl?.value,
            readme = updatedProfile.readme?.value,
            company = updatedProfile.company?.value,
            location = updatedProfile.location?.value,
            jobTitle = updatedProfile.jobTitle?.value,
            githubUrl = updatedProfile.githubUrl?.value,
            contactEmail = updatedProfile.contactEmail?.value,
            websiteUrl = updatedProfile.websiteUrl?.value,
        )
    }
}

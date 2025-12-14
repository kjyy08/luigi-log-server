package cloud.luigi99.blog.member.domain.profile.model

import cloud.luigi99.blog.common.domain.DomainEntity
import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.ContactEmail
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.ProfileId
import cloud.luigi99.blog.member.domain.profile.vo.TechStack
import cloud.luigi99.blog.member.domain.profile.vo.Url
import java.time.LocalDateTime

class Profile private constructor(
    override val entityId: ProfileId,
    val nickname: Nickname,
    val bio: Bio?,
    val profileImageUrl: Url?,
    val jobTitle: JobTitle?,
    val techStack: TechStack,
    val githubUrl: Url?,
    val contactEmail: ContactEmail?,
    val websiteUrl: Url?,
) : DomainEntity<ProfileId>() {
    companion object {
        fun create(
            nickname: Nickname,
            bio: Bio? = null,
            profileImageUrl: Url? = null,
            jobTitle: JobTitle? = null,
            techStack: TechStack = TechStack(emptyList()),
            githubUrl: Url? = null,
            contactEmail: ContactEmail? = null,
            websiteUrl: Url? = null,
        ): Profile =
            Profile(
                entityId = ProfileId.generate(),
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl,
                jobTitle = jobTitle,
                techStack = techStack,
                githubUrl = githubUrl,
                contactEmail = contactEmail,
                websiteUrl = websiteUrl,
            )

        fun from(
            entityId: ProfileId,
            nickname: Nickname,
            bio: Bio?,
            profileImageUrl: Url?,
            jobTitle: JobTitle?,
            techStack: TechStack,
            githubUrl: Url?,
            contactEmail: ContactEmail?,
            websiteUrl: Url?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Profile {
            val profile =
                Profile(
                    entityId = entityId,
                    nickname = nickname,
                    bio = bio,
                    profileImageUrl = profileImageUrl,
                    jobTitle = jobTitle,
                    techStack = techStack,
                    githubUrl = githubUrl,
                    contactEmail = contactEmail,
                    websiteUrl = websiteUrl,
                )
            profile.createdAt = createdAt
            profile.updatedAt = updatedAt
            return profile
        }
    }

    fun update(
        nickname: Nickname = this.nickname,
        bio: Bio? = this.bio,
        profileImageUrl: Url? = this.profileImageUrl,
        jobTitle: JobTitle? = this.jobTitle,
        techStack: TechStack = this.techStack,
        githubUrl: Url? = this.githubUrl,
        contactEmail: ContactEmail? = this.contactEmail,
        websiteUrl: Url? = this.websiteUrl,
    ): Profile {
        val updated =
            Profile(
                entityId = this.entityId,
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl,
                jobTitle = jobTitle,
                techStack = techStack,
                githubUrl = githubUrl,
                contactEmail = contactEmail,
                websiteUrl = websiteUrl,
            )
        updated.createdAt = this.createdAt
        updated.updatedAt = this.updatedAt
        return updated
    }
}

package cloud.luigi99.blog.member.adapter.out.persistence.jpa.profile

import cloud.luigi99.blog.member.domain.profile.model.Profile
import cloud.luigi99.blog.member.domain.profile.vo.Bio
import cloud.luigi99.blog.member.domain.profile.vo.ContactEmail
import cloud.luigi99.blog.member.domain.profile.vo.JobTitle
import cloud.luigi99.blog.member.domain.profile.vo.Nickname
import cloud.luigi99.blog.member.domain.profile.vo.TechStack
import cloud.luigi99.blog.member.domain.profile.vo.Url

object ProfileMapper {
    fun toDomain(entity: ProfileJpaEntity): Profile =
        Profile.from(
            entityId = entity.entityId,
            nickname = Nickname(entity.nickname),
            bio = entity.bio?.let { Bio(it) },
            profileImageUrl = entity.profileImageUrl?.let { Url(it) },
            jobTitle = entity.jobTitle?.let { JobTitle(it) },
            techStack = TechStack(entity.techStack),
            githubUrl = entity.githubUrl?.let { Url(it) },
            contactEmail = entity.contactEmail?.let { ContactEmail(it) },
            websiteUrl = entity.websiteUrl?.let { Url(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(domain: Profile): ProfileJpaEntity {
        val profileEntity =
            ProfileJpaEntity
                .from(
                    entityId = domain.entityId.value,
                    nickname = domain.nickname.value,
                    bio = domain.bio?.value,
                    profileImageUrl = domain.profileImageUrl?.value,
                    jobTitle = domain.jobTitle?.value,
                    techStack = domain.techStack.values,
                    githubUrl = domain.githubUrl?.value,
                    contactEmail = domain.contactEmail?.value,
                    websiteUrl = domain.websiteUrl?.value,
                ).apply {
                    this.createdAt = domain.createdAt
                    this.updatedAt = domain.updatedAt
                }

        return profileEntity
    }
}

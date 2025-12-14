package cloud.luigi99.blog.member.adapter.out.persistence.jpa.profile

import cloud.luigi99.blog.adapter.persistence.jpa.JpaDomainEntity
import cloud.luigi99.blog.member.domain.profile.vo.ProfileId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "profile")
@DynamicUpdate
class ProfileJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "nickname", nullable = false, length = 100)
    val nickname: String,
    @Column(name = "bio", length = 500)
    val bio: String?,
    @Column(name = "profile_image_url", length = 2000)
    val profileImageUrl: String?,
    @Column(name = "job_title", length = 100)
    val jobTitle: String?,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack", columnDefinition = "JSON")
    val techStack: List<String> = emptyList(),
    @Column(name = "github_url", length = 500)
    val githubUrl: String?,
    @Column(name = "contact_email", length = 255)
    val contactEmail: String?,
    @Column(name = "website_url", length = 500)
    val websiteUrl: String?,
) : JpaDomainEntity<ProfileId>() {
    override val entityId: ProfileId
        get() = ProfileId(id)

    companion object {
        fun from(
            entityId: UUID,
            nickname: String,
            bio: String?,
            profileImageUrl: String?,
            jobTitle: String?,
            techStack: List<String>,
            githubUrl: String?,
            contactEmail: String?,
            websiteUrl: String?,
        ): ProfileJpaEntity =
            ProfileJpaEntity(
                id = entityId,
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl,
                jobTitle = jobTitle,
                techStack = techStack,
                githubUrl = githubUrl,
                contactEmail = contactEmail,
                websiteUrl = websiteUrl,
            )
    }
}

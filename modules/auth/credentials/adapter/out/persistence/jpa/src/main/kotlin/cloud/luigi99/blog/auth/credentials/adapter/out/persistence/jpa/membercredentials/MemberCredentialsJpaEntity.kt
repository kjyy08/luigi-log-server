package cloud.luigi99.blog.auth.credentials.adapter.out.persistence.jpa.membercredentials

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.auth.credentials.domain.enums.OAuthProvider
import cloud.luigi99.blog.auth.credentials.domain.enums.Role
import cloud.luigi99.blog.auth.credentials.domain.vo.CredentialsId
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "member_credentials")
@DynamicUpdate
class MemberCredentialsJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "member_id", nullable = false)
    val memberId: UUID,
    @Column(name = "last_login_at", nullable = false)
    var lastLoginAt: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    val role: Role,
    @Embedded
    val oauthInfo: OAuthInfo,
) : JpaAggregateRoot<CredentialsId>() {
    override val entityId: CredentialsId
        get() = CredentialsId(id)

    companion object {
        fun from(
            id: UUID,
            memberId: UUID,
            role: Role,
            provider: OAuthProvider,
            providerId: String,
        ): MemberCredentialsJpaEntity =
            MemberCredentialsJpaEntity(
                id = id,
                memberId = memberId,
                role = role,
                oauthInfo =
                    OAuthInfo(
                        provider = provider,
                        providerId = providerId,
                    ),
            )
    }

    @Embeddable
    data class OAuthInfo(
        @Enumerated(EnumType.STRING)
        @Column(name = "provider", nullable = false, length = 50)
        val provider: OAuthProvider,
        @Column(name = "provider_id", nullable = false, length = 255)
        val providerId: String,
    )
}

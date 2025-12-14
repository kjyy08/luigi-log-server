package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import cloud.luigi99.blog.adapter.persistence.jpa.JpaAggregateRoot
import cloud.luigi99.blog.member.adapter.out.persistence.jpa.profile.ProfileJpaEntity
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.UUID

@Entity
@Table(name = "member")
@DynamicUpdate
class MemberJpaEntity private constructor(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    @Column(name = "username", nullable = false, length = 100)
    val username: String,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    var profile: ProfileJpaEntity? = null,
) : JpaAggregateRoot<MemberId>() {
    override val entityId: MemberId
        get() = MemberId(id)

    companion object {
        fun from(entityId: UUID, email: String, username: String): MemberJpaEntity =
            MemberJpaEntity(
                id = entityId,
                email = email,
                username = username,
            )
    }
}

package cloud.luigi99.blog.member.adapter.out.persistence.jpa.member

import cloud.luigi99.blog.member.adapter.out.persistence.jpa.profile.ProfileMapper
import cloud.luigi99.blog.member.domain.member.model.Member
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username

object MemberMapper {
    fun toDomain(entity: MemberJpaEntity): Member =
        Member.from(
            entityId = MemberId(entity.id),
            email = Email(entity.email),
            username = Username(entity.username),
            profile = entity.profile?.let { ProfileMapper.toDomain(it) },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toEntity(member: Member): MemberJpaEntity {
        val memberJpaEntity =
            MemberJpaEntity
                .from(
                    entityId = member.entityId.value,
                    email = member.email.value,
                    username = member.username.value,
                ).apply {
                    createdAt = member.createdAt
                    updatedAt = member.updatedAt
                }

        memberJpaEntity.profile = member.profile?.let { ProfileMapper.toEntity(it) }

        return memberJpaEntity
    }
}

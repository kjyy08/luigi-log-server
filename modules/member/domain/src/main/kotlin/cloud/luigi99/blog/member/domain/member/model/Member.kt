package cloud.luigi99.blog.member.domain.member.model

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.member.domain.member.event.MemberDeletedEvent
import cloud.luigi99.blog.member.domain.member.event.MemberProfileUpdatedEvent
import cloud.luigi99.blog.member.domain.member.event.MemberRegisteredEvent
import cloud.luigi99.blog.member.domain.member.event.MemberUsernameUpdatedEvent
import cloud.luigi99.blog.member.domain.member.vo.Email
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import cloud.luigi99.blog.member.domain.member.vo.Username
import cloud.luigi99.blog.member.domain.profile.model.Profile
import java.time.LocalDateTime

/**
 * 회원 도메인 엔티티
 *
 * 회원의 핵심 정보와 행위를 정의합니다.
 *
 * @property entityId 회원 ID
 * @property email 이메일
 * @property username 사용자 이름
 * @property profile 회원 프로필 (선택)
 */
class Member private constructor(
    override val entityId: MemberId,
    val email: Email,
    val username: Username,
    val profile: Profile?,
) : AggregateRoot<MemberId>() {
    companion object {
        /**
         * 신규 회원을 등록합니다.
         *
         * @param email 이메일
         * @param username 사용자 이름
         * @param profile 프로필 (선택)
         * @return 생성된 회원 엔티티
         */
        fun register(email: Email, username: Username, profile: Profile? = null): Member {
            val member =
                Member(
                    entityId = MemberId.generate(),
                    email = email,
                    username = username,
                    profile = profile,
                )

            member.registerEvent(MemberRegisteredEvent(member.entityId, member.email))
            return member
        }

        /**
         * 영속성 계층에서 데이터를 로드하여 도메인 엔티티를 재구성합니다.
         */
        fun from(
            entityId: MemberId,
            email: Email,
            username: Username,
            profile: Profile?,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Member {
            val member =
                Member(
                    entityId = entityId,
                    email = email,
                    username = username,
                    profile = profile,
                )
            member.createdAt = createdAt
            member.updatedAt = updatedAt
            return member
        }
    }

    /**
     * 사용자 이름을 변경합니다.
     *
     * @param newUsername 새로운 사용자 이름
     * @return 변경된 회원 엔티티
     */
    fun updateUsername(newUsername: Username): Member {
        val updated =
            Member(
                entityId = entityId,
                email = email,
                username = newUsername,
                profile = profile,
            )
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt

        updated.registerEvent(MemberUsernameUpdatedEvent(updated.entityId, updated.username.value))
        return updated
    }

    /**
     * 프로필을 업데이트합니다.
     *
     * @param newProfile 새로운 프로필
     * @return 변경된 회원 엔티티
     */
    fun updateProfile(newProfile: Profile): Member {
        val updated =
            Member(
                entityId = entityId,
                email = email,
                username = username,
                profile = newProfile,
            )
        updated.createdAt = createdAt
        updated.updatedAt = updatedAt

        updated.registerEvent(MemberProfileUpdatedEvent(updated.entityId))
        return updated
    }

    /**
     * 회원 탈퇴를 처리합니다.
     *
     * @return 탈퇴 처리된 회원 엔티티
     */
    fun withdraw(): Member {
        registerEvent(MemberDeletedEvent(entityId))
        return this
    }
}

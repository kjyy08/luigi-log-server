package cloud.luigi99.blog.user.domain

import cloud.luigi99.blog.common.domain.AggregateRoot
import cloud.luigi99.blog.common.exception.ErrorCode
import cloud.luigi99.blog.user.domain.entity.Profile
import cloud.luigi99.blog.user.domain.entity.UserSession
import cloud.luigi99.blog.user.domain.event.UserCreatedEvent
import cloud.luigi99.blog.user.domain.event.UserLoggedInEvent
import cloud.luigi99.blog.user.domain.event.UserProfileUpdatedEvent
import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.Password
import cloud.luigi99.blog.user.domain.vo.UserId
import cloud.luigi99.blog.user.domain.vo.UserRole
import cloud.luigi99.blog.user.domain.vo.UserStatus
import java.time.LocalDateTime

/**
 * User 애그리게이트 루트
 *
 * 사용자 도메인의 핵심 애그리게이트 루트로, 사용자 관련 모든 비즈니스 로직을 담당합니다.
 * 사용자 인증/인가, 프로필 관리, 세션 관리 등의 책임을 가집니다.
 *
 * @property entityId 사용자 식별자
 * @property email 이메일 주소
 * @property password 암호화된 비밀번호
 * @property role 사용자 역할
 * @property status 사용자 상태
 * @property profile 사용자 프로필
 * @property sessions 사용자 세션 목록
 * @property lastLoginAt 마지막 로그인 시각
 * @property createdAt 생성 시각
 * @property updatedAt 수정 시각
 */
data class User(
    override val entityId: UserId,
    val email: Email,
    val password: Password,
    val role: UserRole,
    val status: UserStatus,
    val profile: Profile,
    val sessions: List<UserSession> = emptyList(),
    val lastLoginAt: LocalDateTime? = null,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null
) : AggregateRoot<UserId>() {

    /**
     * 사용자가 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     */
    fun isActive(): Boolean = status.isActive()

    /**
     * 사용자가 서비스를 이용할 수 있는지 확인합니다.
     *
     * @return 서비스 이용 가능하면 true, 그렇지 않으면 false
     */
    fun canUseService(): Boolean = status.canUseService()

    /**
     * 비밀번호가 일치하는지 확인합니다.
     *
     * @param rawPassword 평문 비밀번호
     * @param matches 비밀번호 일치 여부 확인 함수 (PasswordEncoder.matches)
     * @return 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    fun matchesPassword(rawPassword: String, matches: (String, String) -> Boolean): Boolean {
        return matches(rawPassword, password.value)
    }

    /**
     * 관리자 권한이 있는지 확인합니다.
     *
     * @return 관리자이면 true, 그렇지 않으면 false
     */
    fun isAdmin(): Boolean = role.isAdmin()

    /**
     * 콘텐츠 작성 권한이 있는지 확인합니다.
     *
     * @return 콘텐츠 작성 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canWriteContent(): Boolean = role.canWriteContent()

    /**
     * 관리 권한이 있는지 확인합니다.
     *
     * @return 관리 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canManage(): Boolean = role.canManage()

    /**
     * 댓글 작성 권한이 있는지 확인합니다.
     *
     * @return 댓글 작성 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canComment(): Boolean = role.canComment()

    /**
     * 사용자 역할을 변경합니다.
     *
     * @param newRole 새로운 역할
     * @return 역할이 변경된 User 인스턴스
     */
    fun changeRole(newRole: UserRole): User {
        return copy(
            role = newRole,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @param expiresAt 토큰 만료 시각
     * @param deviceInfo 디바이스 정보 (선택)
     * @param ipAddress IP 주소 (선택)
     * @return 로그인 처리된 User 인스턴스
     * @throws IllegalStateException 서비스 이용이 불가능한 상태인 경우
     */
    fun login(
        refreshToken: String,
        expiresAt: LocalDateTime,
        deviceInfo: String? = null,
        ipAddress: String? = null
    ): User {
        check(canUseService()) {
            "${ErrorCode.USER_DEACTIVATED.description}: 현재 상태(${status.displayName})에서는 로그인할 수 없습니다"
        }

        val newSession = UserSession.create(
            userId = entityId,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
            deviceInfo = deviceInfo,
            ipAddress = ipAddress
        )

        val updatedUser = copy(
            sessions = sessions + newSession,
            lastLoginAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // 로그인 이벤트 발행
        updatedUser.addDomainEvent(
            UserLoggedInEvent(
                userId = entityId,
                email = email,
                loginAt = LocalDateTime.now(),
                ipAddress = ipAddress
            )
        )

        return updatedUser
    }

    /**
     * 사용자 프로필을 업데이트합니다.
     *
     * @param nickname 새로운 닉네임
     * @param bio 새로운 자기소개
     * @param profileImageUrl 새로운 프로필 이미지 URL
     * @return 프로필 업데이트된 User 인스턴스
     */
    fun updateProfile(
        nickname: String = profile.nickname,
        bio: String? = profile.bio,
        profileImageUrl: String? = profile.profileImageUrl
    ): User {
        check(canUseService()) {
            "${ErrorCode.USER_DEACTIVATED.description}: 현재 상태(${status.displayName})에서는 프로필을 수정할 수 없습니다"
        }

        val updatedProfile = profile.update(
            nickname = nickname,
            bio = bio,
            profileImageUrl = profileImageUrl
        )

        val updatedUser = copy(
            profile = updatedProfile,
            updatedAt = LocalDateTime.now()
        )

        // 프로필 업데이트 이벤트 발행
        updatedUser.addDomainEvent(
            UserProfileUpdatedEvent(
                userId = entityId,
                email = email,
                updatedAt = LocalDateTime.now()
            )
        )

        return updatedUser
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param oldRawPassword 기존 평문 비밀번호
     * @param newRawPassword 새로운 평문 비밀번호
     * @param matches 비밀번호 일치 여부 확인 함수
     * @param encode 비밀번호 암호화 함수
     * @return 비밀번호 변경된 User 인스턴스
     * @throws IllegalArgumentException 기존 비밀번호가 일치하지 않는 경우
     */
    fun changePassword(
        oldRawPassword: String,
        newRawPassword: String,
        matches: (String, String) -> Boolean,
        encode: (String) -> String
    ): User {
        require(matchesPassword(oldRawPassword, matches)) {
            "${ErrorCode.AUTH_INVALID_CREDENTIALS.description}: 기존 비밀번호가 일치하지 않습니다"
        }

        val newPassword = Password.create(newRawPassword, encode)

        return copy(
            password = newPassword,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 사용자를 비활성화합니다.
     *
     * @return 비활성화된 User 인스턴스
     */
    fun deactivate(): User {
        check(status != UserStatus.SUSPENDED) {
            "${ErrorCode.STATE_INVALID_TRANSITION.description}: 정지된 계정은 비활성화할 수 없습니다"
        }

        return copy(
            status = UserStatus.INACTIVE,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 사용자를 재활성화합니다.
     *
     * @return 재활성화된 User 인스턴스
     */
    fun activate(): User {
        check(status == UserStatus.INACTIVE) {
            "${ErrorCode.STATE_INVALID_TRANSITION.description}: 비활성 상태의 계정만 재활성화할 수 있습니다"
        }

        return copy(
            status = UserStatus.ACTIVE,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 사용자를 정지합니다.
     * 관리자만 호출할 수 있는 메서드입니다.
     *
     * @return 정지된 User 인스턴스
     */
    fun suspend(): User {
        return copy(
            status = UserStatus.SUSPENDED,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 만료된 세션을 제거합니다.
     *
     * @return 만료된 세션이 제거된 User 인스턴스
     */
    fun removeExpiredSessions(): User {
        val validSessions = sessions.filter { it.isValid() }

        return if (validSessions.size != sessions.size) {
            copy(
                sessions = validSessions,
                updatedAt = LocalDateTime.now()
            )
        } else {
            this
        }
    }

    /**
     * 특정 세션을 제거합니다.
     * 로그아웃 처리에 사용됩니다.
     *
     * @param refreshToken 제거할 세션의 리프레시 토큰
     * @return 세션이 제거된 User 인스턴스
     */
    fun removeSession(refreshToken: String): User {
        val filteredSessions = sessions.filter { it.refreshToken != refreshToken }

        return copy(
            sessions = filteredSessions,
            updatedAt = LocalDateTime.now()
        )
    }

    companion object {
        /**
         * 새로운 User를 생성합니다.
         *
         * @param email 이메일 주소
         * @param rawPassword 평문 비밀번호
         * @param nickname 닉네임
         * @param role 사용자 역할 (기본: USER)
         * @param encode 비밀번호 암호화 함수
         * @param bio 자기소개 (선택)
         * @param profileImageUrl 프로필 이미지 URL (선택)
         * @return 새로운 User 인스턴스
         */
        fun create(
            email: Email,
            rawPassword: String,
            nickname: String,
            role: UserRole = UserRole.USER,
            encode: (String) -> String,
            bio: String? = null,
            profileImageUrl: String? = null
        ): User {
            val userId = UserId.generate()
            val password = Password.create(rawPassword, encode)
            val profile = Profile.create(
                userId = userId,
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl
            )

            val user = User(
                entityId = userId,
                email = email,
                password = password,
                role = role,
                status = UserStatus.ACTIVE,
                profile = profile,
                sessions = emptyList(),
                lastLoginAt = null,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )

            // 사용자 생성 이벤트 발행
            user.addDomainEvent(
                UserCreatedEvent(
                    userId = userId,
                    email = email,
                    createdAt = user.createdAt
                )
            )

            return user
        }
    }
}

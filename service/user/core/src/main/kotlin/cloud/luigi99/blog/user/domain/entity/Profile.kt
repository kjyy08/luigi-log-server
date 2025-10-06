package cloud.luigi99.blog.user.domain.entity

import cloud.luigi99.blog.common.domain.BaseEntity
import cloud.luigi99.blog.user.domain.vo.UserId
import java.time.LocalDateTime

/**
 * 사용자 프로필 엔티티
 *
 * 사용자의 공개 프로필 정보를 관리하는 엔티티입니다.
 * User 애그리게이트 내부에서 관리되며, 독립적으로 존재할 수 없습니다.
 *
 * @property entityId 프로필 식별자 (사용자 ID와 동일)
 * @property nickname 닉네임
 * @property bio 자기소개
 * @property profileImageUrl 프로필 이미지 URL
 * @property createdAt 생성 시각
 * @property updatedAt 수정 시각
 */
data class Profile(
    override val entityId: UserId,
    val nickname: String,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null
) : BaseEntity<UserId>() {

    init {
        validateNickname(nickname)
        bio?.let { validateBio(it) }
    }

    /**
     * 프로필을 업데이트합니다.
     *
     * @param nickname 새로운 닉네임
     * @param bio 새로운 자기소개
     * @param profileImageUrl 새로운 프로필 이미지 URL
     * @return 업데이트된 Profile 인스턴스
     */
    fun update(
        nickname: String = this.nickname,
        bio: String? = this.bio,
        profileImageUrl: String? = this.profileImageUrl
    ): Profile {
        validateNickname(nickname)
        bio?.let { validateBio(it) }

        return copy(
            nickname = nickname,
            bio = bio,
            profileImageUrl = profileImageUrl,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 프로필 이미지를 업데이트합니다.
     *
     * @param imageUrl 새로운 프로필 이미지 URL
     * @return 업데이트된 Profile 인스턴스
     */
    fun updateProfileImage(imageUrl: String): Profile {
        return copy(
            profileImageUrl = imageUrl,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 프로필 이미지를 제거합니다.
     *
     * @return 업데이트된 Profile 인스턴스
     */
    fun removeProfileImage(): Profile {
        return copy(
            profileImageUrl = null,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 프로필이 완성되었는지 확인합니다.
     * 닉네임과 자기소개가 모두 있으면 완성된 것으로 간주합니다.
     *
     * @return 프로필이 완성되었으면 true, 그렇지 않으면 false
     */
    fun isComplete(): Boolean {
        return nickname.isNotBlank() && !bio.isNullOrBlank()
    }

    companion object {
        /**
         * 닉네임 최소 길이
         */
        private const val MIN_NICKNAME_LENGTH = 2

        /**
         * 닉네임 최대 길이
         */
        private const val MAX_NICKNAME_LENGTH = 20

        /**
         * 자기소개 최대 길이
         */
        private const val MAX_BIO_LENGTH = 500

        /**
         * 닉네임 유효성 검증
         *
         * @param nickname 검증할 닉네임
         * @throws IllegalArgumentException 닉네임이 유효하지 않은 경우
         */
        private fun validateNickname(nickname: String) {
            require(nickname.isNotBlank()) {
                "닉네임은 필수값입니다"
            }

            require(nickname.length >= MIN_NICKNAME_LENGTH) {
                "닉네임은 최소 $MIN_NICKNAME_LENGTH 자 이상이어야 합니다"
            }

            require(nickname.length <= MAX_NICKNAME_LENGTH) {
                "닉네임은 최대 $MAX_NICKNAME_LENGTH 자 이하여야 합니다"
            }
        }

        /**
         * 자기소개 유효성 검증
         *
         * @param bio 검증할 자기소개
         * @throws IllegalArgumentException 자기소개가 유효하지 않은 경우
         */
        private fun validateBio(bio: String) {
            require(bio.length <= MAX_BIO_LENGTH) {
                "자기소개는 최대 $MAX_BIO_LENGTH 자 이하여야 합니다"
            }
        }

        /**
         * 새로운 Profile을 생성합니다.
         *
         * @param userId 사용자 ID
         * @param nickname 닉네임
         * @param bio 자기소개 (선택)
         * @param profileImageUrl 프로필 이미지 URL (선택)
         * @return 새로운 Profile 인스턴스
         */
        fun create(
            userId: UserId,
            nickname: String,
            bio: String? = null,
            profileImageUrl: String? = null
        ): Profile {
            return Profile(
                entityId = userId,
                nickname = nickname,
                bio = bio,
                profileImageUrl = profileImageUrl,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }
    }
}

package cloud.luigi99.blog.user.domain.vo

/**
 * 사용자 역할을 나타내는 열거형
 *
 * 사용자의 권한 레벨을 정의합니다.
 *
 * @property displayName 화면에 표시될 역할명
 * @property description 역할 설명
 * @property level 권한 레벨 (높을수록 강한 권한)
 */
enum class UserRole(
    val displayName: String,
    val description: String,
    val level: Int
) {
    /**
     * 게스트
     * 비회원 또는 제한된 권한을 가진 사용자
     */
    GUEST("게스트", "제한된 권한", 0),

    /**
     * 일반 사용자
     * 기본 권한을 가진 사용자
     */
    USER("사용자", "일반 사용자 권한", 1),

    /**
     * 관리자
     * 모든 권한을 가진 최상위 사용자 (블로그 소유자)
     */
    ADMIN("관리자", "시스템 전체 관리 및 콘텐츠 작성 권한", 2);

    /**
     * 게스트 권한인지 확인합니다.
     *
     * @return 게스트이면 true, 그렇지 않으면 false
     */
    fun isGuest(): Boolean = this == GUEST

    /**
     * 일반 사용자 권한인지 확인합니다.
     *
     * @return 일반 사용자이면 true, 그렇지 않으면 false
     */
    fun isUser(): Boolean = this == USER

    /**
     * 관리자 권한인지 확인합니다.
     *
     * @return 관리자이면 true, 그렇지 않으면 false
     */
    fun isAdmin(): Boolean = this == ADMIN

    /**
     * 콘텐츠 작성 권한이 있는지 확인합니다.
     * 관리자만 콘텐츠를 작성할 수 있습니다.
     *
     * @return 콘텐츠 작성 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canWriteContent(): Boolean = this == ADMIN

    /**
     * 관리 권한이 있는지 확인합니다.
     * 관리자만 관리 기능을 사용할 수 있습니다.
     *
     * @return 관리 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canManage(): Boolean = this == ADMIN

    /**
     * 댓글 작성 권한이 있는지 확인합니다.
     * 일반 사용자와 관리자만 댓글을 작성할 수 있습니다.
     *
     * @return 댓글 작성 권한이 있으면 true, 그렇지 않으면 false
     */
    fun canComment(): Boolean = this == USER || this == ADMIN

    /**
     * 다른 역할보다 높은 권한 레벨을 가지는지 확인합니다.
     *
     * @param other 비교할 역할
     * @return 현재 역할이 더 높은 권한이면 true, 그렇지 않으면 false
     */
    fun hasHigherLevelThan(other: UserRole): Boolean = this.level > other.level

    /**
     * 다른 역할보다 낮거나 같은 권한 레벨을 가지는지 확인합니다.
     *
     * @param other 비교할 역할
     * @return 현재 역할이 더 낮거나 같은 권한이면 true, 그렇지 않으면 false
     */
    fun hasLowerOrEqualLevelThan(other: UserRole): Boolean = this.level <= other.level

    companion object {
        /**
         * 문자열로부터 UserRole을 찾습니다.
         *
         * @param value 역할 문자열 (enum name)
         * @return 해당하는 UserRole, 없으면 null
         */
        fun fromString(value: String): UserRole? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }

        /**
         * 문자열로부터 UserRole을 찾습니다. 없으면 기본값을 반환합니다.
         *
         * @param value 역할 문자열 (enum name)
         * @param defaultValue 기본값 (기본: USER)
         * @return 해당하는 UserRole 또는 기본값
         */
        fun fromStringOrDefault(value: String, defaultValue: UserRole = USER): UserRole {
            return fromString(value) ?: defaultValue
        }
    }
}

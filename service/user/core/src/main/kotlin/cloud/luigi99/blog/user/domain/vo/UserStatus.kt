package cloud.luigi99.blog.user.domain.vo

/**
 * 사용자 상태를 나타내는 열거형
 *
 * 사용자 계정의 현재 상태를 정의합니다.
 *
 * @property displayName 화면에 표시될 상태명
 * @property description 상태 설명
 */
enum class UserStatus(
    val displayName: String,
    val description: String
) {
    /**
     * 활성 상태
     * 정상적으로 서비스를 이용할 수 있는 상태
     */
    ACTIVE("활성", "정상적으로 서비스를 이용할 수 있습니다"),

    /**
     * 비활성 상태
     * 사용자가 자발적으로 계정을 비활성화한 상태
     * 재활성화 가능
     */
    INACTIVE("비활성", "계정이 비활성화되었습니다"),

    /**
     * 정지 상태
     * 관리자에 의해 계정이 정지된 상태
     * 서비스 이용 불가
     */
    SUSPENDED("정지", "계정이 정지되었습니다");

    /**
     * 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     */
    fun isActive(): Boolean = this == ACTIVE

    /**
     * 비활성 상태인지 확인합니다.
     *
     * @return 비활성 상태이면 true, 그렇지 않으면 false
     */
    fun isInactive(): Boolean = this == INACTIVE

    /**
     * 정지 상태인지 확인합니다.
     *
     * @return 정지 상태이면 true, 그렇지 않으면 false
     */
    fun isSuspended(): Boolean = this == SUSPENDED

    /**
     * 서비스 이용 가능한 상태인지 확인합니다.
     * 활성 상태만 서비스를 이용할 수 있습니다.
     *
     * @return 서비스 이용 가능하면 true, 그렇지 않으면 false
     */
    fun canUseService(): Boolean = isActive()

    companion object {
        /**
         * 문자열로부터 UserStatus를 찾습니다.
         *
         * @param value 상태 문자열 (enum name)
         * @return 해당하는 UserStatus, 없으면 null
         */
        fun fromString(value: String): UserStatus? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }

        /**
         * 문자열로부터 UserStatus를 찾습니다. 없으면 기본값을 반환합니다.
         *
         * @param value 상태 문자열 (enum name)
         * @param defaultValue 기본값 (기본: ACTIVE)
         * @return 해당하는 UserStatus 또는 기본값
         */
        fun fromStringOrDefault(value: String, defaultValue: UserStatus = ACTIVE): UserStatus {
            return fromString(value) ?: defaultValue
        }
    }
}

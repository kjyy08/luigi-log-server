package cloud.luigi99.blog.common.validation

class PasswordValidator(
    private val minLength: Int = 8,
    private val requireUppercase: Boolean = true,
    private val requireLowercase: Boolean = true,
    private val requireDigit: Boolean = true,
    private val requireSpecialChar: Boolean = true
) : Validator<String?> {

    override fun validate(value: String?): ValidationResult {
        if (value == null) {
            return ValidationResult.failure("비밀번호는 필수입니다")
        }

        if (value.length < minLength) {
            return ValidationResult.failure("비밀번호는 최소 ${minLength}자 이상이어야 합니다")
        }

        if (requireUppercase && !value.any { it.isUpperCase() }) {
            return ValidationResult.failure("대문자를 포함해야 합니다")
        }

        if (requireLowercase && !value.any { it.isLowerCase() }) {
            return ValidationResult.failure("소문자를 포함해야 합니다")
        }

        if (requireDigit && !value.any { it.isDigit() }) {
            return ValidationResult.failure("숫자를 포함해야 합니다")
        }

        if (requireSpecialChar && !value.any { it in "!@#\$%^&*()_+-=[]{}|;:,.<>?" }) {
            return ValidationResult.failure("특수문자를 포함해야 합니다")
        }

        return ValidationResult.success()
    }
}
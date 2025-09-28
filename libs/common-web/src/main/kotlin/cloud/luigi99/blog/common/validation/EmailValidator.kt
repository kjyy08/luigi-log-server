package cloud.luigi99.blog.common.validation

import java.util.regex.Pattern

class EmailValidator(
    private val pattern: Pattern = DEFAULT_EMAIL_PATTERN
) : Validator<String?> {

    override fun validate(value: String?): ValidationResult {
        return if (value != null && pattern.matcher(value).matches()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure("유효하지 않은 이메일 형식입니다")
        }
    }

    companion object {
        private val DEFAULT_EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_-]+(?:\\.[A-Za-z0-9+_-]+)*@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$"
        )
    }
}
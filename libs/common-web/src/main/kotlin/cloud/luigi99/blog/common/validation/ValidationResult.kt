package cloud.luigi99.blog.common.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        fun success() = ValidationResult(true)
        fun failure(message: String) = ValidationResult(false, message)
    }
}
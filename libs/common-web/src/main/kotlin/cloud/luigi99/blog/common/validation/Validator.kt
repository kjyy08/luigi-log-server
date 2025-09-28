package cloud.luigi99.blog.common.validation

fun interface Validator<T> {
    fun validate(value: T): ValidationResult
}
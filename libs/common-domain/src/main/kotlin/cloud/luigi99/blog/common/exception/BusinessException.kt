package cloud.luigi99.blog.common.exception

open class BusinessException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
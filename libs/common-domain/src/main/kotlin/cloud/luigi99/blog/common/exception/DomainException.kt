package cloud.luigi99.blog.common.exception

class DomainException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)
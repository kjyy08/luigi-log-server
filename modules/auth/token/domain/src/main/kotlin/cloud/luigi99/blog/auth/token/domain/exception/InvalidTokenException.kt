package cloud.luigi99.blog.auth.token.domain.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class InvalidTokenException(message: String = "Invalid or expired JWT token") :
    BusinessException(ErrorCode.INVALID_TOKEN, message)

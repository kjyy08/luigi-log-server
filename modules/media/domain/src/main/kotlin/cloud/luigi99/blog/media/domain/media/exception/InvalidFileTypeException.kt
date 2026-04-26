package cloud.luigi99.blog.media.domain.media.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class InvalidFileTypeException(message: String = ErrorCode.INVALID_FILE_TYPE.message) :
    BusinessException(ErrorCode.INVALID_FILE_TYPE, message)

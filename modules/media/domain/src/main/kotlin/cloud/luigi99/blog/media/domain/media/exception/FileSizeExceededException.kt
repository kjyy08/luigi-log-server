package cloud.luigi99.blog.media.domain.media.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class FileSizeExceededException(message: String = ErrorCode.FILE_SIZE_EXCEEDED.message) :
    BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, message)

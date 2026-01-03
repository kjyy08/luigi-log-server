package cloud.luigi99.blog.media.domain.media.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 파일을 찾을 수 없을 때 발생하는 예외
 *
 * HTTP Status: 404 NOT_FOUND
 */
class FileNotFoundException(message: String = "파일을 찾을 수 없습니다.") :
    BusinessException(ErrorCode.INVALID_INPUT, message)

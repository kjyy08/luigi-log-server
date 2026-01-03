package cloud.luigi99.blog.media.domain.media.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 파일 업로드 실패 시 발생하는 예외
 *
 * HTTP Status: 500 INTERNAL_SERVER_ERROR
 */
class FileUploadFailedException(message: String = "파일 업로드에 실패했습니다.") :
    BusinessException(ErrorCode.FILE_UPLOAD_FAILED, message)

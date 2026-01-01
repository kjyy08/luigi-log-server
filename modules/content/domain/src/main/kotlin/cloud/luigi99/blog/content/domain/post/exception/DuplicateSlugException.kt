package cloud.luigi99.blog.content.domain.post.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 중복된 슬러그가 존재할 때 발생하는 예외
 *
 * HTTP Status: 409 CONFLICT
 * ErrorCode: CONTENT_002
 */
class DuplicateSlugException(message: String = "이미 존재하는 슬러그입니다.") :
    BusinessException(ErrorCode.SLUG_ALREADY_EXISTS, message)

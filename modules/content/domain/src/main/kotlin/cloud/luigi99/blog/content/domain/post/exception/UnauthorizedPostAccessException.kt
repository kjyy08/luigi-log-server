package cloud.luigi99.blog.content.domain.post.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 게시글에 대한 권한이 없을 때 발생하는 예외
 *
 * HTTP Status: 403 FORBIDDEN
 * ErrorCode: CONTENT_004
 */
class UnauthorizedPostAccessException(message: String = "게시글에 대한 권한이 없습니다.") :
    BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS, message)

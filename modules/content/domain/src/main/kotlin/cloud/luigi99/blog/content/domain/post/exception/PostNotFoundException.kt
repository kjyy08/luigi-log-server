package cloud.luigi99.blog.content.domain.post.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 게시글을 찾을 수 없을 때 발생하는 예외
 *
 * HTTP Status: 404 NOT_FOUND
 * ErrorCode: CONTENT_001
 */
class PostNotFoundException(message: String = "게시글을 찾을 수 없습니다.") : BusinessException(ErrorCode.POST_NOT_FOUND, message)

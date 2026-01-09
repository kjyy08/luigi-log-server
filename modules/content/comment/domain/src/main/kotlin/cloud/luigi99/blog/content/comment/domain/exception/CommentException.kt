package cloud.luigi99.blog.content.comment.domain.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 댓글을 찾을 수 없을 때 발생하는 예외
 */
class CommentNotFoundException(message: String = "Comment not found") :
    BusinessException(ErrorCode.COMMENT_NOT_FOUND, message)

/**
 * 댓글 접근 권한이 없을 때 발생하는 예외
 */
class UnauthorizedCommentAccessException(message: String = "Unauthorized access to comment") :
    BusinessException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS, message)

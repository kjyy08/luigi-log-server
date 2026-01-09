package cloud.luigi99.blog.content.guestbook.domain.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

/**
 * 방명록을 찾을 수 없는 경우 발생하는 예외
 */
class GuestbookNotFoundException(message: String = "방명록을 찾을 수 없습니다") :
    BusinessException(ErrorCode.GUESTBOOK_NOT_FOUND, message)

/**
 * 방명록에 대한 권한이 없는 경우 발생하는 예외
 */
class UnauthorizedGuestbookAccessException(message: String = "방명록에 대한 권한이 없습니다") :
    BusinessException(ErrorCode.UNAUTHORIZED_GUESTBOOK_ACCESS, message)

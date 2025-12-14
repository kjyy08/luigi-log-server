package cloud.luigi99.blog.member.domain.member.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class MemberNotFoundException(message: String = "Member not found") :
    BusinessException(ErrorCode.MEMBER_NOT_FOUND, message)

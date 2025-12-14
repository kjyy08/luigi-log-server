package cloud.luigi99.blog.member.domain.profile.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class ProfileException(message: String = "Profile not found") : BusinessException(ErrorCode.PROFILE_NOT_FOUND, message)

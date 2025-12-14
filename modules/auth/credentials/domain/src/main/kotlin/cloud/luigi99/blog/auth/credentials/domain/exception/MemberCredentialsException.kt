package cloud.luigi99.blog.auth.credentials.domain.exception

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode

class MemberCredentialsException(message: String = "Member credentials not found") :
    BusinessException(ErrorCode.CREDENTIAL_NOT_FOUND, message)

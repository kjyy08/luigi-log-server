package cloud.luigi99.blog.auth.credentials.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.time.LocalDateTime

data class LastLoginTime(val value: LocalDateTime) : ValueObject {
    companion object {
        fun now(): LastLoginTime = LastLoginTime(LocalDateTime.now())
    }
}

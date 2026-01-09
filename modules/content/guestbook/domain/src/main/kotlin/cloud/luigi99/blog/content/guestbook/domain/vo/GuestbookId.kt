package cloud.luigi99.blog.content.guestbook.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

/**
 * 방명록 식별자 값 객체
 *
 * 방명록 엔티티를 고유하게 식별하기 위한 UUID 기반 값 객체입니다.
 */
@JvmInline
value class GuestbookId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        /**
         * 새로운 방명록 식별자를 생성합니다.
         */
        fun generate(): GuestbookId = GuestbookId(UUID.randomUUID())

        /**
         * 문자열로부터 방명록 식별자를 복원합니다.
         */
        fun from(value: String): GuestbookId = GuestbookId(UUID.fromString(value))
    }
}

package cloud.luigi99.blog.content.comment.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

/**
 * 댓글 식별자
 *
 * 댓글의 고유 식별자를 나타내는 Value Object입니다.
 */
@JvmInline
value class CommentId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        /**
         * 새로운 댓글 식별자를 생성합니다.
         */
        fun generate(): CommentId = CommentId(UUID.randomUUID())

        /**
         * 문자열로부터 댓글 식별자를 생성합니다.
         */
        fun from(value: String): CommentId = CommentId(UUID.fromString(value))
    }
}

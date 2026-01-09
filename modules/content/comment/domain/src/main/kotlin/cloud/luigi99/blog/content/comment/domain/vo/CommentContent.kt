package cloud.luigi99.blog.content.comment.domain.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 댓글 내용
 *
 * 댓글의 본문 내용을 나타내는 Value Object입니다.
 * 1자 이상 1000자 이하의 유효한 텍스트만 허용합니다.
 */
@JvmInline
value class CommentContent(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Comment content cannot be blank" }
        require(value.length in 1..1000) {
            "Comment content must be between 1 and 1000 characters (actual: ${value.length})"
        }
    }
}

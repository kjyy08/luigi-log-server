package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * Post의 제목
 *
 * 1자 이상 200자 이하의 제목을 가집니다.
 *
 * @property value 제목 문자열
 * @throws IllegalArgumentException 제목이 빈 값이거나 200자를 초과하는 경우
 */
@JvmInline
value class Title(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Title cannot be blank" }
        require(value.length in 1..200) {
            "Title must be between 1 and 200 characters (actual: ${value.length})"
        }
    }

    override fun toString(): String = value
}

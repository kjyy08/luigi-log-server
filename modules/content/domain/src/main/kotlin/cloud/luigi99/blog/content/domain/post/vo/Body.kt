package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * Post의 본문 내용
 *
 * Markdown 형식의 본문을 저장합니다.
 * 빈 값은 허용되지 않습니다.
 *
 * @property value Markdown 형식의 본문 문자열
 * @throws IllegalArgumentException 본문이 빈 값인 경우
 */
@JvmInline
value class Body(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Body cannot be blank" }
    }

    override fun toString(): String = value
}

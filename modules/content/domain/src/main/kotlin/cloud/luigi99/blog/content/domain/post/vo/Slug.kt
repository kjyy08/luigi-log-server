package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * Post의 URL slug
 *
 * URL에 사용되는 고유 식별자로 소문자 영문자, 숫자, 하이픈만 허용됩니다.
 * - 소문자 영문자(a-z), 숫자(0-9), 하이픈(-)만 사용 가능
 * - 하이픈으로 시작하거나 끝날 수 없음
 * - 연속된 하이픈 불가
 * - 255자 이하
 *
 * @property value slug 문자열
 * @throws IllegalArgumentException slug 형식이 잘못된 경우
 */
@JvmInline
value class Slug(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Slug cannot be blank" }
        require(value.length <= 255) {
            "Slug must not exceed 255 characters (actual: ${value.length})"
        }
        require(value.matches(SLUG_REGEX)) {
            "Invalid slug format: $value (allowed: lowercase letters, numbers, and hyphens)"
        }
    }

    companion object {
        private val SLUG_REGEX = "^[a-z0-9]+(-[a-z0-9]+)*$".toRegex()
    }

    override fun toString(): String = value
}

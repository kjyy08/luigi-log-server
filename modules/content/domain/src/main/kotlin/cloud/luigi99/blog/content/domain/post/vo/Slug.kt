package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * Post의 URL slug
 *
 * URL에 사용되는 고유 식별자로 한글, 영문 소문자, 숫자, 하이픈, 대괄호를 허용합니다.
 * - 한글(가-힣), 영문 소문자(a-z), 대문자(A-Z), 숫자(0-9), 하이픈(-), 대괄호([])만 사용 가능
 * - 하이픈으로 시작하거나 끝날 수 없음
 * - 연속된 하이픈 불가
 * - 500자 이하
 *
 * @property value slug 문자열
 * @throws IllegalArgumentException slug 형식이 잘못된 경우
 */
@JvmInline
value class Slug(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Slug cannot be blank" }
        require(value.length <= 500) {
            "Slug must not exceed 500 characters (actual: ${value.length})"
        }
        require(value.matches(SLUG_REGEX)) {
            "Invalid slug format: $value (allowed: Korean(가-힣), letters(a-zA-Z), numbers, hyphens, and brackets)"
        }
    }

    companion object {
        private val SLUG_REGEX = "^[a-zA-Z0-9가-힣\\[\\]]+(-[a-zA-Z0-9가-힣\\[\\]]+)*$".toRegex()
    }

    override fun toString(): String = value
}

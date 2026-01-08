package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * GitHub 스타일 프로필 Readme (Markdown)
 *
 * 사용자의 프로필 소개를 Markdown 형식으로 작성할 수 있는 Value Object
 */
@JvmInline
value class Readme(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Readme cannot be blank" }
        require(value.length <= 10000) { "Readme cannot exceed 10000 characters" }
    }
}

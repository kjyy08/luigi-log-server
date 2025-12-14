package cloud.luigi99.blog.member.domain.profile.vo

import cloud.luigi99.blog.common.domain.ValueObject

data class TechStack(val values: List<String>) : ValueObject {
    init {
        require(values.all { it.isNotBlank() }) { "Tech stack items cannot be blank" }
        require(values.size <= 50) { "Tech stack cannot exceed 50 items" }
    }

    fun isEmpty(): Boolean = values.isEmpty()
}

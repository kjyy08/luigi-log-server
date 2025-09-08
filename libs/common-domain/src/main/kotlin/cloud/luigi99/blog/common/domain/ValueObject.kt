package cloud.luigi99.blog.common.domain

abstract class ValueObject {
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}
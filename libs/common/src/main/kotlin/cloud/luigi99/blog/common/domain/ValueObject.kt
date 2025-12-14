package cloud.luigi99.blog.common.domain

/**
 * DDD Value Object 마커 인터페이스
 * 불변 객체이며, 속성 값으로 동등성을 판단합니다.
 * Kotlin의 data class를 사용하여 구현하는 것을 권장합니다.
 */
interface ValueObject {
    override fun toString(): String
}

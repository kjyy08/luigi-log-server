package cloud.luigi99.blog.common.domain

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * ValueObject 인터페이스는 도메인 주도 설계의 값 객체 계약을 정의합니다.
 *
 * 값 객체의 핵심 특성:
 * - 불변성: 생성 후 상태가 변경되지 않음
 * - 동등성: 내부 값이 같으면 동일한 객체로 취급
 * - 타입 안전성: 컴파일 타임에 타입 검증
 *
 * Kotlin data class와 완벽하게 호환됩니다:
 * ```kotlin
 * @JvmInline
 * value class Email(val value: String) : ValueObject {
 *     init {
 *         require(value.isValidEmail()) { "유효하지 않은 이메일 형식: $value" }
 *     }
 * }
 *
 * data class Money(val amount: BigDecimal, val currency: String) : ValueObject {
 *     init {
 *         require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다" }
 *         require(currency.isNotBlank()) { "통화 정보는 필수입니다" }
 *     }
 * }
 * ```
 */
interface ValueObject {
    /**
     * 값 객체의 유효성을 검증합니다.
     * 구현체에서 도메인 규칙에 따른 검증 로직을 제공해야 합니다.
     *
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    fun isValid(): Boolean = true

    /**
     * 값 객체가 비어있는지 확인합니다.
     * 기본 구현은 false를 반환하며, 필요시 구현체에서 오버라이드할 수 있습니다.
     *
     * @return 비어있는 경우 true, 그렇지 않으면 false
     */
    fun isEmpty(): Boolean = false

    /**
     * 값 객체가 비어있지 않은지 확인합니다.
     *
     * @return 비어있지 않은 경우 true, 그렇지 않으면 false
     */
    fun isNotEmpty(): Boolean = !isEmpty()
}

/**
 * 문자열 기반 값 객체의 기본 구현을 제공하는 인터페이스
 */
interface StringValueObject : ValueObject {
    val value: String

    override fun isEmpty(): Boolean = value.isBlank()

    override fun isValid(): Boolean = value.isNotBlank()
}

/**
 * 숫자 기반 값 객체의 기본 구현을 제공하는 인터페이스
 */
interface NumericValueObject<T : Number> : ValueObject {
    val value: T

    /**
     * 값이 0인지 확인합니다.
     */
    fun isZero(): Boolean

    /**
     * 값이 양수인지 확인합니다.
     */
    fun isPositive(): Boolean

    /**
     * 값이 음수인지 확인합니다.
     */
    fun isNegative(): Boolean
}

/**
 * 값 객체의 유효성 검증을 위한 확장 함수들
 */
inline fun ValueObject.requireValid(lazyMessage: () -> Any = { "값 객체가 유효하지 않습니다" }) {
    require(this.isValid(), lazyMessage)
}

inline fun ValueObject.requireNotEmpty(lazyMessage: () -> Any = { "값 객체가 비어있습니다" }) {
    require(this.isNotEmpty(), lazyMessage)
}
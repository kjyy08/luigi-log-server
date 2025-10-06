package cloud.luigi99.blog.common.domain

/**
 * 값 객체(Value Object)의 기반이 되는 추상 클래스
 *
 * 개인 기술 블로그 프로젝트에서 사용되는 모든 값 객체는 이 클래스를 상속받아
 * 값 기반의 동등성과 불변성을 보장받습니다.
 *
 * 값 객체는 다음 특징을 가집니다:
 * - 불변성: 생성 후 상태가 변경되지 않음
 * - 값 동등성: 속성 값이 같으면 동일한 객체로 간주
 * - 부작용 없음: 메서드 호출이 객체 상태를 변경하지 않음
 */
abstract class ValueObject {

    /**
     * 값 객체의 동등성을 판단합니다.
     *
     * 구현체에서는 모든 속성 값을 기준으로 동등성을 판단해야 합니다.
     *
     * @param other 비교할 객체
     * @return 모든 속성 값이 같으면 true, 다르면 false
     */
    abstract override fun equals(other: Any?): Boolean

    /**
     * 값 객체의 해시코드를 생성합니다.
     *
     * 구현체에서는 모든 속성 값을 기준으로 해시코드를 생성해야 합니다.
     * equals가 true인 객체들은 반드시 같은 해시코드를 반환해야 합니다.
     *
     * @return 모든 속성 값을 기준으로 한 해시코드
     */
    abstract override fun hashCode(): Int

    /**
     * 값 객체의 문자열 표현을 생성합니다.
     *
     * 기본 구현으로 클래스명과 해시코드를 포함한 문자열을 제공합니다.
     * 필요시 구현체에서 오버라이드하여 더 의미있는 문자열을 제공할 수 있습니다.
     *
     * @return 값 객체의 문자열 표현
     */
    override fun toString(): String {
        return "${this::class.simpleName}@${hashCode().toString(16)}"
    }

    /**
     * 값 객체의 유효성을 검증합니다.
     *
     * 모든 값 객체는 이 메서드를 구현하여 비즈니스 규칙에 따른 유효성 검증을 수행해야 합니다.
     * 검증이 필요 없는 단순한 값 객체의 경우에도 명시적으로 true를 반환하도록 구현해야 합니다.
     *
     * @return 값 객체가 유효하면 true, 그렇지 않으면 false
     */
    protected abstract fun isValid(): Boolean

    /**
     * 값 객체의 유효성을 검증하고 유효하지 않은 경우 예외를 발생시킵니다.
     *
     * 값 객체 생성 시 호출하여 유효하지 않은 값 객체 생성을 방지할 수 있습니다.
     *
     * @throws IllegalArgumentException 값 객체가 유효하지 않은 경우
     */
    protected fun validate() {
        require(isValid()) {
            "${this::class.simpleName} is not valid"
        }
    }
}
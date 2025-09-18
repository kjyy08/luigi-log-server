package cloud.luigi99.blog.common.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.assertions.throwables.shouldThrow

/**
 * ValueObject의 행위를 검증하는 테스트
 */
class ValueObjectTest : BehaviorSpec({

    given("동일한 값을 가진 두 개의 ValueObject 구현체가 있을 때") {
        val value1 = TestValueObject("테스트값", 42)
        val value2 = TestValueObject("테스트값", 42)

        `when`("equals 메서드를 호출하면") {
            val result = value1.equals(value2)

            then("값이 같으므로 true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = value1.hashCode()
            val hashCode2 = value2.hashCode()

            then("값이 같으므로 같은 해시코드를 반환한다") {
                hashCode1 shouldBe hashCode2
            }
        }
    }

    given("서로 다른 값을 가진 두 개의 ValueObject 구현체가 있을 때") {
        val value1 = TestValueObject("테스트값1", 42)
        val value2 = TestValueObject("테스트값2", 42)

        `when`("equals 메서드를 호출하면") {
            val result = value1.equals(value2)

            then("값이 다르므로 false를 반환한다") {
                result shouldBe false
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = value1.hashCode()
            val hashCode2 = value2.hashCode()

            then("값이 다르므로 다른 해시코드를 반환한다") {
                hashCode1 shouldNotBe hashCode2
            }
        }
    }

    given("ValueObject 구현체가 있을 때") {
        val valueObject = TestValueObject("테스트값", 42)

        `when`("toString 메서드를 호출하면") {
            val result = valueObject.toString()

            then("클래스명과 해시코드를 포함한 문자열을 반환한다") {
                result shouldContain "TestValueObject"
                result shouldContain "@"
            }
        }
    }

    given("ValueObject 구현체가 자기 자신과 비교될 때") {
        val valueObject = TestValueObject("테스트값", 42)

        `when`("equals 메서드로 자기 자신과 비교하면") {
            val result = valueObject.equals(valueObject)

            then("true를 반환한다") {
                result shouldBe true
            }
        }
    }

    given("ValueObject 구현체가 null과 비교될 때") {
        val valueObject = TestValueObject("테스트값", 42)

        `when`("equals 메서드로 null과 비교하면") {
            val result = valueObject.equals(null)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("ValueObject 구현체가 다른 타입의 객체와 비교될 때") {
        val valueObject = TestValueObject("테스트값", 42)
        val otherObject = "문자열 객체"

        `when`("equals 메서드로 다른 타입 객체와 비교하면") {
            val result = valueObject.equals(otherObject)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("유효성 검증을 재정의한 ValueObject가 있을 때") {
        `when`("유효하지 않은 값으로 생성하면") {
            then("IllegalArgumentException이 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    InvalidTestValueObject("잘못된값")
                }
            }
        }

        `when`("유효한 값으로 생성하면") {
            val validValue = InvalidTestValueObject("유효한값")

            then("정상적으로 생성된다") {
                validValue.value shouldBe "유효한값"
            }
        }
    }

    given("기본 유효성 검증을 사용하는 ValueObject가 있을 때") {
        val valueObject = TestValueObject("테스트값", 42)

        `when`("validate 메서드를 호출하면") {
            then("예외가 발생하지 않는다") {
                // 기본 isValid()가 true를 반환하므로 예외가 발생하지 않아야 함
                valueObject.testValidate()
            }
        }
    }
})

/**
 * 테스트용 ValueObject 구현체
 */
private class TestValueObject(
    val name: String,
    val number: Int
) : ValueObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestValueObject) return false
        return name == other.name && number == other.number
    }

    override fun hashCode(): Int {
        return 31 * name.hashCode() + number
    }

    fun testValidate() {
        validate()
    }
}

/**
 * 유효성 검증을 포함한 테스트용 ValueObject 구현체
 */
private class InvalidTestValueObject(
    val value: String
) : ValueObject() {

    init {
        validate()
    }

    override fun isValid(): Boolean {
        return value != "잘못된값"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InvalidTestValueObject) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
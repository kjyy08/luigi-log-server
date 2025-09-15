package cloud.luigi99.blog.common.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.NullAndEmptySource
import java.math.BigDecimal
import kotlin.test.*

@DisplayName("ValueObject 인터페이스 및 특화 구현체 테스트")
class ValueObjectTest {

    /**
     * 기본 ValueObject 구현체 (테스트용)
     */
    private data class SimpleValueObject(val data: String) : ValueObject {
        override fun isValid(): Boolean = data.length >= 3
        override fun isEmpty(): Boolean = data.isEmpty()
    }

    /**
     * StringValueObject 구현체 (테스트용)
     */
    @JvmInline
    private value class Email(override val value: String) : StringValueObject {
        init {
            requireValid { "유효하지 않은 이메일 형식: $value" }
        }

        override fun isValid(): Boolean =
            value.isNotBlank() &&
            value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) &&
            !value.startsWith("@") &&
            !value.endsWith("@") &&
            value.count { it == '@' } == 1
    }

    /**
     * 사용자명 ValueObject (테스트용)
     */
    private data class Username(override val value: String) : StringValueObject {
        init {
            requireNotEmpty { "사용자명은 비어있을 수 없습니다" }
        }

        override fun isValid(): Boolean = value.length >= 3 && value.length <= 20
    }

    /**
     * NumericValueObject 구현체 - Money (테스트용)
     */
    private data class Money(override val value: BigDecimal, val currency: String) : NumericValueObject<BigDecimal> {

        override fun isZero(): Boolean = value.compareTo(BigDecimal.ZERO) == 0

        override fun isPositive(): Boolean = value.compareTo(BigDecimal.ZERO) > 0

        override fun isNegative(): Boolean = value.compareTo(BigDecimal.ZERO) < 0

        override fun isValid(): Boolean = currency.isNotBlank() && currency.length == 3

        override fun isEmpty(): Boolean = isZero()
    }

    /**
     * NumericValueObject 구현체 - Quantity (테스트용)
     */
    private data class Quantity(override val value: Int) : NumericValueObject<Int> {

        override fun isZero(): Boolean = value == 0

        override fun isPositive(): Boolean = value > 0

        override fun isNegative(): Boolean = value < 0

        override fun isValid(): Boolean = value >= 0

        override fun isEmpty(): Boolean = isZero()
    }

    @Nested
    @DisplayName("기본 ValueObject 인터페이스 테스트")
    inner class BaseValueObjectTest {

        @Test
        @DisplayName("기본 ValueObject 구현체의 기본 동작 확인")
        fun `default ValueObject implementation works correctly`() {
            // Given
            val validObject = SimpleValueObject("valid")
            val invalidObject = SimpleValueObject("no")
            val emptyObject = SimpleValueObject("")

            // When & Then
            assertAll(
                { assertTrue(validObject.isValid()) },
                { assertFalse(invalidObject.isValid()) },
                { assertFalse(validObject.isEmpty()) },
                { assertTrue(emptyObject.isEmpty()) },
                { assertTrue(validObject.isNotEmpty()) },
                { assertFalse(emptyObject.isNotEmpty()) }
            )
        }

        @Test
        @DisplayName("requireValid() 확장 함수는 유효하지 않은 객체에 대해 예외를 던진다")
        fun `requireValid throws exception for invalid object`() {
            // Given
            val invalidObject = SimpleValueObject("no")

            // When & Then
            val exception = assertThrows<IllegalArgumentException> {
                invalidObject.requireValid { "사용자 정의 오류 메시지" }
            }
            assertEquals("사용자 정의 오류 메시지", exception.message)
        }

        @Test
        @DisplayName("requireValid() 확장 함수는 유효한 객체에 대해 예외를 던지지 않는다")
        fun `requireValid does not throw exception for valid object`() {
            // Given
            val validObject = SimpleValueObject("valid")

            // When & Then
            assertDoesNotThrow {
                validObject.requireValid()
            }
        }

        @Test
        @DisplayName("requireNotEmpty() 확장 함수는 비어있는 객체에 대해 예외를 던진다")
        fun `requireNotEmpty throws exception for empty object`() {
            // Given
            val emptyObject = SimpleValueObject("")

            // When & Then
            val exception = assertThrows<IllegalArgumentException> {
                emptyObject.requireNotEmpty { "비어있는 객체입니다" }
            }
            assertEquals("비어있는 객체입니다", exception.message)
        }

        @Test
        @DisplayName("requireNotEmpty() 확장 함수는 비어있지 않은 객체에 대해 예외를 던지지 않는다")
        fun `requireNotEmpty does not throw exception for non-empty object`() {
            // Given
            val nonEmptyObject = SimpleValueObject("content")

            // When & Then
            assertDoesNotThrow {
                nonEmptyObject.requireNotEmpty()
            }
        }
    }

    @Nested
    @DisplayName("StringValueObject 인터페이스 테스트")
    inner class StringValueObjectTest {

        @Test
        @DisplayName("유효한 이메일 생성")
        fun `valid email creation`() {
            // Given
            val validEmailString = "test@example.com"

            // When
            val email = Email(validEmailString)

            // Then
            assertAll(
                { assertEquals(validEmailString, email.value) },
                { assertTrue(email.isValid()) },
                { assertFalse(email.isEmpty()) },
                { assertTrue(email.isNotEmpty()) }
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["invalid-email", "@example.com", "test@", "test", ""])
        @DisplayName("유효하지 않은 이메일로 생성 시 예외 발생")
        fun `invalid email creation throws exception`(invalidEmail: String) {
            // When & Then
            assertThrows<IllegalArgumentException> {
                Email(invalidEmail)
            }
        }

        @Test
        @DisplayName("사용자명 길이 검증")
        fun `username length validation`() {
            // Given
            val validUsername = Username("validuser")
            val shortUsername = "ab"
            val longUsername = "a".repeat(25)

            // When & Then
            assertAll(
                { assertTrue(validUsername.isValid()) },
                { assertFalse(Username(shortUsername).isValid()) },
                { assertFalse(Username(longUsername).isValid()) }
            )
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = [" ", "\t", "\n"])
        @DisplayName("빈 문자열 및 공백 문자로 사용자명 생성 시 예외 발생")
        fun `empty or blank username creation throws exception`(blankUsername: String?) {
            // When & Then
            assertThrows<IllegalArgumentException> {
                Username(blankUsername ?: "")
            }
        }

        @Test
        @DisplayName("StringValueObject의 isEmpty() 동작 확인")
        fun `StringValueObject isEmpty behavior`() {
            // Given
            val emptyString = Username("content") // 실제로는 비어있지 않음
            val emptyUsername = object : StringValueObject {
                override val value: String = ""
            }
            val blankUsername = object : StringValueObject {
                override val value: String = "   "
            }

            // When & Then
            assertAll(
                { assertFalse(emptyString.isEmpty()) },
                { assertTrue(emptyUsername.isEmpty()) },
                { assertTrue(blankUsername.isEmpty()) } // isBlank()을 사용하므로 공백도 empty
            )
        }
    }

    @Nested
    @DisplayName("NumericValueObject 인터페이스 테스트")
    inner class NumericValueObjectTest {

        @Test
        @DisplayName("Money 값 객체의 기본 동작 확인")
        fun `Money value object basic operations`() {
            // Given
            val positiveMoney = Money(BigDecimal("100.50"), "USD")
            val zeroMoney = Money(BigDecimal.ZERO, "EUR")
            val negativeMoney = Money(BigDecimal("-50.25"), "KRW")

            // When & Then
            assertAll(
                // 양수 금액
                { assertTrue(positiveMoney.isPositive()) },
                { assertFalse(positiveMoney.isZero()) },
                { assertFalse(positiveMoney.isNegative()) },
                { assertFalse(positiveMoney.isEmpty()) },

                // 0 금액
                { assertFalse(zeroMoney.isPositive()) },
                { assertTrue(zeroMoney.isZero()) },
                { assertFalse(zeroMoney.isNegative()) },
                { assertTrue(zeroMoney.isEmpty()) },

                // 음수 금액
                { assertFalse(negativeMoney.isPositive()) },
                { assertFalse(negativeMoney.isZero()) },
                { assertTrue(negativeMoney.isNegative()) },
                { assertFalse(negativeMoney.isEmpty()) }
            )
        }

        @Test
        @DisplayName("Quantity 값 객체의 정수 연산 확인")
        fun `Quantity value object integer operations`() {
            // Given
            val positiveQuantity = Quantity(10)
            val zeroQuantity = Quantity(0)
            val negativeQuantity = Quantity(-5)

            // When & Then
            assertAll(
                // 양수 수량
                { assertTrue(positiveQuantity.isPositive()) },
                { assertFalse(positiveQuantity.isZero()) },
                { assertFalse(positiveQuantity.isNegative()) },
                { assertTrue(positiveQuantity.isValid()) },
                { assertFalse(positiveQuantity.isEmpty()) },

                // 0 수량
                { assertFalse(zeroQuantity.isPositive()) },
                { assertTrue(zeroQuantity.isZero()) },
                { assertFalse(zeroQuantity.isNegative()) },
                { assertTrue(zeroQuantity.isValid()) },
                { assertTrue(zeroQuantity.isEmpty()) },

                // 음수 수량 (유효하지 않음)
                { assertFalse(negativeQuantity.isPositive()) },
                { assertFalse(negativeQuantity.isZero()) },
                { assertTrue(negativeQuantity.isNegative()) },
                { assertFalse(negativeQuantity.isValid()) },
                { assertFalse(negativeQuantity.isEmpty()) }
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["US", "DOLLAR", "", " "])
        @DisplayName("잘못된 통화 코드를 가진 Money 객체는 유효하지 않음")
        fun `Money with invalid currency code is invalid`(invalidCurrency: String) {
            // Given
            val money = Money(BigDecimal("100"), invalidCurrency)

            // When & Then
            assertFalse(money.isValid())
        }

        @Test
        @DisplayName("유효한 통화 코드를 가진 Money 객체는 유효함")
        fun `Money with valid currency code is valid`() {
            // Given
            val validCurrencies = listOf("USD", "EUR", "KRW", "JPY", "GBP")

            // When & Then
            validCurrencies.forEach { currency ->
                val money = Money(BigDecimal("100"), currency)
                assertTrue(money.isValid(), "Currency $currency should be valid")
            }
        }
    }

    @Nested
    @DisplayName("값 객체의 불변성 및 동등성 테스트")
    inner class ImmutabilityAndEqualityTest {

        @Test
        @DisplayName("동일한 값을 가진 값 객체는 동등하다")
        fun `value objects with same values are equal`() {
            // Given
            val email1 = Email("test@example.com")
            val email2 = Email("test@example.com")
            val money1 = Money(BigDecimal("100"), "USD")
            val money2 = Money(BigDecimal("100"), "USD")

            // When & Then
            assertAll(
                { assertEquals(email1, email2) },
                { assertEquals(money1, money2) },
                { assertEquals(email1.hashCode(), email2.hashCode()) },
                { assertEquals(money1.hashCode(), money2.hashCode()) }
            )
        }

        @Test
        @DisplayName("다른 값을 가진 값 객체는 동등하지 않다")
        fun `value objects with different values are not equal`() {
            // Given
            val email1 = Email("test1@example.com")
            val email2 = Email("test2@example.com")
            val money1 = Money(BigDecimal("100"), "USD")
            val money2 = Money(BigDecimal("200"), "USD")
            val money3 = Money(BigDecimal("100"), "EUR")

            // When & Then
            assertAll(
                { assertNotEquals(email1, email2) },
                { assertNotEquals(money1, money2) },
                { assertNotEquals(money1, money3) }
            )
        }

        @Test
        @DisplayName("값 객체의 toString() 메서드 동작 확인")
        fun `value object toString method works correctly`() {
            // Given
            val email = Email("test@example.com")
            val money = Money(BigDecimal("100.50"), "USD")

            // When
            val emailString = email.toString()
            val moneyString = money.toString()

            // Then
            assertAll(
                { assertTrue(emailString.contains("test@example.com")) },
                { assertTrue(moneyString.contains("100.50")) },
                { assertTrue(moneyString.contains("USD")) }
            )
        }
    }

    @Nested
    @DisplayName("복잡한 시나리오 및 경계값 테스트")
    inner class ComplexScenariosTest {

        @Test
        @DisplayName("다양한 이메일 형식 검증")
        fun `various email format validation`() {
            // Given
            val validEmails = listOf(
                "simple@example.com",
                "user+tag@example.co.kr",
                "user.name@example-domain.com",
                "123@number-domain.org"
            )

            val invalidEmails = listOf(
                "plainaddress",
                "@missingstart.com",
                "missing@.com",
                "missing@domain",
                "spaces @domain.com",
                "double@@domain.com"
            )

            // When & Then
            validEmails.forEach { emailStr ->
                assertDoesNotThrow {
                    val email = Email(emailStr)
                    assertTrue(email.isValid(), "Email '$emailStr' should be valid")
                }
            }

            invalidEmails.forEach { emailStr ->
                assertThrows<IllegalArgumentException> {
                    Email(emailStr)
                }
            }
        }

        @Test
        @DisplayName("극한값을 가진 Money 객체 테스트")
        fun `extreme value Money object test`() {
            // Given
            val maxMoney = Money(BigDecimal("999999999999.99"), "USD")
            val minMoney = Money(BigDecimal("-999999999999.99"), "USD")
            val precisionMoney = Money(BigDecimal("0.01"), "USD")
            val verySmallMoney = Money(BigDecimal("0.0001"), "USD")

            // When & Then
            assertAll(
                { assertTrue(maxMoney.isPositive()) },
                { assertTrue(minMoney.isNegative()) },
                { assertTrue(precisionMoney.isPositive()) },
                { assertTrue(verySmallMoney.isPositive()) },
                { assertTrue(maxMoney.isValid()) },
                { assertTrue(minMoney.isValid()) },
                { assertTrue(precisionMoney.isValid()) },
                { assertTrue(verySmallMoney.isValid()) }
            )
        }

        @Test
        @DisplayName("다양한 타입의 NumericValueObject 조합 테스트")
        fun `different types of NumericValueObject combination test`() {
            // Given
            val money = Money(BigDecimal("100"), "USD")
            val quantity = Quantity(5)
            val zeroMoney = Money(BigDecimal.ZERO, "USD")
            val zeroQuantity = Quantity(0)

            // When & Then
            assertAll(
                // 둘 다 양수
                { assertTrue(money.isPositive() && quantity.isPositive()) },

                // 둘 다 0
                { assertTrue(zeroMoney.isZero() && zeroQuantity.isZero()) },

                // 둘 다 비어있음
                { assertTrue(zeroMoney.isEmpty() && zeroQuantity.isEmpty()) },

                // 유효성 검증
                { assertTrue(money.isValid() && quantity.isValid()) },
                { assertTrue(zeroMoney.isValid() && zeroQuantity.isValid()) }
            )
        }
    }

    @Nested
    @DisplayName("성능 및 메모리 효율성 테스트")
    inner class PerformanceTest {

        @Test
        @DisplayName("대량의 값 객체 생성 및 비교 성능")
        fun `performance test with large number of value objects`() {
            // Given & When & Then
            assertDoesNotThrow {
                val emails = (1..1000).map { index ->
                    Email("user$index@example.com")
                }

                val moneys = (1..1000).map { index ->
                    Money(BigDecimal(index), "USD")
                }

                // 각 객체의 기본 연산 수행
                emails.forEach { email ->
                    email.isValid()
                    email.isEmpty()
                    email.toString()
                }

                moneys.forEach { money ->
                    money.isValid()
                    money.isPositive()
                    money.isZero()
                    money.toString()
                }
            }
        }

        @Test
        @DisplayName("값 객체의 해시코드 분산성 테스트")
        fun `value object hashCode distribution test`() {
            // Given
            val emails = (1..100).map { index ->
                Email("user$index@example.com")
            }

            // When
            val hashCodes = emails.map { it.hashCode() }.toSet()

            // Then
            // 해시코드가 충분히 분산되어야 함 (최소 90% 이상 고유)
            assertTrue(hashCodes.size >= emails.size * 0.9)
        }
    }
}
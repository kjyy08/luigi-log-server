package cloud.luigi99.blog.common.exception

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import kotlin.test.*

@DisplayName("Exception 기본 기능 테스트")
class SimpleExceptionTest {

    @Nested
    @DisplayName("ErrorCode 테스트")
    inner class ErrorCodeTest {

        @Test
        @DisplayName("ErrorCode 기본 기능 확인")
        fun `ErrorCode basic functionality`() {
            // Given
            val errorCode = ErrorCode.DOMAIN_RULE_VIOLATION

            // When & Then
            assertAll(
                { assertNotNull(errorCode.code) },
                { assertNotNull(errorCode.defaultMessage) },
                { assertNotNull(errorCode.category) },
                { assertTrue(errorCode.code.isNotBlank()) },
                { assertTrue(errorCode.defaultMessage.isNotBlank()) }
            )
        }

        @Test
        @DisplayName("모든 ErrorCode가 고유한 코드를 가진다")
        fun `all ErrorCodes have unique codes`() {
            // Given
            val allErrorCodes = ErrorCode.values()

            // When
            val uniqueCodes = allErrorCodes.map { it.code }.toSet()

            // Then
            assertEquals(allErrorCodes.size, uniqueCodes.size)
        }
    }

    @Nested
    @DisplayName("BusinessException 테스트")
    inner class BusinessExceptionTest {

        @Test
        @DisplayName("ErrorCode 기반 BusinessException 생성")
        fun `create BusinessException with ErrorCode`() {
            // Given
            val errorCode = ErrorCode.DOMAIN_RULE_VIOLATION
            val messageKey = "test.message.key"

            // When
            val exception = BusinessException(
                errorCode = errorCode,
                messageKey = messageKey
            )

            // Then
            assertAll(
                { assertEquals(errorCode, exception.errorCode) },
                { assertEquals(messageKey, exception.messageKey) },
                { assertEquals(LogLevel.ERROR, exception.logLevel) },
                { assertTrue(exception.context.isEmpty()) }
            )
        }

        @Test
        @DisplayName("컨텍스트 정보 추가")
        fun `add context information`() {
            // Given
            val exception = BusinessException(ErrorCode.INVALID_INPUT)

            // When
            exception.addContext("key1", "value1")
            exception.addContext("key2", 123)

            // Then
            assertAll(
                { assertEquals("value1", exception.context["key1"]) },
                { assertEquals(123, exception.context["key2"]) },
                { assertEquals(2, exception.context.size) }
            )
        }

        @Test
        @DisplayName("민감한 정보 마스킹")
        fun `sensitive information masking`() {
            // Given
            val exception = BusinessException(ErrorCode.UNAUTHORIZED)

            // When
            exception.addSensitiveContext("password", "mySecret", SensitiveFieldType.PASSWORD)
            exception.addSensitiveContext("email", "test@example.com", SensitiveFieldType.EMAIL)

            // Then
            assertAll(
                { assertEquals("***MASKED***", exception.context["password"]) },
                { assertEquals("te***@example.com", exception.context["email"]) }
            )
        }
    }

    @Nested
    @DisplayName("DomainException 테스트")
    inner class DomainExceptionTest {

        @Test
        @DisplayName("기본 DomainException 생성")
        fun `create basic DomainException`() {
            // Given
            val errorCode = ErrorCode.DOMAIN_RULE_VIOLATION
            val domainType = "User"
            val aggregateId = "user-123"

            // When
            val exception = DomainException(
                errorCode = errorCode,
                domainType = domainType,
                aggregateId = aggregateId
            )

            // Then
            assertAll(
                { assertEquals(errorCode, exception.errorCode) },
                { assertEquals(domainType, exception.domainType) },
                { assertEquals(aggregateId, exception.aggregateId) }
            )
        }

        @Test
        @DisplayName("팩토리 메서드 - entityNotFound")
        fun `factory method entityNotFound`() {
            // When
            val exception = DomainException.entityNotFound("User", "user-123")

            // Then
            assertAll(
                { assertEquals(ErrorCode.ENTITY_NOT_FOUND, exception.errorCode) },
                { assertEquals("User", exception.domainType) },
                { assertEquals("user-123", exception.aggregateId) }
            )
        }

        @Test
        @DisplayName("팩토리 메서드 - ruleViolation")
        fun `factory method ruleViolation`() {
            // When
            val exception = DomainException.ruleViolation("EmailMustBeUnique", "User")

            // Then
            assertAll(
                { assertEquals(ErrorCode.DOMAIN_RULE_VIOLATION, exception.errorCode) },
                { assertEquals("EmailMustBeUnique", exception.domainRule) },
                { assertEquals("User", exception.domainType) }
            )
        }
    }
}
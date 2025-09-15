package cloud.luigi99.blog.common.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.*

@DisplayName("BaseEntity 인터페이스 테스트")
class BaseEntityTest {

    /**
     * 테스트용 BaseEntity 구현체
     */
    private data class TestEntity(
        override val id: UUID,
        override val createdAt: LocalDateTime,
        override val updatedAt: LocalDateTime? = null
    ) : BaseEntity

    @Nested
    @DisplayName("BaseEntity 기본 계약 테스트")
    inner class BaseEntityContractTest {

        @Test
        @DisplayName("새로운 엔티티는 updatedAt이 null이어야 한다")
        fun `new entity should have null updatedAt`() {
            // Given
            val testId = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val entity = TestEntity(testId, createdAt, null)

            // When & Then
            assertAll(
                { assertEquals(testId, entity.id) },
                { assertEquals(createdAt, entity.createdAt) },
                { assertNull(entity.updatedAt) },
                { assertTrue(entity.isNew()) }
            )
        }

        @Test
        @DisplayName("수정된 엔티티는 updatedAt이 있어야 한다")
        fun `updated entity should have updatedAt`() {
            // Given
            val testId = UUID.randomUUID()
            val createdAt = LocalDateTime.now().minusHours(1)
            val updatedAt = LocalDateTime.now()
            val entity = TestEntity(testId, createdAt, updatedAt)

            // When & Then
            assertAll(
                { assertEquals(testId, entity.id) },
                { assertEquals(createdAt, entity.createdAt) },
                { assertEquals(updatedAt, entity.updatedAt) },
                { assertFalse(entity.isNew()) }
            )
        }
    }

    @Nested
    @DisplayName("엔티티 생명주기 메서드 테스트")
    inner class LifecycleMethodsTest {

        @Test
        @DisplayName("isNew()는 updatedAt이 null일 때 true를 반환한다")
        fun `isNew returns true when updatedAt is null`() {
            // Given
            val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now())

            // When & Then
            assertTrue(entity.isNew())
        }

        @Test
        @DisplayName("isNew()는 updatedAt이 있을 때 false를 반환한다")
        fun `isNew returns false when updatedAt exists`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
            )

            // When & Then
            assertFalse(entity.isNew())
        }

        @ParameterizedTest
        @ValueSource(longs = [1, 5, 10, 30, 60])
        @DisplayName("age() 메서드는 다양한 시간 단위로 경과 시간을 계산한다")
        fun `age calculates elapsed time in various units`(minutesAgo: Long) {
            // Given
            val createdAt = LocalDateTime.now().minusMinutes(minutesAgo)
            val entity = TestEntity(UUID.randomUUID(), createdAt)

            // When
            val ageInMinutes = entity.age(ChronoUnit.MINUTES)
            val ageInSeconds = entity.age(ChronoUnit.SECONDS)

            // Then
            assertTrue(ageInMinutes >= minutesAgo - 1) // 테스트 실행 시간 고려
            assertTrue(ageInSeconds >= (minutesAgo - 1) * 60)
        }

        @ParameterizedTest
        @EnumSource(ChronoUnit::class, names = ["MINUTES", "HOURS", "DAYS"])
        @DisplayName("age() 메서드는 다양한 ChronoUnit을 지원한다")
        fun `age supports different ChronoUnits`(unit: ChronoUnit) {
            // Given
            val createdAt = LocalDateTime.now().minus(1, unit)
            val entity = TestEntity(UUID.randomUUID(), createdAt)

            // When
            val age = entity.age(unit)

            // Then
            assertTrue(age >= 0)
        }

        @Test
        @DisplayName("timeSinceLastUpdate()는 updatedAt이 null일 때 null을 반환한다")
        fun `timeSinceLastUpdate returns null when updatedAt is null`() {
            // Given
            val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now())

            // When
            val timeSinceUpdate = entity.timeSinceLastUpdate()

            // Then
            assertNull(timeSinceUpdate)
        }

        @Test
        @DisplayName("timeSinceLastUpdate()는 updatedAt이 있을 때 경과 시간을 계산한다")
        fun `timeSinceLastUpdate calculates time since last update`() {
            // Given
            val updatedAt = LocalDateTime.now().minusMinutes(30)
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusHours(1),
                updatedAt
            )

            // When
            val timeSinceUpdate = entity.timeSinceLastUpdate(ChronoUnit.MINUTES)

            // Then
            assertNotNull(timeSinceUpdate)
            assertTrue(timeSinceUpdate!! >= 29) // 테스트 실행 시간 고려
        }

        @ParameterizedTest
        @ValueSource(longs = [1, 5, 10, 15, 30])
        @DisplayName("isRecentlyCreated()는 임계값에 따라 올바르게 판단한다")
        fun `isRecentlyCreated works correctly with different thresholds`(thresholdMinutes: Long) {
            // Given
            val recentEntity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusMinutes(thresholdMinutes - 1)
            )
            val oldEntity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusMinutes(thresholdMinutes + 1)
            )

            // When & Then
            assertTrue(recentEntity.isRecentlyCreated(thresholdMinutes))
            assertFalse(oldEntity.isRecentlyCreated(thresholdMinutes))
        }

        @Test
        @DisplayName("isRecentlyUpdated()는 updatedAt이 null일 때 false를 반환한다")
        fun `isRecentlyUpdated returns false when updatedAt is null`() {
            // Given
            val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now())

            // When & Then
            assertFalse(entity.isRecentlyUpdated(10))
        }

        @Test
        @DisplayName("isRecentlyUpdated()는 최근 업데이트된 엔티티에 대해 true를 반환한다")
        fun `isRecentlyUpdated returns true for recently updated entities`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(5)
            )

            // When & Then
            assertTrue(entity.isRecentlyUpdated(10))
            assertFalse(entity.isRecentlyUpdated(3))
        }
    }

    @Nested
    @DisplayName("BaseEntity 확장 함수 테스트")
    inner class ExtensionFunctionsTest {

        @Test
        @DisplayName("lifecycleStatus()는 새로운 엔티티에 대해 NEW를 반환한다")
        fun `lifecycleStatus returns NEW for new entity`() {
            // Given
            val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now())

            // When
            val status = entity.lifecycleStatus()

            // Then
            assertEquals("NEW", status)
        }

        @Test
        @DisplayName("lifecycleStatus()는 최근 업데이트된 엔티티에 대해 RECENTLY_UPDATED를 반환한다")
        fun `lifecycleStatus returns RECENTLY_UPDATED for recently updated entity`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(5)
            )

            // When
            val status = entity.lifecycleStatus()

            // Then
            assertEquals("RECENTLY_UPDATED", status)
        }

        @Test
        @DisplayName("lifecycleStatus()는 최근 생성된 엔티티에 대해 RECENTLY_CREATED를 반환한다")
        fun `lifecycleStatus returns RECENTLY_CREATED for recently created entity`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().minusMinutes(15) // 오래된 업데이트
            )

            // When
            val status = entity.lifecycleStatus()

            // Then
            assertEquals("RECENTLY_CREATED", status)
        }

        @Test
        @DisplayName("lifecycleStatus()는 안정적인 엔티티에 대해 STABLE을 반환한다")
        fun `lifecycleStatus returns STABLE for stable entity`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(15)
            )

            // When
            val status = entity.lifecycleStatus()

            // Then
            assertEquals("STABLE", status)
        }

        @Test
        @DisplayName("toSummary()는 엔티티 정보를 올바른 형식으로 반환한다")
        fun `toSummary returns entity information in correct format`() {
            // Given
            val testId = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val updatedAt = LocalDateTime.now()
            val entity = TestEntity(testId, createdAt, updatedAt)

            // When
            val summary = entity.toSummary()

            // Then
            assertAll(
                { assertTrue(summary.contains("Entity[")) },
                { assertTrue(summary.contains("id=$testId")) },
                { assertTrue(summary.contains("created=$createdAt")) },
                { assertTrue(summary.contains("updated=$updatedAt")) },
                { assertTrue(summary.contains("status=")) }
            )
        }

        @Test
        @DisplayName("isSameEntityAs는 같은 ID를 가진 엔티티에 대해 true를 반환한다")
        fun `isSameEntityAs returns true for entities with same ID`() {
            // Given
            val testId = UUID.randomUUID()
            val entity1 = TestEntity(testId, LocalDateTime.now())
            val entity2 = TestEntity(testId, LocalDateTime.now().minusHours(1))

            // When & Then
            assertTrue(entity1 isSameEntityAs entity2)
            assertTrue(entity2 isSameEntityAs entity1)
        }

        @Test
        @DisplayName("isSameEntityAs는 다른 ID를 가진 엔티티에 대해 false를 반환한다")
        fun `isSameEntityAs returns false for entities with different IDs`() {
            // Given
            val entity1 = TestEntity(UUID.randomUUID(), LocalDateTime.now())
            val entity2 = TestEntity(UUID.randomUUID(), LocalDateTime.now())

            // When & Then
            assertFalse(entity1 isSameEntityAs entity2)
        }

        @Test
        @DisplayName("wasCreatedBefore는 더 이른 생성 시간을 가진 엔티티에 대해 true를 반환한다")
        fun `wasCreatedBefore returns true for entity created earlier`() {
            // Given
            val earlierTime = LocalDateTime.now().minusHours(2)
            val laterTime = LocalDateTime.now().minusHours(1)
            val earlierEntity = TestEntity(UUID.randomUUID(), earlierTime)
            val laterEntity = TestEntity(UUID.randomUUID(), laterTime)

            // When & Then
            assertTrue(earlierEntity wasCreatedBefore laterEntity)
            assertFalse(laterEntity wasCreatedBefore earlierEntity)
        }

        @Test
        @DisplayName("wasCreatedAfter는 더 늦은 생성 시간을 가진 엔티티에 대해 true를 반환한다")
        fun `wasCreatedAfter returns true for entity created later`() {
            // Given
            val earlierTime = LocalDateTime.now().minusHours(2)
            val laterTime = LocalDateTime.now().minusHours(1)
            val earlierEntity = TestEntity(UUID.randomUUID(), earlierTime)
            val laterEntity = TestEntity(UUID.randomUUID(), laterTime)

            // When & Then
            assertTrue(laterEntity wasCreatedAfter earlierEntity)
            assertFalse(earlierEntity wasCreatedAfter laterEntity)
        }
    }

    @Nested
    @DisplayName("경계값 및 예외 상황 테스트")
    inner class EdgeCaseTest {

        @Test
        @DisplayName("동일한 시간에 생성된 엔티티들의 비교")
        fun `entities created at same time comparison`() {
            // Given
            val sameTime = LocalDateTime.now()
            val entity1 = TestEntity(UUID.randomUUID(), sameTime)
            val entity2 = TestEntity(UUID.randomUUID(), sameTime)

            // When & Then
            assertFalse(entity1 wasCreatedBefore entity2)
            assertFalse(entity1 wasCreatedAfter entity2)
            assertFalse(entity1 isSameEntityAs entity2) // 다른 ID
        }

        @Test
        @DisplayName("0 임계값으로 최근 생성/수정 확인")
        fun `recently created and updated with zero threshold`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )

            // When & Then
            assertTrue(entity.isRecentlyCreated(0))
            assertTrue(entity.isRecentlyUpdated(0))
        }

        @Test
        @DisplayName("매우 큰 임계값으로 최근 생성/수정 확인")
        fun `recently created and updated with large threshold`() {
            // Given
            val entity = TestEntity(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(15)
            )

            // When & Then
            assertTrue(entity.isRecentlyCreated(Long.MAX_VALUE))
            assertTrue(entity.isRecentlyUpdated(Long.MAX_VALUE))
        }

        @Test
        @DisplayName("미래 시간으로 생성된 엔티티 처리")
        fun `entity created in future time`() {
            // Given
            val futureTime = LocalDateTime.now().plusHours(1)
            val entity = TestEntity(UUID.randomUUID(), futureTime)

            // When
            val age = entity.age(ChronoUnit.MINUTES)

            // Then
            // 미래 시간의 경우 음수 값이 반환될 수 있음
            assertTrue(age <= 0)
            assertTrue(entity.isRecentlyCreated(Long.MAX_VALUE))
        }
    }

    @Nested
    @DisplayName("성능 및 대용량 데이터 테스트")
    inner class PerformanceTest {

        @Test
        @DisplayName("대량의 엔티티 생성 및 비교 성능")
        fun `performance test with large number of entities`() {
            // Given
            val entities = (1..1000).map {
                TestEntity(
                    UUID.randomUUID(),
                    LocalDateTime.now().minusMinutes(it.toLong())
                )
            }

            // When & Then - 성능 테스트는 예외 없이 완료되어야 함
            assertDoesNotThrow {
                entities.forEach { entity ->
                    entity.isNew()
                    entity.age()
                    entity.isRecentlyCreated()
                    entity.lifecycleStatus()
                    entity.toSummary()
                }
            }
        }

        @Test
        @DisplayName("엔티티 비교 연산 성능")
        fun `performance test for entity comparison operations`() {
            // Given
            val baseEntity = TestEntity(UUID.randomUUID(), LocalDateTime.now())
            val entities = (1..100).map {
                TestEntity(UUID.randomUUID(), LocalDateTime.now().minusMinutes(it.toLong()))
            }

            // When & Then
            assertDoesNotThrow {
                entities.forEach { entity ->
                    baseEntity isSameEntityAs entity
                    baseEntity wasCreatedBefore entity
                    baseEntity wasCreatedAfter entity
                }
            }
        }
    }
}
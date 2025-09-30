package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.fixtures.TestJpaEntity
import cloud.luigi99.blog.common.fixtures.TestEntityId
import cloud.luigi99.blog.common.fixtures.TestJpaRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.*

/**
 * JpaBaseRepository의 행위를 검증하는 테스트
 *
 * MockK를 사용하여 JPA 레포지토리의 기본 기능을 테스트합니다.
 * 실제 데이터베이스 연동 없이 계약(Contract) 검증에 집중합니다.
 */
class JpaBaseRepositoryTest : BehaviorSpec({

    given("JpaBaseRepository MockK 테스트") {

        // 각 테스트마다 새로운 mock 인스턴스 생성
        beforeEach {
            clearAllMocks()
        }

        `when`("삭제된 엔티티 조회 테스트") {
            then("findAllDeleted가 삭제된 엔티티만 반환해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val deletedEntities = listOf(TestJpaEntity(), TestJpaEntity())

                every { mockRepository.findAllDeleted() } returns deletedEntities

                val result = mockRepository.findAllDeleted()

                result shouldHaveSize 2
                result shouldBe deletedEntities
                verify(exactly = 1) { mockRepository.findAllDeleted() }
            }

            then("findAllIncludingDeleted가 모든 엔티티를 반환해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val allEntities = listOf(TestJpaEntity(), TestJpaEntity(), TestJpaEntity())

                every { mockRepository.findAllIncludingDeleted() } returns allEntities

                val result = mockRepository.findAllIncludingDeleted()

                result shouldHaveSize 3
                result shouldBe allEntities
                verify(exactly = 1) { mockRepository.findAllIncludingDeleted() }
            }
        }

        `when`("엔티티 복원 테스트") {
            then("restoreById가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testId = UUID.randomUUID()

                every { mockRepository.restoreById(testId) } returns 1

                val result = mockRepository.restoreById(testId)

                result shouldBe 1
                verify(exactly = 1) { mockRepository.restoreById(testId) }
            }

            then("존재하지 않는 ID로 복원 시도 시 0을 반환해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val nonExistentId = UUID.randomUUID()

                every { mockRepository.restoreById(nonExistentId) } returns 0

                val result = mockRepository.restoreById(nonExistentId)

                result shouldBe 0
                verify(exactly = 1) { mockRepository.restoreById(nonExistentId) }
            }

            then("이미 활성 상태인 엔티티 복원 시도 시 0을 반환해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val activeEntityId = UUID.randomUUID()

                // 이미 deleted=false인 엔티티는 UPDATE 쿼리가 영향을 주지 않음
                every { mockRepository.restoreById(activeEntityId) } returns 0

                val result = mockRepository.restoreById(activeEntityId)

                result shouldBe 0
                verify(exactly = 1) { mockRepository.restoreById(activeEntityId) }
            }
        }

        `when`("@SoftDelete 어노테이션 동작 검증") {
            then("Hibernate 6.x @SoftDelete가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val activeEntities = listOf(TestJpaEntity(), TestJpaEntity())

                // @SoftDelete 어노테이션으로 deleted=false 조건이 자동 적용
                every { mockRepository.findAll() } returns activeEntities
                every { mockRepository.count() } returns 2L

                val allActive = mockRepository.findAll()
                val activeCount = mockRepository.count()

                allActive shouldHaveSize 2
                activeCount shouldBe 2L

                verify(exactly = 1) { mockRepository.findAll() }
                verify(exactly = 1) { mockRepository.count() }
            }
        }

        `when`("낙관적 락 기능 테스트") {
            then("findByIdWithLock이 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testId = UUID.randomUUID()
                val testEntity = TestJpaEntity()

                every { mockRepository.findByIdWithLock(testId) } returns Optional.of(testEntity)

                val result = mockRepository.findByIdWithLock(testId)

                result.isPresent shouldBe true
                result.get() shouldBe testEntity
                verify(exactly = 1) { mockRepository.findByIdWithLock(testId) }
            }

            then("findByIdAndVersion이 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testId = UUID.randomUUID()
                val version = 1L
                val testEntity = TestJpaEntity()

                every { mockRepository.findByIdAndVersion(testId, version) } returns Optional.of(testEntity)

                val result = mockRepository.findByIdAndVersion(testId, version)

                result.isPresent shouldBe true
                result.get() shouldBe testEntity
                verify(exactly = 1) { mockRepository.findByIdAndVersion(testId, version) }
            }
        }

        `when`("감사 정보 기반 조회 테스트") {
            then("findAllCreatedAfter가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val baseDate = LocalDateTime.now().minusDays(1)
                val recentEntities = listOf(TestJpaEntity(), TestJpaEntity())

                every { mockRepository.findAllCreatedAfter(baseDate) } returns recentEntities

                val result = mockRepository.findAllCreatedAfter(baseDate)

                result shouldHaveSize 2
                result shouldBe recentEntities
                verify(exactly = 1) { mockRepository.findAllCreatedAfter(baseDate) }
            }

            then("findAllUpdatedAfter가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val baseDate = LocalDateTime.now().minusHours(2)
                val updatedEntities = listOf(TestJpaEntity())

                every { mockRepository.findAllUpdatedAfter(baseDate) } returns updatedEntities

                val result = mockRepository.findAllUpdatedAfter(baseDate)

                result shouldHaveSize 1
                result shouldBe updatedEntities
                verify(exactly = 1) { mockRepository.findAllUpdatedAfter(baseDate) }
            }
        }
    }

    given("TestJpaEntity 기본 동작 확인") {
        `when`("새로운 엔티티를 생성할 때") {
            then("기본 값들이 올바르게 설정되어야 한다") {
                val entity = TestJpaEntity()

                entity.entityId shouldNotBe null
                entity.deleted shouldBe false
                entity.deletedAt shouldBe null
                entity.isDeleted() shouldBe false
            }
        }

        `when`("onSoftDelete 콜백이 호출될 때") {
            then("삭제 상태가 올바르게 설정되어야 한다") {
                val entity = TestJpaEntity()

                entity.onSoftDelete()

                entity.deleted shouldBe true
                entity.deletedAt shouldNotBe null
                entity.isDeleted() shouldBe true
            }
        }
    }
})


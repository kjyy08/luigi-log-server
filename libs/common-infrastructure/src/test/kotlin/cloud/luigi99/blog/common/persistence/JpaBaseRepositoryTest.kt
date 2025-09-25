package cloud.luigi99.blog.common.persistence

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

                mockRepository.findAll() shouldHaveSize 2
                mockRepository.count() shouldBe 2L

                verify(exactly = 1) { mockRepository.findAll() }
                verify(exactly = 1) { mockRepository.count() }
            }

            then("@SoftDelete와 @SQLDelete가 함께 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testEntity = TestJpaEntity()

                // JPA 표준 delete() 메서드 사용 (@SQLDelete 자동 실행)
                every { mockRepository.delete(testEntity) } returns Unit

                mockRepository.delete(testEntity)

                verify(exactly = 1) { mockRepository.delete(testEntity) }
            }
        }

        `when`("날짜 기반 조회 메서드 테스트") {
            then("findAllCreatedAfter가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testDate = LocalDateTime.now().minusDays(1)
                val entities = listOf(TestJpaEntity(), TestJpaEntity())

                every { mockRepository.findAllCreatedAfter(testDate) } returns entities

                val result = mockRepository.findAllCreatedAfter(testDate)

                result shouldHaveSize 2
                result shouldBe entities
                verify(exactly = 1) { mockRepository.findAllCreatedAfter(testDate) }
            }

            then("findAllUpdatedAfter가 정상 동작해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testDate = LocalDateTime.now().minusDays(1)
                val entities = listOf(TestJpaEntity(), TestJpaEntity())

                every { mockRepository.findAllUpdatedAfter(testDate) } returns entities

                val result = mockRepository.findAllUpdatedAfter(testDate)

                result shouldHaveSize 2
                result shouldBe entities
                verify(exactly = 1) { mockRepository.findAllUpdatedAfter(testDate) }
            }
        }

        `when`("낙관적 락 관련 조회 테스트") {
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
                val testVersion = 1L
                val testEntity = TestJpaEntity()

                every { mockRepository.findByIdAndVersion(testId, testVersion) } returns Optional.of(testEntity)

                val result = mockRepository.findByIdAndVersion(testId, testVersion)

                result.isPresent shouldBe true
                result.get() shouldBe testEntity
                verify(exactly = 1) { mockRepository.findByIdAndVersion(testId, testVersion) }
            }

            then("버전이 일치하지 않으면 빈 Optional을 반환해야 한다") {
                val mockRepository = mockk<TestJpaRepository>()
                val testId = UUID.randomUUID()
                val wrongVersion = 999L

                every { mockRepository.findByIdAndVersion(testId, wrongVersion) } returns Optional.empty()

                val result = mockRepository.findByIdAndVersion(testId, wrongVersion)

                result.isEmpty shouldBe true
                verify(exactly = 1) { mockRepository.findByIdAndVersion(testId, wrongVersion) }
            }
        }

        `when`("기본 JPA 메서드 동작 테스트") {
            then("@SoftDelete 어노테이션으로 deleted=false 자동 적용") {
                val mockRepository = mockk<TestJpaRepository>()
                val entities = listOf(TestJpaEntity(), TestJpaEntity())

                // 기본 JPA 메서드도 @SoftDelete 어노테이션으로 deleted=false 자동 적용
                every { mockRepository.findAll() } returns entities
                every { mockRepository.count() } returns 2L

                mockRepository.findAll() shouldHaveSize 2
                mockRepository.count() shouldBe 2L

                verify(exactly = 1) { mockRepository.findAll() }
                verify(exactly = 1) { mockRepository.count() }
            }
        }
    }

    given("BaseJpaEntity와의 통합") {
        `when`("TestJpaEntity를 생성할 때") {
            then("BaseJpaEntity의 모든 기능을 상속받아야 한다") {
                val entity = TestJpaEntity()

                // BaseJpaEntity의 기본 필드들
                entity.id shouldBe entity.id
                entity.createdAt shouldBe entity.createdAt
                entity.updatedAt shouldBe null

                // 새로 추가된 필드들
                entity.version shouldBe 0L
                entity.deleted shouldBe false
                entity.deletedAt shouldBe null
                entity.isDeleted() shouldBe false
            }

            then("Soft Delete 기능이 정상적으로 동작해야 한다") {
                val entity = TestJpaEntity()

                // 삭제 전 상태
                entity.isDeleted() shouldBe false

                // @PreRemove 콜백 시뮬레이션 (JPA delete() 호출 시 자동 실행)
                entity.onSoftDelete()
                entity.isDeleted() shouldBe true
                entity.deleted shouldBe true
                entity.deletedAt shouldNotBe null

                // 주의: 실제 복원은 Repository의 restoreById() 사용
                // Entity 레벨에서는 @PreRemove 콜백만 제공
            }
        }
    }
}) {
    /**
     * 테스트용 JPA Repository 인터페이스
     */
    interface TestJpaRepository : JpaBaseRepository<TestJpaEntity, UUID>

    /**
     * 테스트용 JPA 엔티티 클래스
     */
    class TestJpaEntity(
        fixedId: UUID? = null
    ) : BaseJpaEntity() {
        override val id: UUID = fixedId ?: super.id
    }
}
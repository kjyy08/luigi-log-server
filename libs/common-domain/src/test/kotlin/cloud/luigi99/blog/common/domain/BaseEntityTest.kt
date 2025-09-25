package cloud.luigi99.blog.common.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime
import java.util.*

/**
 * BaseEntity의 행위를 검증하는 테스트
 */
class BaseEntityTest : BehaviorSpec({

    given("동일한 ID를 가진 두 개의 BaseEntity 구현체가 있을 때") {
        val entityId = UUID.randomUUID()
        val now = LocalDateTime.now()

        val entity1 = TestEntity(entityId, now, null)
        val entity2 = TestEntity(entityId, now.plusMinutes(10), now.plusMinutes(5))

        `when`("equals 메서드를 호출하면") {
            val result = entity1.equals(entity2)

            then("ID가 같으므로 true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = entity1.hashCode()
            val hashCode2 = entity2.hashCode()

            then("ID가 같으므로 같은 해시코드를 반환한다") {
                hashCode1 shouldBe hashCode2
            }
        }
    }

    given("서로 다른 ID를 가진 두 개의 BaseEntity 구현체가 있을 때") {
        val entityId1 = UUID.randomUUID()
        val entityId2 = UUID.randomUUID()
        val now = LocalDateTime.now()

        val entity1 = TestEntity(entityId1, now, null)
        val entity2 = TestEntity(entityId2, now, null)

        `when`("equals 메서드를 호출하면") {
            val result = entity1.equals(entity2)

            then("ID가 다르므로 false를 반환한다") {
                result shouldBe false
            }
        }

        `when`("hashCode 메서드를 호출하면") {
            val hashCode1 = entity1.hashCode()
            val hashCode2 = entity2.hashCode()

            then("ID가 다르므로 다른 해시코드를 반환한다") {
                hashCode1 shouldNotBe hashCode2
            }
        }
    }

    given("BaseEntity 구현체가 있을 때") {
        val entityId = UUID.randomUUID()
        val createdAt = LocalDateTime.now()
        val updatedAt = createdAt.plusMinutes(5)

        val entity = TestEntity(entityId, createdAt, updatedAt)

        `when`("toString 메서드를 호출하면") {
            val result = entity.toString()

            then("클래스명과 ID, 생성시각, 수정시각을 포함한 문자열을 반환한다") {
                result shouldContain "TestEntity"
                result shouldContain entityId.toString()
                result shouldContain createdAt.toString()
                result shouldContain updatedAt.toString()
            }
        }
    }

    given("BaseEntity 구현체가 자기 자신과 비교될 때") {
        val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now(), null)

        `when`("equals 메서드로 자기 자신과 비교하면") {
            val result = entity.equals(entity)

            then("true를 반환한다") {
                result shouldBe true
            }
        }
    }

    given("BaseEntity 구현체가 null과 비교될 때") {
        val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now(), null)

        `when`("equals 메서드로 null과 비교하면") {
            val result = entity.equals(null)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("BaseEntity 구현체가 다른 타입의 객체와 비교될 때") {
        val entity = TestEntity(UUID.randomUUID(), LocalDateTime.now(), null)
        val otherObject = "문자열 객체"

        `when`("equals 메서드로 다른 타입 객체와 비교하면") {
            val result = entity.equals(otherObject)

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }
})

/**
 * 테스트용 BaseEntity 구현체
 */
private class TestEntity(
    override val id: UUID,
    override val createdAt: LocalDateTime,
    override var updatedAt: LocalDateTime?
) : BaseEntity()
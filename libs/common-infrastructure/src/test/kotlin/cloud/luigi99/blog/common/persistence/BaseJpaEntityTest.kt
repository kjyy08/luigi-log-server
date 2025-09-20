package cloud.luigi99.blog.common.persistence

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime
import java.util.*

class BaseJpaEntityTest : BehaviorSpec({

    given("BaseJpaEntity 구현체") {
        `when`("엔티티를 생성할 때") {
            then("ID가 자동으로 생성되어야 한다") {
                val entity1 = TestJpaEntity()
                val entity2 = TestJpaEntity()

                entity1.id.shouldNotBeNull()
                entity2.id.shouldNotBeNull()
                entity1.id shouldNotBe entity2.id
            }

            then("createdAt이 자동으로 설정되어야 한다") {
                val beforeCreation = LocalDateTime.now().minusSeconds(1)
                val entity = TestJpaEntity()
                val afterCreation = LocalDateTime.now().plusSeconds(1)

                entity.createdAt.shouldNotBeNull()
                entity.createdAt.isAfter(beforeCreation) shouldBe true
                entity.createdAt.isBefore(afterCreation) shouldBe true
            }

            then("updatedAt은 null이어야 한다") {
                val entity = TestJpaEntity()
                entity.updatedAt shouldBe null
            }
        }
    }

    given("동일한 ID를 가진 두 엔티티") {
        `when`("동등성을 비교할 때") {
            then("같다고 판단되어야 한다") {
                val id = UUID.randomUUID()
                val entity1 = TestJpaEntity(id)
                val entity2 = TestJpaEntity(id)

                entity1 shouldBe entity2
                entity1.hashCode() shouldBe entity2.hashCode()
            }
        }
    }

    given("다른 ID를 가진 두 엔티티") {
        `when`("동등성을 비교할 때") {
            then("다르다고 판단되어야 한다") {
                val entity1 = TestJpaEntity()
                val entity2 = TestJpaEntity()

                entity1 shouldNotBe entity2
                entity1.hashCode() shouldNotBe entity2.hashCode()
            }
        }
    }

    given("엔티티와 다른 객체") {
        `when`("null과 비교할 때") {
            then("다르다고 판단되어야 한다") {
                val entity = TestJpaEntity()
                entity shouldNotBe null
            }
        }

        `when`("다른 타입 객체와 비교할 때") {
            then("다르다고 판단되어야 한다") {
                val entity = TestJpaEntity()
                val otherObject = "string"
                entity shouldNotBe otherObject
            }
        }

        `when`("자기 자신과 비교할 때") {
            then("같다고 판단되어야 한다") {
                val entity = TestJpaEntity()
                entity shouldBe entity
            }
        }
    }

    given("엔티티의 toString 메서드") {
        `when`("toString을 호출할 때") {
            then("올바른 형식을 반환해야 한다") {
                val entity = TestJpaEntity()
                val stringRepresentation = entity.toString()

                stringRepresentation shouldContain "TestJpaEntity"
                stringRepresentation shouldContain "id="
                stringRepresentation shouldContain "createdAt="
                stringRepresentation shouldContain "updatedAt="
            }
        }
    }

    given("고정된 ID를 가진 엔티티") {
        `when`("생성자에서 ID를 지정할 때") {
            then("지정된 ID가 사용되어야 한다") {
                val fixedId = UUID.randomUUID()
                val entity = TestJpaEntity(fixedId)

                entity.id shouldBe fixedId
            }
        }
    }

    given("여러 엔티티의 ID 유일성") {
        `when`("대량의 엔티티를 생성할 때") {
            then("모든 ID가 고유해야 한다") {
                val entities = (1..100).map { TestJpaEntity() }
                val ids = entities.map { it.id }.toSet()

                ids.size shouldBe 100 // 모든 ID가 고유하므로 Set 크기는 100이어야 함
            }
        }
    }

    given("엔티티 생성 시점") {
        `when`("여러 엔티티를 순차적으로 생성할 때") {
            then("생성 시간이 순서대로 증가해야 한다") {
                val entity1 = TestJpaEntity()
                Thread.sleep(1) // 시간 차이를 만들기 위해
                val entity2 = TestJpaEntity()

                entity2.createdAt.isAfter(entity1.createdAt) shouldBe true
            }
        }
    }
}) {
    /**
     * 테스트용 JPA 엔티티 클래스
     */
    class TestJpaEntity(
        fixedId: UUID? = null
    ) : BaseJpaEntity() {
        override val id: UUID = fixedId ?: super.id
    }
}
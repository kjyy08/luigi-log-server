package cloud.luigi99.blog.common.persistence

import cloud.luigi99.blog.common.fixtures.TestJpaEntity
import cloud.luigi99.blog.common.fixtures.TestEntityId
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

                entity1.entityId.shouldNotBeNull()
                entity2.entityId.shouldNotBeNull()
                entity1.entityId shouldNotBe entity2.entityId
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

            then("version이 0으로 초기화되어야 한다") {
                val entity = TestJpaEntity()
                entity.version shouldBe 0L
            }

            then("deleted가 false로 초기화되어야 한다") {
                val entity = TestJpaEntity()
                entity.deleted shouldBe false
            }

            then("deletedAt이 null로 초기화되어야 한다") {
                val entity = TestJpaEntity()
                entity.deletedAt shouldBe null
            }

            then("isDeleted()가 false를 반환해야 한다") {
                val entity = TestJpaEntity()
                entity.isDeleted() shouldBe false
            }
        }
    }

    given("동일한 ID를 가진 두 엔티티") {
        `when`("동등성을 비교할 때") {
            then("같다고 판단되어야 한다") {
                val entityId = TestEntityId()
                val entity1 = TestJpaEntity(entityId)
                val entity2 = TestJpaEntity(entityId)

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
                stringRepresentation shouldContain "entityId="
                stringRepresentation shouldContain "createdAt="
                stringRepresentation shouldContain "updatedAt="
            }
        }
    }

    given("고정된 ID를 가진 엔티티") {
        `when`("생성자에서 ID를 지정할 때") {
            then("지정된 ID가 사용되어야 한다") {
                val fixedEntityId = TestEntityId()
                val entity = TestJpaEntity(fixedEntityId)

                entity.entityId shouldBe fixedEntityId
            }
        }
    }

    given("여러 엔티티의 ID 유일성") {
        `when`("대량의 엔티티를 생성할 때") {
            then("모든 ID가 고유해야 한다") {
                val entities = (1..100).map { TestJpaEntity() }
                val ids = entities.map { it.entityId }.toSet()

                ids.size shouldBe 100 // 모든 ID가 고유하므로 Set 크기는 100이어야 함
            }
        }
    }

    given("엔티티 생성 시점") {
        `when`("여러 엔티티를 순차적으로 생성할 때") {
            then("생성 시간이 순서대로 증가해야 한다") {
                val entity1 = TestJpaEntity()
                Thread.sleep(10) // 시간 차이를 만들기 위해
                val entity2 = TestJpaEntity()

                entity2.createdAt.isAfter(entity1.createdAt) shouldBe true
            }
        }
    }

    given("@PreRemove 콜백 기능") {
        `when`("onSoftDelete()가 호출될 때") {
            then("deleted가 true로 설정되어야 한다") {
                val entity = TestJpaEntity()

                // @PreRemove 콜백 시뮬레이션
                entity.onSoftDelete()

                entity.deleted shouldBe true
                entity.deletedAt.shouldNotBeNull()
                entity.isDeleted() shouldBe true
            }

            then("deletedAt이 현재 시간으로 설정되어야 한다") {
                val beforeDeletion = LocalDateTime.now().minusSeconds(1)
                val entity = TestJpaEntity()

                // @PreRemove 콜백 시뮬레이션
                entity.onSoftDelete()
                val afterDeletion = LocalDateTime.now().plusSeconds(1)

                entity.deletedAt.shouldNotBeNull()
                entity.deletedAt!!.isAfter(beforeDeletion) shouldBe true
                entity.deletedAt!!.isBefore(afterDeletion) shouldBe true
            }
        }

    }

    given("낙관적 락 기능") {
        `when`("새로운 엔티티를 생성할 때") {
            then("version이 0으로 시작해야 한다") {
                val entity = TestJpaEntity()
                entity.version shouldBe 0L
            }
        }
    }

    given("JPA 라이프사이클 호환성") {
        `when`("@PreRemove와 @SoftDelete, @SQLDelete가 함께 사용될 때") {
            then("세 어노테이션이 정상적으로 협동해야 한다") {
                val entity = TestJpaEntity()

                // @SoftDelete: 자동 필터링 (deleted = false)
                entity.deleted shouldBe false

                // @PreRemove: JPA delete() 호출 전 자동 콜백 시뮬레이션
                val beforeDeletion = LocalDateTime.now().minusSeconds(1)
                entity.onSoftDelete()  // JPA 컨테이너가 자동 호출할 내용 시뮬레이션
                val afterDeletion = LocalDateTime.now().plusSeconds(1)

                // @PreRemove 콜백으로 논리적 삭제 상태 설정
                entity.deleted shouldBe true
                entity.deletedAt.shouldNotBeNull()
                entity.deletedAt!!.isAfter(beforeDeletion) shouldBe true
                entity.deletedAt!!.isBefore(afterDeletion) shouldBe true

                // 복원 기능은 Repository 레벨에서 처리 (restoreById)
                // Entity 레벨에서는 @PreRemove 콜백만 지원
            }
        }
    }

}) {
}
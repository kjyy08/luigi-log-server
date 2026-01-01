package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

/**
 * Metadata Value Object 테스트
 *
 * Metadata는 Post의 시간 관련 정보를 담습니다.
 */
class MetadataTest :
    BehaviorSpec({
        Given("생성 시간과 수정 시간이 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val updatedAt = createdAt.plusHours(1)

            When("Metadata 객체를 생성하면") {
                val metadata =
                    Metadata(
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        publishedAt = null,
                    )

                Then("값이 올바르게 설정된다") {
                    metadata.createdAt shouldBe createdAt
                    metadata.updatedAt shouldBe updatedAt
                    metadata.publishedAt.shouldBeNull()
                }
            }
        }

        Given("모든 시간 정보가 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val updatedAt = createdAt.plusHours(1)
            val publishedAt = createdAt.plusHours(2)

            When("Metadata 객체를 생성하면") {
                val metadata =
                    Metadata(
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        publishedAt = publishedAt,
                    )

                Then("모든 값이 올바르게 설정된다") {
                    metadata.createdAt shouldBe createdAt
                    metadata.updatedAt shouldBe updatedAt
                    metadata.publishedAt shouldBe publishedAt
                }
            }
        }

        Given("초안 상태의 Metadata가 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val metadata =
                Metadata(
                    createdAt = createdAt,
                    updatedAt = createdAt,
                    publishedAt = null,
                )

            When("publishedAt이 null인지 확인하면") {
                Then("null이어야 한다") {
                    metadata.publishedAt.shouldBeNull()
                }
            }
        }

        Given("발행된 Metadata가 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val publishedAt = createdAt.plusDays(1)
            val metadata =
                Metadata(
                    createdAt = createdAt,
                    updatedAt = createdAt,
                    publishedAt = publishedAt,
                )

            When("publishedAt을 확인하면") {
                Then("발행 시간이 설정되어 있어야 한다") {
                    metadata.publishedAt shouldNotBe null
                    metadata.publishedAt shouldBe publishedAt
                }
            }
        }

        Given("두 개의 동일한 Metadata가 주어졌을 때") {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 1, 11, 0)
            val metadata1 = Metadata(createdAt, updatedAt, null)
            val metadata2 = Metadata(createdAt, updatedAt, null)

            When("두 객체를 비교하면") {
                Then("같은 값으로 인식된다") {
                    metadata1 shouldBe metadata2
                    metadata1.hashCode() shouldBe metadata2.hashCode()
                }
            }
        }

        Given("Metadata 객체가 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val metadata = Metadata(createdAt, createdAt, null)

            When("copy()로 updatedAt을 변경하면") {
                val newUpdatedAt = createdAt.plusHours(5)
                val updated = metadata.copy(updatedAt = newUpdatedAt)

                Then("새로운 객체가 생성되고 값이 변경된다") {
                    updated.createdAt shouldBe createdAt
                    updated.updatedAt shouldBe newUpdatedAt
                    updated.publishedAt.shouldBeNull()
                }
            }
        }

        Given("Metadata 객체가 주어졌을 때") {
            val createdAt = LocalDateTime.now()
            val metadata = Metadata(createdAt, createdAt, null)

            When("copy()로 publishedAt을 설정하면") {
                val publishedAt = createdAt.plusDays(1)
                val published = metadata.copy(publishedAt = publishedAt)

                Then("발행 시간이 설정된다") {
                    published.publishedAt shouldBe publishedAt
                }
            }
        }
    })

package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * PostStatus enum 테스트
 *
 * PostStatus는 Post의 발행 상태를 나타냅니다.
 */
class PostStatusTest :
    BehaviorSpec({
        Given("DRAFT PostStatus가 주어졌을 때") {
            val status = PostStatus.DRAFT

            Then("올바른 값을 가진다") {
                status.name shouldBe "DRAFT"
            }
        }

        Given("PUBLISHED PostStatus가 주어졌을 때") {
            val status = PostStatus.PUBLISHED

            Then("올바른 값을 가진다") {
                status.name shouldBe "PUBLISHED"
            }
        }

        Given("ARCHIVED PostStatus가 주어졌을 때") {
            val status = PostStatus.ARCHIVED

            Then("올바른 값을 가진다") {
                status.name shouldBe "ARCHIVED"
            }
        }

        Given("모든 PostStatus 값이 요청되었을 때") {
            When("values()를 호출하면") {
                val allStatuses = PostStatus.entries

                Then("DRAFT, PUBLISHED, ARCHIVED가 포함된다") {
                    allStatuses.map { it.name } shouldContainExactlyInAnyOrder
                        listOf("DRAFT", "PUBLISHED", "ARCHIVED")
                }
            }
        }

        Given("DRAFT 문자열이 주어졌을 때") {
            val statusString = "DRAFT"

            When("valueOf()로 PostStatus를 생성하면") {
                val status = PostStatus.valueOf(statusString)

                Then("DRAFT PostStatus가 반환된다") {
                    status shouldBe PostStatus.DRAFT
                }
            }
        }

        Given("PUBLISHED 문자열이 주어졌을 때") {
            val statusString = "PUBLISHED"

            When("valueOf()로 PostStatus를 생성하면") {
                val status = PostStatus.valueOf(statusString)

                Then("PUBLISHED PostStatus가 반환된다") {
                    status shouldBe PostStatus.PUBLISHED
                }
            }
        }

        Given("ARCHIVED 문자열이 주어졌을 때") {
            val statusString = "ARCHIVED"

            When("valueOf()로 PostStatus를 생성하면") {
                val status = PostStatus.valueOf(statusString)

                Then("ARCHIVED PostStatus가 반환된다") {
                    status shouldBe PostStatus.ARCHIVED
                }
            }
        }
    })

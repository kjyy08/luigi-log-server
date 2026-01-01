package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import java.util.UUID

/**
 * PostId Value Object 테스트
 *
 * PostId는 Post의 고유 식별자로 UUID를 사용합니다.
 */
class PostIdTest :
    BehaviorSpec({
        Given("UUID 값이 주어졌을 때") {
            val uuid = UUID.randomUUID()

            When("PostId 객체를 생성하면") {
                val postId = PostId(uuid)

                Then("값이 올바르게 설정된다") {
                    postId.value shouldBe uuid
                }
            }
        }

        Given("PostId 생성 요청이 주어졌을 때") {
            When("generate()로 PostId를 생성하면") {
                val postId = PostId.generate()

                Then("UUID 값이 자동 생성된다") {
                    postId.value shouldNotBe null
                }
            }
        }

        Given("UUID 문자열이 주어졌을 때") {
            val uuidString = "550e8400-e29b-41d4-a716-446655440000"

            When("from()으로 PostId를 생성하면") {
                val postId = PostId.from(uuidString)

                Then("문자열이 UUID로 변환되어 설정된다") {
                    postId.value.toString() shouldBe uuidString
                }
            }
        }

        Given("두 개의 동일한 UUID로 생성된 PostId가 주어졌을 때") {
            val uuid = UUID.randomUUID()
            val postId1 = PostId(uuid)
            val postId2 = PostId(uuid)

            When("두 객체를 비교하면") {
                Then("같은 값으로 인식된다") {
                    postId1 shouldBe postId2
                    postId1.hashCode() shouldBe postId2.hashCode()
                }
            }
        }

        Given("PostId 객체가 주어졌을 때") {
            val postId = PostId.generate()

            When("toString()을 호출하면") {
                val result = postId.toString()

                Then("UUID 문자열 형식이 반환된다") {
                    result shouldMatch "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                }
            }
        }
    })

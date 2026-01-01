package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * ContentType enum 테스트
 *
 * ContentType은 Post의 종류를 구분합니다 (블로그 글 또는 포트폴리오).
 */
class ContentTypeTest :
    BehaviorSpec({
        Given("BLOG ContentType이 주어졌을 때") {
            val contentType = ContentType.BLOG

            Then("올바른 값을 가진다") {
                contentType.name shouldBe "BLOG"
            }
        }

        Given("PORTFOLIO ContentType이 주어졌을 때") {
            val contentType = ContentType.PORTFOLIO

            Then("올바른 값을 가진다") {
                contentType.name shouldBe "PORTFOLIO"
            }
        }

        Given("모든 ContentType 값이 요청되었을 때") {
            When("values()를 호출하면") {
                val allTypes = ContentType.entries

                Then("BLOG와 PORTFOLIO가 포함된다") {
                    allTypes.map { it.name } shouldContainExactlyInAnyOrder listOf("BLOG", "PORTFOLIO")
                }
            }
        }

        Given("BLOG 문자열이 주어졌을 때") {
            val typeString = "BLOG"

            When("valueOf()로 ContentType을 생성하면") {
                val contentType = ContentType.valueOf(typeString)

                Then("BLOG ContentType이 반환된다") {
                    contentType shouldBe ContentType.BLOG
                }
            }
        }

        Given("PORTFOLIO 문자열이 주어졌을 때") {
            val typeString = "PORTFOLIO"

            When("valueOf()로 ContentType을 생성하면") {
                val contentType = ContentType.valueOf(typeString)

                Then("PORTFOLIO ContentType이 반환된다") {
                    contentType shouldBe ContentType.PORTFOLIO
                }
            }
        }
    })

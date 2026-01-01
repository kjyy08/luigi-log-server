package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Body Value Object 테스트
 *
 * Body는 Post의 본문 내용으로 Markdown 형식을 저장합니다.
 */
class BodyTest :
    BehaviorSpec({
        Given("유효한 Markdown 본문이 주어졌을 때") {
            val validBody =
                """
                # 제목

                본문 내용입니다.

                ## 소제목
                - 리스트 1
                - 리스트 2
                """.trimIndent()

            When("Body 객체를 생성하면") {
                val body = Body(validBody)

                Then("값이 올바르게 설정된다") {
                    body.value shouldBe validBody
                }
            }
        }

        Given("단순한 텍스트가 주어졌을 때") {
            val simpleText = "단순한 텍스트 본문"

            When("Body 객체를 생성하면") {
                val body = Body(simpleText)

                Then("객체가 정상적으로 생성된다") {
                    body.value shouldBe simpleText
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            When("Body 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Body("")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Body cannot be blank"
                }
            }
        }

        Given("공백 문자열이 주어졌을 때") {
            When("Body 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Body("   ")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Body cannot be blank"
                }
            }
        }

        Given("개행 문자만 포함된 문자열이 주어졌을 때") {
            When("Body 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Body("\n\n\n")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Body cannot be blank"
                }
            }
        }

        Given("매우 긴 Markdown 본문이 주어졌을 때") {
            val longBody = "본문 ".repeat(10000)

            When("Body 객체를 생성하면") {
                val body = Body(longBody)

                Then("객체가 정상적으로 생성된다") {
                    body.value shouldBe longBody
                }
            }
        }

        Given("코드 블록이 포함된 Markdown이 주어졌을 때") {
            val bodyWithCode =
                """
                ```kotlin
                fun main() {
                    println("Hello World")
                }
                ```
                """.trimIndent()

            When("Body 객체를 생성하면") {
                val body = Body(bodyWithCode)

                Then("객체가 정상적으로 생성된다") {
                    body.value shouldBe bodyWithCode
                }
            }
        }

        Given("두 개의 동일한 본문으로 생성된 Body가 주어졌을 때") {
            val bodyString = "동일한 본문"
            val body1 = Body(bodyString)
            val body2 = Body(bodyString)

            When("두 객체를 비교하면") {
                Then("같은 값으로 인식된다") {
                    body1 shouldBe body2
                    body1.hashCode() shouldBe body2.hashCode()
                }
            }
        }

        Given("Body 객체가 주어졌을 때") {
            val bodyString = "테스트 본문"
            val body = Body(bodyString)

            When("toString()을 호출하면") {
                val result = body.toString()

                Then("원본 문자열이 반환된다") {
                    result shouldBe bodyString
                }
            }
        }
    })

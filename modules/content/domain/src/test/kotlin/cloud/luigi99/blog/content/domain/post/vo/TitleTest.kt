package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Title Value Object 테스트
 *
 * Title은 Post의 제목으로 1-200자 제한이 있습니다.
 */
class TitleTest :
    BehaviorSpec({
        Given("유효한 제목이 주어졌을 때") {
            val validTitle = "테스트 블로그 글 제목"

            When("Title 객체를 생성하면") {
                val title = Title(validTitle)

                Then("값이 올바르게 설정된다") {
                    title.value shouldBe validTitle
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            When("Title 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Title("")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Title cannot be blank"
                }
            }
        }

        Given("공백 문자열이 주어졌을 때") {
            When("Title 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Title("   ")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Title cannot be blank"
                }
            }
        }

        Given("1자 제목이 주어졌을 때") {
            val singleChar = "제"

            When("Title 객체를 생성하면") {
                val title = Title(singleChar)

                Then("객체가 정상적으로 생성된다") {
                    title.value shouldBe singleChar
                }
            }
        }

        Given("200자 제목이 주어졌을 때") {
            val maxLength = "가".repeat(200)

            When("Title 객체를 생성하면") {
                val title = Title(maxLength)

                Then("객체가 정상적으로 생성된다") {
                    title.value shouldBe maxLength
                }
            }
        }

        Given("201자 제목이 주어졌을 때") {
            val tooLong = "가".repeat(201)

            When("Title 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Title(tooLong)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Title must be between 1 and 200 characters"
                    exception.message shouldContain "201"
                }
            }
        }

        Given("두 개의 동일한 제목으로 생성된 Title이 주어졌을 때") {
            val titleString = "동일한 제목"
            val title1 = Title(titleString)
            val title2 = Title(titleString)

            When("두 객체를 비교하면") {
                Then("같은 값으로 인식된다") {
                    title1 shouldBe title2
                    title1.hashCode() shouldBe title2.hashCode()
                }
            }
        }

        Given("Title 객체가 주어졌을 때") {
            val titleString = "테스트 제목"
            val title = Title(titleString)

            When("toString()을 호출하면") {
                val result = title.toString()

                Then("원본 문자열이 반환된다") {
                    result shouldBe titleString
                }
            }
        }
    })

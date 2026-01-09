package cloud.luigi99.blog.content.guestbook.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class GuestbookContentTest :
    BehaviorSpec({

        Given("사용자가 방명록에 올바른 내용을 작성했을 때") {
            val validContent = "안녕하세요! 블로그 잘 보고 있습니다."

            When("방명록 내용 객체를 생성하면") {
                val content = GuestbookContent(validContent)

                Then("입력한 내용이 정상적으로 저장된다") {
                    content.value shouldBe validContent
                }
            }
        }

        Given("사용자가 방명록 내용을 입력하지 않았을 때") {
            val emptyContent = ""

            When("방명록 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuestbookContent(emptyContent)
                    }

                Then("빈 내용은 허용되지 않으므로 생성이 거절된다") {
                    exception.message shouldContain "방명록 내용"
                }
            }
        }

        Given("사용자가 공백만 입력했을 때") {
            val blankContent = "   "

            When("방명록 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuestbookContent(blankContent)
                    }

                Then("실질적인 내용이 없으므로 생성이 거절된다") {
                    exception.message shouldContain "방명록 내용"
                }
            }
        }

        Given("사용자가 최대 길이를 초과하는 내용을 입력했을 때") {
            val tooLongContent = "a".repeat(5001)

            When("방명록 내용 객체 생성을 시도하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuestbookContent(tooLongContent)
                    }

                Then("최대 길이 초과로 생성이 거절된다") {
                    exception.message shouldContain "5000"
                }
            }
        }

        Given("사용자가 최대 길이까지 내용을 입력했을 때") {
            val maxLengthContent = "a".repeat(5000)

            When("방명록 내용 객체를 생성하면") {
                val content = GuestbookContent(maxLengthContent)

                Then("정상적으로 생성된다") {
                    content shouldNotBe null
                    content.value.length shouldBe 5000
                }
            }
        }

        Given("동일한 내용을 가진 두 방명록 내용 객체가 있을 때") {
            val contentText = "동일한 내용입니다."
            val content1 = GuestbookContent(contentText)
            val content2 = GuestbookContent(contentText)

            Then("두 객체는 논리적으로 동일한 것으로 취급한다") {
                content1 shouldBe content2
            }

            Then("해시코드도 동일하다") {
                content1.hashCode() shouldBe content2.hashCode()
            }
        }
    })

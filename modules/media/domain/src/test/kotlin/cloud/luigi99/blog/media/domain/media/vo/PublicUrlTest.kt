package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * PublicUrl Value Object 테스트
 */
class PublicUrlTest :
    BehaviorSpec({
        Given("유효한 접근 URL이 주어졌을 때") {
            val validUrl = "https://r2.dev/2025/01/test.png"

            When("PublicUrl 객체를 생성하면") {
                val publicUrl = PublicUrl(validUrl)

                Then("값이 정상적으로 저장된다") {
                    publicUrl.value shouldBe validUrl
                }
            }
        }

        Given("URL이 비어있는 경우") {
            When("객체 생성을 시도하면") {
                Then("필수 값 누락 오류가 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            PublicUrl("")
                        }
                    exception.message shouldContain "Public URL cannot be blank"
                }
            }
        }
    })

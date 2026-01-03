package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * OriginalFileName Value Object 테스트
 */
class OriginalFileNameTest :
    BehaviorSpec({
        Given("유효한 파일 이름이 주어졌을 때") {
            val validFileName = "test-image.png"

            When("객체를 생성하면") {
                val originalFileName = OriginalFileName(validFileName)

                Then("정상적으로 값이 저장된다") {
                    originalFileName.value shouldBe validFileName
                }
            }
        }

        Given("파일 이름이 비어있는 경우") {
            When("객체 생성을 시도하면") {
                Then("필수 값 누락 오류가 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            OriginalFileName("")
                        }
                    exception.message shouldContain "Original file name cannot be blank"
                }
            }
        }

        Given("파일 이름이 시스템 허용 길이(255자)를 초과하는 경우") {
            val longFileName = "a".repeat(256)

            When("객체 생성을 시도하면") {
                Then("길이 제한 초과 오류가 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            OriginalFileName(longFileName)
                        }
                    exception.message shouldContain "File name too long"
                }
            }
        }

        Given("파일 이름이 최대 허용 길이(255자)인 경우") {
            val maxLengthFileName = "a".repeat(255)

            When("객체를 생성하면") {
                val originalFileName = OriginalFileName(maxLengthFileName)

                Then("정상적으로 생성된다") {
                    originalFileName.value shouldBe maxLengthFileName
                }
            }
        }
    })

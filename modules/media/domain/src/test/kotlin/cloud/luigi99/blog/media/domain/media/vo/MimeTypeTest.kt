package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * MimeType Value Object 테스트
 */
class MimeTypeTest :
    BehaviorSpec({
        Given("시스템에서 지원하는 이미지 형식(JPEG, PNG 등)인 경우") {
            val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "image/webp")

            allowedTypes.forEach { type ->
                When("$type 형식으로 객체를 생성하면") {
                    val mimeType = MimeType(type)

                    Then("정상적으로 생성된다") {
                        mimeType.value shouldBe type
                    }
                }
            }
        }

        Given("지원하지 않는 파일 형식(PDF, Video 등)인 경우") {
            val disallowedTypes = listOf("application/pdf", "video/mp4", "text/plain")

            disallowedTypes.forEach { type ->
                When("$type 형식으로 객체 생성을 시도하면") {
                    Then("지원하지 않는 미디어 타입 오류가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                MimeType(type)
                            }
                        exception.message shouldContain "Unsupported MIME type"
                    }
                }
            }
        }

        Given("파일 타입 허용 여부를 검사할 때") {
            When("허용된 타입을 확인하면") {
                Then("검증을 통과한다(true)") {
                    MimeType.isAllowed("image/jpeg") shouldBe true
                    MimeType.isAllowed("image/png") shouldBe true
                }
            }

            When("허용되지 않은 타입을 확인하면") {
                Then("검증에 실패한다(false)") {
                    MimeType.isAllowed("application/pdf") shouldBe false
                }
            }
        }
    })

package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.exception.ErrorCode
import cloud.luigi99.blog.media.domain.media.exception.InvalidFileTypeException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

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
                            shouldThrow<InvalidFileTypeException> {
                                MimeType(type)
                            }
                        exception.errorCode shouldBe ErrorCode.INVALID_FILE_TYPE
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

        Given("MIME 타입과 파일 시그니처가 일치하는 경우") {
            val samples =
                mapOf(
                    "image/jpeg" to byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0x00),
                    "image/png" to byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A),
                    "image/gif" to "GIF89a".toByteArray(),
                    "image/webp" to "RIFFxxxxWEBP".toByteArray(),
                )

            samples.forEach { (type, bytes) ->
                When("$type 파일 시그니처를 검증하면") {
                    Then("검증을 통과한다") {
                        MimeType.matchesMagicBytes(type, bytes) shouldBe true
                    }
                }
            }
        }

        Given("MIME 타입과 파일 시그니처가 일치하지 않는 경우") {
            When("PNG로 선언된 GIF 바이트를 검증하면") {
                Then("검증에 실패한다") {
                    MimeType.matchesMagicBytes("image/png", "GIF89a".toByteArray()) shouldBe false
                }
            }
        }
    })

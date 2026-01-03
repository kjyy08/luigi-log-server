package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * FileSize Value Object 테스트
 */
class FileSizeTest :
    BehaviorSpec({
        Given("허용 범위 내의 파일 크기가 주어지면") {
            val validSize = 1024L * 1024L // 1MB

            When("파일 크기 객체를 생성하면") {
                val fileSize = FileSize(validSize)

                Then("정상적으로 값이 저장된다") {
                    fileSize.bytes shouldBe validSize
                }
            }
        }

        Given("파일 크기가 0바이트인 경우") {
            When("객체 생성을 시도하면") {
                Then("최소 파일 크기 위반 오류가 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            FileSize(0L)
                        }
                    exception.message shouldContain "File size must be positive"
                }
            }
        }

        Given("다양한 크기의 파일 바이트가 주어졌을 때") {
            val testCases =
                mapOf(
                    1024L * 1024L to 1.0, // 1MB -> 1.0MB
                    2L * 1024L * 1024L to 2.0, // 2MB -> 2.0MB
                )

            testCases.forEach { (bytes, expectedMB) ->
                When("${bytes}바이트를 MB 단위로 변환하면") {
                    val fileSize = FileSize(bytes)
                    val megaBytes = fileSize.toMegaBytes()

                    Then("정확한 메가바이트 값(${expectedMB}MB)이 반환된다") {
                        megaBytes shouldBe expectedMB
                    }
                }
            }
        }
    })

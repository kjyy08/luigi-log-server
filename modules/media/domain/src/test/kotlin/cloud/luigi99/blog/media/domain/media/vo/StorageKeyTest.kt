package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch

/**
 * StorageKey Value Object 테스트
 */
class StorageKeyTest :
    BehaviorSpec({
        Given("유효한 저장소 키 경로가 주어졌을 때") {
            val validKey = "2025/01/550e8400-e29b-41d4-a716-446655440000.png"

            When("StorageKey 객체를 생성하면") {
                val storageKey = StorageKey(validKey)

                Then("경로 값이 정상적으로 저장된다") {
                    storageKey.value shouldBe validKey
                }
            }
        }

        Given("저장소 키가 비어있는 경우") {
            val emptyKey = ""

            When("객체 생성을 시도하면") {
                Then("필수 값 누락 오류가 발생한다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            StorageKey(emptyKey)
                        }
                    exception.message shouldContain "Storage key cannot be blank"
                }
            }
        }

        Given("업로드된 파일의 원본 이름이 있을 때") {
            val originalFileName = "test-image.png"

            When("저장소 경로(Key)를 자동 생성하면") {
                val storageKey = StorageKey.generate(originalFileName)

                Then("날짜(년/월)와 UUID를 조합한 고유한 경로가 생성된다") {
                    storageKey.value shouldMatch Regex("\\d{4}/\\d{2}/[0-9a-f-]{36}\\.png")
                }
            }
        }

        Given("확장자가 없는 파일명이 주어졌을 때") {
            val fileNameWithoutExt = "test-file"

            When("저장소 경로를 생성하면") {
                val storageKey = StorageKey.generate(fileNameWithoutExt)

                Then("확장자 없이 날짜와 UUID로만 경로가 생성된다") {
                    storageKey.value shouldMatch Regex("\\d{4}/\\d{2}/[0-9a-f-]{36}")
                }
            }
        }

        Given("연속으로 키 생성을 요청할 때") {
            When("두 번 생성하면") {
                val key1 = StorageKey.generate("test.png")
                val key2 = StorageKey.generate("test.png")

                Then("매번 서로 다른 고유한 키가 생성되어야 한다") {
                    key1.value shouldNotBe key2.value
                }
            }
        }
    })

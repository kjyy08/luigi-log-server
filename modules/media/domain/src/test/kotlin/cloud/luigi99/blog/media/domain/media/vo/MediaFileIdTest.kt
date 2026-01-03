package cloud.luigi99.blog.media.domain.media.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

/**
 * MediaFileId Value Object 테스트
 */
class MediaFileIdTest :
    BehaviorSpec({
        Given("외부에서 생성된 UUID가 있는 경우") {
            val uuid = UUID.randomUUID()

            When("해당 UUID로 식별자를 생성하면") {
                val mediaFileId = MediaFileId.from(uuid)

                Then("동일한 값을 가진 식별자가 생성된다") {
                    mediaFileId.value shouldBe uuid
                }
            }
        }

        Given("문자열 형태의 UUID가 있는 경우") {
            val uuidString = "550e8400-e29b-41d4-a716-446655440000"

            When("식별자로 변환하면") {
                val mediaFileId = MediaFileId.from(uuidString)

                Then("유효한 UUID 객체로 저장된다") {
                    mediaFileId.value.toString() shouldBe uuidString
                }
            }
        }

        Given("새로운 미디어 파일 식별자가 필요한 경우") {
            When("식별자를 자동 생성하면") {
                val mediaFileId = MediaFileId.generate()

                Then("유효한 UUID 값을 가진 식별자가 반환된다") {
                    mediaFileId.value shouldNotBe null
                }

                Then("생성 시마다 매번 새로운 값이 부여된다") {
                    val another = MediaFileId.generate()
                    mediaFileId.value shouldNotBe another.value
                }
            }
        }

        Given("두 개의 동일한 UUID 값을 가진 식별자가 있을 때") {
            val uuid = UUID.randomUUID()
            val id1 = MediaFileId.from(uuid)
            val id2 = MediaFileId.from(uuid)

            When("두 식별자를 비교하면") {
                Then("동일한 객체(동등성 보장)로 판단해야 한다") {
                    id1 shouldBe id2
                }

                Then("해시코드 또한 동일해야 한다") {
                    id1.hashCode() shouldBe id2.hashCode()
                }
            }
        }

        Given("서로 다른 UUID를 가진 식별자가 있을 때") {
            val id1 = MediaFileId.generate()
            val id2 = MediaFileId.generate()

            When("두 식별자를 비교하면") {
                Then("다른 객체로 판단해야 한다") {
                    (id1 == id2) shouldBe false
                }
            }
        }
    })

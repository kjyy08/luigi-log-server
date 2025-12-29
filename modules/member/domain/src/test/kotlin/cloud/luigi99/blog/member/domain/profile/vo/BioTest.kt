package cloud.luigi99.blog.member.domain.profile.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class BioTest :
    BehaviorSpec({

        Given("유효한 자기소개가 주어졌을 때") {
            val validBio = "안녕하세요. 백엔드 개발자입니다."

            When("Bio 객체를 생성하면") {
                val bio = Bio(validBio)

                Then("자기소개 값이 올바르게 저장된다") {
                    bio.value shouldBe validBio
                }
            }
        }

        Given("다양한 길이의 유효한 자기소개가 주어졌을 때") {
            val validBios =
                listOf(
                    "간단한 소개",
                    "Hello, I'm a developer.",
                    "안녕하세요.\n줄바꿈도 포함됩니다.",
                    "a".repeat(500), // 최대 길이 500자
                )

            validBios.forEach { validBio ->
                When("자기소개로 Bio 객체를 생성하면") {
                    val bio = Bio(validBio)

                    Then("정상적으로 생성된다") {
                        bio shouldNotBe null
                        bio.value shouldBe validBio
                    }
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            val emptyBio = ""

            When("Bio 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Bio(emptyBio)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Bio cannot be blank"
                }
            }
        }

        Given("공백만 있는 문자열이 주어졌을 때") {
            val blankBio = "   "

            When("Bio 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Bio(blankBio)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Bio cannot be blank"
                }
            }
        }

        Given("501자를 초과하는 자기소개가 주어졌을 때") {
            val longBio = "a".repeat(501)

            When("Bio 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Bio(longBio)
                    }

                Then("예외가 발생하며 최대 길이 요구사항을 언급한다") {
                    exception.message shouldContain "Bio cannot exceed 500 characters"
                }
            }
        }

        Given("동일한 자기소개로 생성된 두 Bio 객체가 있을 때") {
            val bio1 = Bio("백엔드 개발자입니다.")
            val bio2 = Bio("백엔드 개발자입니다.")

            Then("두 객체는 동일하다") {
                bio1 shouldBe bio2
            }

            Then("해시코드도 동일하다") {
                bio1.hashCode() shouldBe bio2.hashCode()
            }
        }

        Given("다른 자기소개로 생성된 두 Bio 객체가 있을 때") {
            val bio1 = Bio("백엔드 개발자입니다.")
            val bio2 = Bio("프론트엔드 개발자입니다.")

            Then("두 객체는 다르다") {
                bio1 shouldNotBe bio2
            }
        }

        Given("경계값 테스트") {
            When("정확히 500자인 자기소개를 생성하면") {
                val bio = Bio("a".repeat(500))

                Then("정상적으로 생성된다") {
                    bio.value.length shouldBe 500
                }
            }
        }

        Given("특수 문자가 포함된 자기소개가 주어졌을 때") {
            val bioWithSpecialChars = "Hello! 👋 I'm a developer. Email: test@example.com"

            When("Bio 객체를 생성하면") {
                val bio = Bio(bioWithSpecialChars)

                Then("특수 문자를 포함하여 정상적으로 생성된다") {
                    bio.value shouldBe bioWithSpecialChars
                }
            }
        }
    })

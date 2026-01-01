package cloud.luigi99.blog.content.domain.post.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Slug Value Object 테스트
 *
 * Slug는 URL에 사용되는 고유 식별자로 소문자, 숫자, 하이픈만 허용됩니다.
 */
class SlugTest :
    BehaviorSpec({
        Given("유효한 slug가 주어졌을 때") {
            val validSlug = "my-first-blog-post"

            When("Slug 객체를 생성하면") {
                val slug = Slug(validSlug)

                Then("값이 올바르게 설정된다") {
                    slug.value shouldBe validSlug
                }
            }
        }

        Given("숫자가 포함된 slug가 주어졌을 때") {
            val slugWithNumbers = "blog-post-2024"

            When("Slug 객체를 생성하면") {
                val slug = Slug(slugWithNumbers)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe slugWithNumbers
                }
            }
        }

        Given("빈 문자열이 주어졌을 때") {
            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug("")
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Slug cannot be blank"
                }
            }
        }

        Given("대문자가 포함된 slug가 주어졌을 때") {
            val invalidSlug = "My-First-Post"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                    exception.message shouldContain invalidSlug
                }
            }
        }

        Given("공백이 포함된 slug가 주어졌을 때") {
            val invalidSlug = "my first post"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                }
            }
        }

        Given("특수 문자가 포함된 slug가 주어졌을 때") {
            val invalidSlug = "my_post@2024"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                }
            }
        }

        Given("하이픈으로 시작하는 slug가 주어졌을 때") {
            val invalidSlug = "-my-post"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                }
            }
        }

        Given("하이픈으로 끝나는 slug가 주어졌을 때") {
            val invalidSlug = "my-post-"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                }
            }
        }

        Given("연속된 하이픈이 포함된 slug가 주어졌을 때") {
            val invalidSlug = "my--post"

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(invalidSlug)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Invalid slug format"
                }
            }
        }

        Given("255자 slug가 주어졌을 때") {
            val maxLength = "a".repeat(255)

            When("Slug 객체를 생성하면") {
                val slug = Slug(maxLength)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe maxLength
                }
            }
        }

        Given("256자 slug가 주어졌을 때") {
            val tooLong = "a".repeat(256)

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(tooLong)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Slug must not exceed 255 characters"
                    exception.message shouldContain "256"
                }
            }
        }

        Given("두 개의 동일한 slug로 생성된 Slug가 주어졌을 때") {
            val slugString = "same-slug"
            val slug1 = Slug(slugString)
            val slug2 = Slug(slugString)

            When("두 객체를 비교하면") {
                Then("같은 값으로 인식된다") {
                    slug1 shouldBe slug2
                    slug1.hashCode() shouldBe slug2.hashCode()
                }
            }
        }

        Given("Slug 객체가 주어졌을 때") {
            val slugString = "test-slug"
            val slug = Slug(slugString)

            When("toString()을 호출하면") {
                val result = slug.toString()

                Then("원본 문자열이 반환된다") {
                    result shouldBe slugString
                }
            }
        }
    })

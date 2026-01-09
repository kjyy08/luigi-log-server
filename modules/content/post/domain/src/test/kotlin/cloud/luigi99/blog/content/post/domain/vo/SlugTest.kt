package cloud.luigi99.blog.content.post.domain.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Slug Value Object 테스트
 *
 * Slug는 URL에 사용되는 고유 식별자로 한글, 영문, 숫자, 하이픈, 대괄호를 허용합니다.
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

        Given("500자 slug가 주어졌을 때") {
            val maxLength = "a".repeat(500)

            When("Slug 객체를 생성하면") {
                val slug = Slug(maxLength)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe maxLength
                }
            }
        }

        Given("501자 slug가 주어졌을 때") {
            val tooLong = "a".repeat(501)

            When("Slug 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        Slug(tooLong)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldContain "Slug must not exceed 500 characters"
                    exception.message shouldContain "501"
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

        Given("한글이 포함된 slug가 주어졌을 때") {
            val koreanSlug = "스위프-후기-생성형-ai를-활용한-여행-서비스-개발-회고"

            When("Slug 객체를 생성하면") {
                val slug = Slug(koreanSlug)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe koreanSlug
                }
            }
        }

        Given("한글과 영문이 혼합된 slug가 주어졌을 때") {
            val mixedSlug = "kotlin-코틀린-spring-스프링부트-tutorial"

            When("Slug 객체를 생성하면") {
                val slug = Slug(mixedSlug)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe mixedSlug
                }
            }
        }

        Given("한글과 숫자가 포함된 slug가 주어졌을 때") {
            val koreanWithNumbers = "블로그-포스트-2024"

            When("Slug 객체를 생성하면") {
                val slug = Slug(koreanWithNumbers)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe koreanWithNumbers
                }
            }
        }

        Given("순수 한글만 포함된 slug가 주어졌을 때") {
            val pureKorean = "한글슬러그"

            When("Slug 객체를 생성하면") {
                val slug = Slug(pureKorean)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe pureKorean
                }
            }
        }

        Given("대괄호가 포함된 slug가 주어졌을 때") {
            val withBrackets = "[스위프-후기]-생성형-AI를-활용한-여행-서비스-개발-회고"

            When("Slug 객체를 생성하면") {
                val slug = Slug(withBrackets)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe withBrackets
                }
            }
        }

        Given("대문자가 포함된 slug가 주어졌을 때") {
            val withUppercase = "Spring-Boot-Tutorial"

            When("Slug 객체를 생성하면") {
                val slug = Slug(withUppercase)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe withUppercase
                }
            }
        }

        Given("대괄호와 한글이 혼합된 slug가 주어졌을 때") {
            val mixed = "[Kotlin]-코틀린-완벽가이드-[2024]"

            When("Slug 객체를 생성하면") {
                val slug = Slug(mixed)

                Then("객체가 정상적으로 생성된다") {
                    slug.value shouldBe mixed
                }
            }
        }
    })

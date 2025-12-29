package cloud.luigi99.blog.member.domain.profile.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TechStackTest :
    BehaviorSpec({

        Given("유효한 기술 스택 목록이 주어졌을 때") {
            val validTechList = listOf("Kotlin", "Spring Boot", "PostgreSQL")

            When("TechStack 객체를 생성하면") {
                val techStack = TechStack(validTechList)

                Then("기술 스택 값이 올바르게 저장된다") {
                    techStack.values shouldBe validTechList
                }

                Then("isEmpty()는 false를 반환한다") {
                    techStack.isEmpty() shouldBe false
                }
            }
        }

        Given("빈 목록이 주어졌을 때") {
            val emptyList = emptyList<String>()

            When("TechStack 객체를 생성하면") {
                val techStack = TechStack(emptyList)

                Then("정상적으로 생성된다") {
                    techStack shouldNotBe null
                }

                Then("isEmpty()는 true를 반환한다") {
                    techStack.isEmpty() shouldBe true
                }

                Then("values는 빈 목록이다") {
                    techStack.values shouldBe emptyList
                }
            }
        }

        Given("다양한 개수의 기술 스택이 주어졌을 때") {
            When("1개의 기술을 포함하면") {
                val techStack = TechStack(listOf("Kotlin"))

                Then("정상적으로 생성된다") {
                    techStack.values.size shouldBe 1
                }
            }

            When("10개의 기술을 포함하면") {
                val techList = (1..10).map { "Tech$it" }
                val techStack = TechStack(techList)

                Then("정상적으로 생성된다") {
                    techStack.values.size shouldBe 10
                }
            }

            When("정확히 50개의 기술을 포함하면") {
                val techList = (1..50).map { "Tech$it" }
                val techStack = TechStack(techList)

                Then("정상적으로 생성된다") {
                    techStack.values.size shouldBe 50
                }
            }
        }

        Given("51개를 초과하는 기술 스택이 주어졌을 때") {
            val tooManyTechs = (1..51).map { "Tech$it" }

            When("TechStack 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        TechStack(tooManyTechs)
                    }

                Then("예외가 발생하며 최대 개수 요구사항을 언급한다") {
                    exception.message shouldBe "Tech stack cannot exceed 50 items"
                }
            }
        }

        Given("빈 문자열을 포함하는 기술 스택이 주어졌을 때") {
            val techListWithEmpty = listOf("Kotlin", "", "Spring Boot")

            When("TechStack 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        TechStack(techListWithEmpty)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldBe "Tech stack items cannot be blank"
                }
            }
        }

        Given("공백만 있는 문자열을 포함하는 기술 스택이 주어졌을 때") {
            val techListWithBlank = listOf("Kotlin", "   ", "Spring Boot")

            When("TechStack 객체를 생성하려고 하면") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        TechStack(techListWithBlank)
                    }

                Then("예외가 발생한다") {
                    exception.message shouldBe "Tech stack items cannot be blank"
                }
            }
        }

        Given("동일한 기술 스택으로 생성된 두 TechStack 객체가 있을 때") {
            val techList = listOf("Kotlin", "Spring Boot")
            val techStack1 = TechStack(techList)
            val techStack2 = TechStack(techList)

            Then("두 객체는 동일하다") {
                techStack1 shouldBe techStack2
            }

            Then("해시코드도 동일하다") {
                techStack1.hashCode() shouldBe techStack2.hashCode()
            }
        }

        Given("다른 기술 스택으로 생성된 두 TechStack 객체가 있을 때") {
            val techStack1 = TechStack(listOf("Kotlin", "Spring Boot"))
            val techStack2 = TechStack(listOf("Java", "Spring Boot"))

            Then("두 객체는 다르다") {
                techStack1 shouldNotBe techStack2
            }
        }

        Given("순서가 다른 동일한 기술 스택이 주어졌을 때") {
            val techStack1 = TechStack(listOf("Kotlin", "Spring Boot", "PostgreSQL"))
            val techStack2 = TechStack(listOf("Spring Boot", "Kotlin", "PostgreSQL"))

            Then("두 객체는 다르다 (순서가 중요함)") {
                techStack1 shouldNotBe techStack2
            }
        }

        Given("중복된 기술이 포함된 기술 스택이 주어졌을 때") {
            val techListWithDuplicates = listOf("Kotlin", "Spring Boot", "Kotlin")

            When("TechStack 객체를 생성하면") {
                val techStack = TechStack(techListWithDuplicates)

                Then("중복을 허용하여 정상적으로 생성된다") {
                    techStack.values shouldBe techListWithDuplicates
                    techStack.values.size shouldBe 3
                }
            }
        }

        Given("특수 문자가 포함된 기술 이름이 주어졌을 때") {
            val techListWithSpecialChars = listOf("C++", "C#", ".NET", "Vue.js")

            When("TechStack 객체를 생성하면") {
                val techStack = TechStack(techListWithSpecialChars)

                Then("특수 문자를 포함하여 정상적으로 생성된다") {
                    techStack.values shouldBe techListWithSpecialChars
                }
            }
        }
    })

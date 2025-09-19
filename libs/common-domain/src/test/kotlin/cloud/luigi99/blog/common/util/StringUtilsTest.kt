package cloud.luigi99.blog.common.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * StringUtils의 행위를 검증하는 테스트
 */
class StringUtilsTest : BehaviorSpec({

    given("문자열 공백 확인 기능을 테스트할 때") {
        `when`("null 문자열을 확인하면") {
            val result = StringUtils.isBlank(null)

            then("true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("빈 문자열을 확인하면") {
            val result = StringUtils.isBlank("")

            then("true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("공백만 있는 문자열을 확인하면") {
            val result = StringUtils.isBlank("   ")

            then("true를 반환한다") {
                result shouldBe true
            }
        }

        `when`("값이 있는 문자열을 확인하면") {
            val result = StringUtils.isBlank("hello")

            then("false를 반환한다") {
                result shouldBe false
            }
        }
    }

    given("문자열 비공백 확인 기능을 테스트할 때") {
        `when`("null 문자열을 확인하면") {
            val result = StringUtils.isNotBlank(null)

            then("false를 반환한다") {
                result shouldBe false
            }
        }

        `when`("값이 있는 문자열을 확인하면") {
            val result = StringUtils.isNotBlank("hello")

            then("true를 반환한다") {
                result shouldBe true
            }
        }
    }

    given("카멜케이스 변환 기능을 테스트할 때") {
        `when`("언더스코어 문자열을 변환하면") {
            val result = StringUtils.toCamelCase("hello_world_test")

            then("카멜케이스로 변환된다") {
                result shouldBe "helloWorldTest"
            }
        }

        `when`("하이픈 문자열을 변환하면") {
            val result = StringUtils.toCamelCase("hello-world-test")

            then("카멜케이스로 변환된다") {
                result shouldBe "helloWorldTest"
            }
        }

        `when`("공백 문자열을 변환하면") {
            val result = StringUtils.toCamelCase("hello world test")

            then("카멜케이스로 변환된다") {
                result shouldBe "helloWorldTest"
            }
        }

        `when`("빈 문자열을 변환하면") {
            val result = StringUtils.toCamelCase("")

            then("빈 문자열을 반환한다") {
                result shouldBe ""
            }
        }
    }

    given("스네이크케이스 변환 기능을 테스트할 때") {
        `when`("카멜케이스 문자열을 변환하면") {
            val result = StringUtils.toSnakeCase("helloWorldTest")

            then("스네이크케이스로 변환된다") {
                result shouldBe "hello_world_test"
            }
        }

        `when`("단일 단어를 변환하면") {
            val result = StringUtils.toSnakeCase("hello")

            then("그대로 반환된다") {
                result shouldBe "hello"
            }
        }

        `when`("빈 문자열을 변환하면") {
            val result = StringUtils.toSnakeCase("")

            then("빈 문자열을 반환한다") {
                result shouldBe ""
            }
        }
    }
})
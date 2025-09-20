package cloud.luigi99.blog.common.security

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.string.shouldStartWith

class BCryptPasswordEncoderImplTest : BehaviorSpec({

    given("올바른 비밀번호") {
        val rawPassword = "testPassword123!"

        `when`("암호화할 때") {
            then("올바른 BCrypt 해시가 생성되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val encodedPassword = passwordEncoder.encode(rawPassword)

                encodedPassword.shouldNotBeNull()
                encodedPassword shouldNotBe rawPassword
                encodedPassword shouldStartWith "\$2a\$12\$"
                encodedPassword.length shouldBeGreaterThanOrEqual 60
            }
        }

        `when`("같은 비밀번호를 여러 번 암호화할 때") {
            then("다른 해시가 생성되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val encodedPassword1 = passwordEncoder.encode(rawPassword)
                val encodedPassword2 = passwordEncoder.encode(rawPassword)

                encodedPassword1 shouldNotBe encodedPassword2
                passwordEncoder.matches(rawPassword, encodedPassword1) shouldBe true
                passwordEncoder.matches(rawPassword, encodedPassword2) shouldBe true
            }
        }
    }

    given("비밀번호와 해시") {
        `when`("올바른 비밀번호로 매칭을 확인할 때") {
            then("일치해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val rawPassword = "testPassword123!"
                val encodedPassword = passwordEncoder.encode(rawPassword)

                val matches = passwordEncoder.matches(rawPassword, encodedPassword)
                matches shouldBe true
            }
        }

        `when`("잘못된 비밀번호로 매칭을 확인할 때") {
            then("일치하지 않아야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val rawPassword = "testPassword123!"
                val wrongPassword = "wrongPassword456!"
                val encodedPassword = passwordEncoder.encode(rawPassword)

                val matches = passwordEncoder.matches(wrongPassword, encodedPassword)
                matches shouldBe false
            }
        }
    }

    given("유효하지 않은 비밀번호") {
        `when`("빈 문자열로 암호화를 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val emptyPassword = ""

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.encode(emptyPassword)
                }
                exception.message shouldBe "비밀번호는 공백일 수 없습니다."
            }
        }

        `when`("공백만 있는 비밀번호로 암호화를 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val blankPassword = "   "

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.encode(blankPassword)
                }
                exception.message shouldBe "비밀번호는 공백일 수 없습니다."
            }
        }

        `when`("너무 짧은 비밀번호로 암호화를 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val shortPassword = "1234567" // 7글자

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.encode(shortPassword)
                }
                exception.message shouldBe "비밀번호는 최소 8자 이상이어야 합니다."
            }
        }

        `when`("너무 긴 비밀번호로 암호화를 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val longPassword = "a".repeat(73) // 73글자

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.encode(longPassword)
                }
                exception.message shouldBe "비밀번호는 최대 72자 이하여야 합니다."
            }
        }
    }

    given("경계값 비밀번호") {
        `when`("최소 길이 비밀번호를 암호화할 때") {
            then("정상적으로 암호화되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val minLengthPassword = "12345678" // 8글자
                val encodedPassword = passwordEncoder.encode(minLengthPassword)

                encodedPassword.shouldNotBeNull()
                passwordEncoder.matches(minLengthPassword, encodedPassword) shouldBe true
            }
        }

        `when`("최대 길이 비밀번호를 암호화할 때") {
            then("정상적으로 암호화되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val maxLengthPassword = "a".repeat(72) // 72글자
                val encodedPassword = passwordEncoder.encode(maxLengthPassword)

                encodedPassword.shouldNotBeNull()
                passwordEncoder.matches(maxLengthPassword, encodedPassword) shouldBe true
            }
        }
    }

    given("매칭 과정에서의 예외 상황") {
        `when`("빈 문자열로 매칭을 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val validEncodedPassword = passwordEncoder.encode("testPassword123!")
                val emptyPassword = ""

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.matches(emptyPassword, validEncodedPassword)
                }
                exception.message shouldBe "비밀번호는 공백일 수 없습니다."
            }
        }

        `when`("빈 해시로 매칭을 시도할 때") {
            then("예외가 발생해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val rawPassword = "testPassword123!"
                val emptyEncodedPassword = ""

                val exception = shouldThrow<IllegalArgumentException> {
                    passwordEncoder.matches(rawPassword, emptyEncodedPassword)
                }
                exception.message shouldBe "암호화된 비밀번호는 공백일 수 없습니다."
            }
        }

        `when`("잘못된 형식의 해시로 매칭을 시도할 때") {
            then("false를 반환해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val rawPassword = "testPassword123!"
                val invalidHash = "invalid-hash-format"

                val matches = passwordEncoder.matches(rawPassword, invalidHash)
                matches shouldBe false
            }
        }
    }

    given("특수한 문자를 포함한 비밀번호") {
        `when`("특수문자가 포함된 비밀번호를 처리할 때") {
            then("정상적으로 처리되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val passwordWithSpecialChars = "test@#\$%^&*()_+-=[]{}|;':\",./<>?`~"
                val encodedPassword = passwordEncoder.encode(passwordWithSpecialChars)

                encodedPassword.shouldNotBeNull()
                passwordEncoder.matches(passwordWithSpecialChars, encodedPassword) shouldBe true
            }
        }

        `when`("유니코드 문자가 포함된 비밀번호를 처리할 때") {
            then("정상적으로 처리되어야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val passwordWithUnicode = "테스트비밀번호123!"
                val encodedPassword = passwordEncoder.encode(passwordWithUnicode)

                encodedPassword.shouldNotBeNull()
                passwordEncoder.matches(passwordWithUnicode, encodedPassword) shouldBe true
            }
        }
    }

    given("암호화 강도 검증") {
        `when`("동일한 비밀번호를 반복 암호화할 때") {
            then("모든 해시가 다르지만 모두 검증에 성공해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val rawPassword = "consistencyTest123!"
                val hashes = (1..10).map { passwordEncoder.encode(rawPassword) }

                // 모든 해시가 서로 다른지 확인
                hashes.toSet().size shouldBe 10

                // 모든 해시가 원본 비밀번호와 매치되는지 확인
                hashes.forEach { hash ->
                    passwordEncoder.matches(rawPassword, hash) shouldBe true
                }
            }
        }
    }

    given("암호화 성능 검증") {
        `when`("여러 비밀번호를 빠르게 암호화할 때") {
            then("모든 암호화가 성공해야 한다") {
                val passwordEncoder = BCryptPasswordEncoderImpl()
                val passwords = (1..5).map { "password$it" }

                val encodedPasswords = passwords.map { passwordEncoder.encode(it) }

                encodedPasswords.size shouldBe 5
                encodedPasswords.forEach { encoded ->
                    encoded.shouldNotBeNull()
                    encoded shouldStartWith "\$2a\$12\$"
                }
            }
        }
    }
})
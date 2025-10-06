package cloud.luigi99.blog.user.domain

import cloud.luigi99.blog.user.domain.vo.Email
import cloud.luigi99.blog.user.domain.vo.UserRole
import cloud.luigi99.blog.user.domain.vo.UserStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime

class UserTest : BehaviorSpec({

    val mockEncoder: (String) -> String = { "encoded_$it" }
    val mockMatcher: (String, String) -> Boolean = { raw, encoded -> encoded == "encoded_$raw" }

    given("User 생성") {
        `when`("유효한 정보로 User를 생성하면") {
            val email = Email.of("test@example.com")
            val user = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "테스트유저",
                encode = mockEncoder,
                bio = "안녕하세요",
                profileImageUrl = "https://example.com/profile.jpg"
            )

            then("User가 생성된다") {
                user shouldNotBe null
                user.email shouldBe email
                user.status shouldBe UserStatus.ACTIVE
                user.profile.nickname shouldBe "테스트유저"
                user.profile.bio shouldBe "안녕하세요"
                user.profile.profileImageUrl shouldBe "https://example.com/profile.jpg"
                user.sessions shouldHaveSize 0
                user.lastLoginAt shouldBe null
            }

            and("UserCreatedEvent가 발행된다") {
                user.hasDomainEvents() shouldBe true
                user.hasDomainEvent("UserCreated") shouldBe true
            }
        }

        `when`("bio와 profileImageUrl 없이 User를 생성하면") {
            val email = Email.of("test@example.com")
            val user = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "테스트유저",
                encode = mockEncoder
            )

            then("User가 생성된다") {
                user shouldNotBe null
                user.profile.bio shouldBe null
                user.profile.profileImageUrl shouldBe null
            }
        }
    }

    given("User 상태 확인") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "테스트유저",
            encode = mockEncoder
        )

        `when`("ACTIVE 상태의 User를 확인하면") {
            then("활성 상태로 판단된다") {
                user.isActive() shouldBe true
                user.canUseService() shouldBe true
            }
        }

        `when`("INACTIVE 상태의 User를 확인하면") {
            val inactiveUser = user.copy(status = UserStatus.INACTIVE)

            then("서비스 이용이 불가능하다") {
                inactiveUser.isActive() shouldBe false
                inactiveUser.canUseService() shouldBe false
            }
        }

        `when`("SUSPENDED 상태의 User를 확인하면") {
            val suspendedUser = user.copy(status = UserStatus.SUSPENDED)

            then("서비스 이용이 불가능하다") {
                suspendedUser.isActive() shouldBe false
                suspendedUser.canUseService() shouldBe false
            }
        }
    }

    given("User 로그인") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "테스트유저",
            encode = mockEncoder
        )

        `when`("비밀번호가 일치하면") {
            val matches = user.matchesPassword("Password123!", mockMatcher)

            then("true가 반환된다") {
                matches shouldBe true
            }
        }

        `when`("비밀번호가 일치하지 않으면") {
            val matches = user.matchesPassword("WrongPassword!", mockMatcher)

            then("false가 반환된다") {
                matches shouldBe false
            }
        }

        `when`("로그인을 수행하면") {
            val expiresAt = LocalDateTime.now().plusDays(30)
            val loggedInUser = user.login(
                refreshToken = "refresh_token_123",
                expiresAt = expiresAt,
                deviceInfo = "Chrome on Windows",
                ipAddress = "192.168.1.1"
            )

            then("세션이 추가되고 lastLoginAt이 업데이트된다") {
                loggedInUser.sessions shouldHaveSize 1
                loggedInUser.lastLoginAt shouldNotBe null
                loggedInUser.updatedAt shouldNotBe null
            }

            and("UserLoggedInEvent가 발행된다") {
                loggedInUser.hasDomainEvent("UserLoggedIn") shouldBe true
            }
        }

        `when`("비활성 상태에서 로그인을 시도하면") {
            val inactiveUser = user.copy(status = UserStatus.INACTIVE)
            val expiresAt = LocalDateTime.now().plusDays(30)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalStateException> {
                    inactiveUser.login("refresh_token", expiresAt)
                }
                exception.message shouldContain "로그인할 수 없습니다"
            }
        }
    }

    given("User 프로필 업데이트") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "기존닉네임",
            encode = mockEncoder
        )

        `when`("프로필을 업데이트하면") {
            val updatedUser = user.updateProfile(
                nickname = "새닉네임",
                bio = "새로운 소개",
                profileImageUrl = "https://example.com/new.jpg"
            )

            then("프로필이 업데이트된다") {
                updatedUser.profile.nickname shouldBe "새닉네임"
                updatedUser.profile.bio shouldBe "새로운 소개"
                updatedUser.profile.profileImageUrl shouldBe "https://example.com/new.jpg"
                updatedUser.updatedAt shouldNotBe null
            }

            and("UserProfileUpdatedEvent가 발행된다") {
                updatedUser.hasDomainEvent("UserProfileUpdated") shouldBe true
            }
        }

        `when`("비활성 상태에서 프로필을 업데이트하려고 하면") {
            val inactiveUser = user.copy(status = UserStatus.INACTIVE)

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalStateException> {
                    inactiveUser.updateProfile(nickname = "새닉네임")
                }
                exception.message shouldContain "프로필을 수정할 수 없습니다"
            }
        }
    }

    given("User 비밀번호 변경") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "테스트유저",
            encode = mockEncoder
        )

        `when`("올바른 기존 비밀번호로 비밀번호를 변경하면") {
            val updatedUser = user.changePassword(
                oldRawPassword = "Password123!",
                newRawPassword = "NewPassword456!",
                matches = mockMatcher,
                encode = mockEncoder
            )

            then("비밀번호가 변경된다") {
                updatedUser.password.value shouldBe "encoded_NewPassword456!"
                updatedUser.updatedAt shouldNotBe null
            }
        }

        `when`("잘못된 기존 비밀번호로 비밀번호를 변경하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    user.changePassword(
                        oldRawPassword = "WrongPassword!",
                        newRawPassword = "NewPassword456!",
                        matches = mockMatcher,
                        encode = mockEncoder
                    )
                }
                exception.message shouldContain "일치하지 않습니다"
            }
        }
    }

    given("User 상태 전환") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "테스트유저",
            encode = mockEncoder
        )

        `when`("활성 상태에서 비활성화하면") {
            val deactivatedUser = user.deactivate()

            then("비활성 상태로 변경된다") {
                deactivatedUser.status shouldBe UserStatus.INACTIVE
                deactivatedUser.updatedAt shouldNotBe null
            }
        }

        `when`("비활성 상태에서 재활성화하면") {
            val deactivatedUser = user.deactivate()
            val reactivatedUser = deactivatedUser.activate()

            then("활성 상태로 변경된다") {
                reactivatedUser.status shouldBe UserStatus.ACTIVE
                reactivatedUser.updatedAt shouldNotBe null
            }
        }

        `when`("활성 상태에서 재활성화하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalStateException> {
                    user.activate()
                }
                exception.message shouldContain "비활성 상태의 계정만"
            }
        }

        `when`("활성 상태에서 정지하면") {
            val suspendedUser = user.suspend()

            then("정지 상태로 변경된다") {
                suspendedUser.status shouldBe UserStatus.SUSPENDED
                suspendedUser.updatedAt shouldNotBe null
            }
        }

        `when`("정지 상태에서 비활성화하려고 하면") {
            val suspendedUser = user.suspend()

            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalStateException> {
                    suspendedUser.deactivate()
                }
                exception.message shouldContain "비활성화할 수 없습니다"
            }
        }
    }

    given("User 세션 관리") {
        val email = Email.of("test@example.com")
        val user = User.create(
            email = email,
            rawPassword = "Password123!",
            nickname = "테스트유저",
            encode = mockEncoder
        )
        val expiresAt = LocalDateTime.now().plusDays(30)
        val userWithSession = user.login("refresh_token_1", expiresAt)

        `when`("세션을 제거하면") {
            val userWithoutSession = userWithSession.removeSession("refresh_token_1")

            then("세션이 제거된다") {
                userWithoutSession.sessions shouldHaveSize 0
                userWithoutSession.updatedAt shouldNotBe null
            }
        }

        `when`("만료된 세션을 제거하면") {
            val expiredExpiresAt = LocalDateTime.now().minusDays(1)
            val userWithExpiredSession = userWithSession.copy(
                sessions = userWithSession.sessions + userWithSession.sessions[0].copy(
                    expiresAt = expiredExpiresAt,
                    refreshToken = "expired_token"
                )
            )

            val cleanedUser = userWithExpiredSession.removeExpiredSessions()

            then("만료된 세션만 제거된다") {
                cleanedUser.sessions shouldHaveSize 1
                cleanedUser.sessions[0].refreshToken shouldBe "refresh_token_1"
            }
        }

        `when`("만료되지 않은 세션만 있을 때 removeExpiredSessions를 호출하면") {
            val cleanedUser = userWithSession.removeExpiredSessions()

            then("세션이 유지된다") {
                cleanedUser shouldBe userWithSession
            }
        }
    }

    given("User 권한 관리") {
        val email = Email.of("test@example.com")

        `when`("기본 역할로 User를 생성하면") {
            val user = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "테스트유저",
                encode = mockEncoder
            )

            then("USER 역할을 가진다") {
                user.role shouldBe UserRole.USER
            }

            then("댓글 작성 권한이 있다") {
                user.canComment() shouldBe true
            }

            then("콘텐츠 작성 및 관리 권한은 없다") {
                user.canWriteContent() shouldBe false
                user.canManage() shouldBe false
                user.isAdmin() shouldBe false
            }
        }

        `when`("ADMIN 역할로 User를 생성하면") {
            val admin = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "관리자",
                role = UserRole.ADMIN,
                encode = mockEncoder
            )

            then("ADMIN 역할을 가진다") {
                admin.role shouldBe UserRole.ADMIN
            }

            then("모든 권한이 있다") {
                admin.isAdmin() shouldBe true
                admin.canWriteContent() shouldBe true
                admin.canManage() shouldBe true
                admin.canComment() shouldBe true
            }
        }

        `when`("GUEST 역할로 User를 생성하면") {
            val guest = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "게스트",
                role = UserRole.GUEST,
                encode = mockEncoder
            )

            then("GUEST 역할을 가진다") {
                guest.role shouldBe UserRole.GUEST
            }

            then("권한이 제한된다") {
                guest.isAdmin() shouldBe false
                guest.canWriteContent() shouldBe false
                guest.canManage() shouldBe false
                guest.canComment() shouldBe false
            }
        }

        `when`("사용자 역할을 변경하면") {
            val user = User.create(
                email = email,
                rawPassword = "Password123!",
                nickname = "테스트유저",
                encode = mockEncoder
            )

            val admin = user.changeRole(UserRole.ADMIN)

            then("역할이 변경된다") {
                admin.role shouldBe UserRole.ADMIN
            }

            then("권한이 변경된다") {
                admin.isAdmin() shouldBe true
                admin.canWriteContent() shouldBe true
                admin.canManage() shouldBe true
            }

            then("updatedAt이 갱신된다") {
                admin.updatedAt shouldNotBe null
            }
        }
    }
})

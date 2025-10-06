package cloud.luigi99.blog.user.domain.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

/**
 * UserRole의 행위를 검증하는 테스트
 */
class UserRoleTest : BehaviorSpec({

    given("UserRole GUEST") {
        val guest = UserRole.GUEST

        `when`("권한을 확인하면") {
            then("게스트로 식별된다") {
                guest.isGuest() shouldBe true
                guest.isUser() shouldBe false
                guest.isAdmin() shouldBe false
            }

            then("제한된 권한만 가진다") {
                guest.canWriteContent() shouldBe false
                guest.canManage() shouldBe false
                guest.canComment() shouldBe false
            }

            then("가장 낮은 권한 레벨을 가진다") {
                guest.level shouldBe 0
            }
        }
    }

    given("UserRole USER") {
        val user = UserRole.USER

        `when`("권한을 확인하면") {
            then("일반 사용자로 식별된다") {
                user.isGuest() shouldBe false
                user.isUser() shouldBe true
                user.isAdmin() shouldBe false
            }

            then("댓글 작성 권한이 있다") {
                user.canComment() shouldBe true
            }

            then("콘텐츠 작성 및 관리 권한은 없다") {
                user.canWriteContent() shouldBe false
                user.canManage() shouldBe false
            }

            then("레벨 1의 권한을 가진다") {
                user.level shouldBe 1
            }
        }
    }

    given("UserRole ADMIN") {
        val admin = UserRole.ADMIN

        `when`("권한을 확인하면") {
            then("관리자로 식별된다") {
                admin.isGuest() shouldBe false
                admin.isUser() shouldBe false
                admin.isAdmin() shouldBe true
            }

            then("모든 권한이 있다") {
                admin.canWriteContent() shouldBe true
                admin.canManage() shouldBe true
                admin.canComment() shouldBe true
            }

            then("가장 높은 권한 레벨을 가진다") {
                admin.level shouldBe 2
            }
        }
    }

    given("권한 레벨 비교") {
        val guest = UserRole.GUEST
        val user = UserRole.USER
        val admin = UserRole.ADMIN

        `when`("높은 권한과 낮은 권한을 비교하면") {
            then("ADMIN이 USER보다 높은 권한이다") {
                admin.hasHigherLevelThan(user) shouldBe true
                user.hasHigherLevelThan(admin) shouldBe false
            }

            then("USER가 GUEST보다 높은 권한이다") {
                user.hasHigherLevelThan(guest) shouldBe true
                guest.hasHigherLevelThan(user) shouldBe false
            }

            then("ADMIN이 GUEST보다 높은 권한이다") {
                admin.hasHigherLevelThan(guest) shouldBe true
                guest.hasHigherLevelThan(admin) shouldBe false
            }
        }

        `when`("낮거나 같은 권한을 비교하면") {
            then("GUEST가 USER보다 낮거나 같은 권한이다") {
                guest.hasLowerOrEqualLevelThan(user) shouldBe true
            }

            then("USER가 ADMIN보다 낮거나 같은 권한이다") {
                user.hasLowerOrEqualLevelThan(admin) shouldBe true
            }

            then("같은 권한끼리는 낮거나 같은 권한이다") {
                user.hasLowerOrEqualLevelThan(user) shouldBe true
                admin.hasLowerOrEqualLevelThan(admin) shouldBe true
            }
        }
    }

    given("UserRole 문자열 변환") {
        `when`("유효한 문자열로 변환하면") {
            then("해당하는 UserRole을 반환한다") {
                UserRole.fromString("GUEST") shouldBe UserRole.GUEST
                UserRole.fromString("USER") shouldBe UserRole.USER
                UserRole.fromString("ADMIN") shouldBe UserRole.ADMIN
            }

            then("대소문자 구분 없이 변환된다") {
                UserRole.fromString("guest") shouldBe UserRole.GUEST
                UserRole.fromString("user") shouldBe UserRole.USER
                UserRole.fromString("admin") shouldBe UserRole.ADMIN
            }
        }

        `when`("유효하지 않은 문자열로 변환하면") {
            then("null을 반환한다") {
                UserRole.fromString("INVALID") shouldBe null
                UserRole.fromString("") shouldBe null
            }
        }

        `when`("fromStringOrDefault로 변환하면") {
            then("유효한 문자열은 해당 UserRole을 반환한다") {
                UserRole.fromStringOrDefault("ADMIN") shouldBe UserRole.ADMIN
            }

            then("유효하지 않은 문자열은 기본값을 반환한다") {
                UserRole.fromStringOrDefault("INVALID") shouldBe UserRole.USER
                UserRole.fromStringOrDefault("INVALID", UserRole.GUEST) shouldBe UserRole.GUEST
            }
        }
    }

    given("UserRole 속성") {
        `when`("displayName을 확인하면") {
            then("한글 이름이 반환된다") {
                UserRole.GUEST.displayName shouldBe "게스트"
                UserRole.USER.displayName shouldBe "사용자"
                UserRole.ADMIN.displayName shouldBe "관리자"
            }
        }

        `when`("description을 확인하면") {
            then("역할 설명이 반환된다") {
                UserRole.GUEST.description shouldBe "제한된 권한"
                UserRole.USER.description shouldBe "일반 사용자 권한"
                UserRole.ADMIN.description shouldBe "시스템 전체 관리 및 콘텐츠 작성 권한"
            }
        }
    }
})

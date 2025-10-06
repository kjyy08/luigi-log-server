package cloud.luigi99.blog.user.domain.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class UserStatusTest : BehaviorSpec({

    given("UserStatus 상태 확인") {
        `when`("ACTIVE 상태를 확인하면") {
            val status = UserStatus.ACTIVE

            then("활성 상태로 판단된다") {
                status.isActive() shouldBe true
                status.isInactive() shouldBe false
                status.isSuspended() shouldBe false
                status.canUseService() shouldBe true
            }
        }

        `when`("INACTIVE 상태를 확인하면") {
            val status = UserStatus.INACTIVE

            then("비활성 상태로 판단된다") {
                status.isActive() shouldBe false
                status.isInactive() shouldBe true
                status.isSuspended() shouldBe false
                status.canUseService() shouldBe false
            }
        }

        `when`("SUSPENDED 상태를 확인하면") {
            val status = UserStatus.SUSPENDED

            then("정지 상태로 판단된다") {
                status.isActive() shouldBe false
                status.isInactive() shouldBe false
                status.isSuspended() shouldBe true
                status.canUseService() shouldBe false
            }
        }
    }

    given("UserStatus 문자열 변환") {
        `when`("fromString을 유효한 문자열로 호출하면") {
            val status = UserStatus.fromString("ACTIVE")

            then("해당 상태가 반환된다") {
                status shouldBe UserStatus.ACTIVE
            }
        }

        `when`("fromString을 대소문자 무시하고 호출하면") {
            val status = UserStatus.fromString("active")

            then("해당 상태가 반환된다") {
                status shouldBe UserStatus.ACTIVE
            }
        }

        `when`("fromString을 잘못된 문자열로 호출하면") {
            val status = UserStatus.fromString("INVALID")

            then("null이 반환된다") {
                status shouldBe null
            }
        }

        `when`("fromStringOrDefault를 유효한 문자열로 호출하면") {
            val status = UserStatus.fromStringOrDefault("ACTIVE")

            then("해당 상태가 반환된다") {
                status shouldBe UserStatus.ACTIVE
            }
        }

        `when`("fromStringOrDefault를 잘못된 문자열로 호출하면") {
            val status = UserStatus.fromStringOrDefault("INVALID")

            then("기본값이 반환된다") {
                status shouldBe UserStatus.ACTIVE
            }
        }

        `when`("fromStringOrDefault를 커스텀 기본값과 함께 호출하면") {
            val status = UserStatus.fromStringOrDefault("INVALID", UserStatus.INACTIVE)

            then("커스텀 기본값이 반환된다") {
                status shouldBe UserStatus.INACTIVE
            }
        }
    }

    given("UserStatus 속성") {
        `when`("ACTIVE 상태의 속성을 확인하면") {
            val status = UserStatus.ACTIVE

            then("올바른 displayName과 description을 가진다") {
                status.displayName shouldBe "활성"
                status.description shouldBe "정상적으로 서비스를 이용할 수 있습니다"
            }
        }

        `when`("INACTIVE 상태의 속성을 확인하면") {
            val status = UserStatus.INACTIVE

            then("올바른 displayName과 description을 가진다") {
                status.displayName shouldBe "비활성"
                status.description shouldBe "계정이 비활성화되었습니다"
            }
        }

        `when`("SUSPENDED 상태의 속성을 확인하면") {
            val status = UserStatus.SUSPENDED

            then("올바른 displayName과 description을 가진다") {
                status.displayName shouldBe "정지"
                status.description shouldBe "계정이 정지되었습니다"
            }
        }
    }
})

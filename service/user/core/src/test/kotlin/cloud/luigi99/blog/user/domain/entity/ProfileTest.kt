package cloud.luigi99.blog.user.domain.entity

import cloud.luigi99.blog.user.domain.vo.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class ProfileTest : BehaviorSpec({

    val userId = UserId.generate()

    given("Profile 생성") {
        `when`("유효한 정보로 생성하면") {
            val profile = Profile.create(
                userId = userId,
                nickname = "테스트유저",
                bio = "안녕하세요",
                profileImageUrl = "https://example.com/image.jpg"
            )

            then("Profile이 생성된다") {
                profile shouldNotBe null
                profile.entityId shouldBe userId
                profile.nickname shouldBe "테스트유저"
                profile.bio shouldBe "안녕하세요"
                profile.profileImageUrl shouldBe "https://example.com/image.jpg"
            }
        }

        `when`("bio와 profileImageUrl 없이 생성하면") {
            val profile = Profile.create(
                userId = userId,
                nickname = "테스트유저"
            )

            then("Profile이 생성된다") {
                profile shouldNotBe null
                profile.nickname shouldBe "테스트유저"
                profile.bio shouldBe null
                profile.profileImageUrl shouldBe null
            }
        }

        `when`("너무 짧은 닉네임으로 생성하려고 하면") {
            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Profile.create(userId, "a")
                }
            }
        }

        `when`("너무 긴 닉네임으로 생성하려고 하면") {
            val longNickname = "a".repeat(21)

            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Profile.create(userId, longNickname)
                }
            }
        }

        `when`("빈 닉네임으로 생성하려고 하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    Profile.create(userId, "")
                }
                exception.message shouldContain "필수값"
            }
        }

        `when`("너무 긴 자기소개로 생성하려고 하면") {
            val longBio = "a".repeat(501)

            then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Profile.create(userId, "테스트유저", longBio)
                }
            }
        }
    }

    given("Profile 업데이트") {
        val profile = Profile.create(
            userId = userId,
            nickname = "기존닉네임",
            bio = "기존소개"
        )

        `when`("프로필을 업데이트하면") {
            val updatedProfile = profile.update(
                nickname = "새닉네임",
                bio = "새소개",
                profileImageUrl = "https://example.com/new.jpg"
            )

            then("업데이트된 Profile이 반환된다") {
                updatedProfile.nickname shouldBe "새닉네임"
                updatedProfile.bio shouldBe "새소개"
                updatedProfile.profileImageUrl shouldBe "https://example.com/new.jpg"
                updatedProfile.updatedAt shouldNotBe null
            }
        }

        `when`("일부 필드만 업데이트하면") {
            val updatedProfile = profile.update(nickname = "새닉네임")

            then("해당 필드만 변경된다") {
                updatedProfile.nickname shouldBe "새닉네임"
                updatedProfile.bio shouldBe "기존소개"
            }
        }

        `when`("프로필 이미지를 업데이트하면") {
            val updatedProfile = profile.updateProfileImage("https://example.com/new.jpg")

            then("프로필 이미지가 변경된다") {
                updatedProfile.profileImageUrl shouldBe "https://example.com/new.jpg"
                updatedProfile.updatedAt shouldNotBe null
            }
        }

        `when`("프로필 이미지를 제거하면") {
            val profileWithImage = profile.copy(profileImageUrl = "https://example.com/image.jpg")
            val updatedProfile = profileWithImage.removeProfileImage()

            then("프로필 이미지가 null이 된다") {
                updatedProfile.profileImageUrl shouldBe null
                updatedProfile.updatedAt shouldNotBe null
            }
        }
    }

    given("Profile 완성도 확인") {
        `when`("닉네임과 자기소개가 모두 있으면") {
            val profile = Profile.create(
                userId = userId,
                nickname = "테스트유저",
                bio = "안녕하세요"
            )

            then("완성된 프로필로 판단된다") {
                profile.isComplete() shouldBe true
            }
        }

        `when`("자기소개가 없으면") {
            val profile = Profile.create(
                userId = userId,
                nickname = "테스트유저"
            )

            then("완성되지 않은 프로필로 판단된다") {
                profile.isComplete() shouldBe false
            }
        }

        `when`("자기소개가 빈 문자열이면") {
            val profile = Profile.create(
                userId = userId,
                nickname = "테스트유저",
                bio = ""
            )

            then("완성되지 않은 프로필로 판단된다") {
                profile.isComplete() shouldBe false
            }
        }
    }
})

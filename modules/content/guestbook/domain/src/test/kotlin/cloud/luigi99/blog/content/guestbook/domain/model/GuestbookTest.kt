package cloud.luigi99.blog.content.guestbook.domain.model

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockkObject
import java.time.LocalDateTime

class GuestbookTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns io.mockk.mockk(relaxed = true)
        }

        Given("회원이 새 방명록 글을 작성하려 할 때") {
            val authorId = MemberId.generate()
            val content = GuestbookContent("안녕하세요! 블로그 잘 보고 있습니다.")

            When("방명록을 생성하면") {
                val guestbook = Guestbook.create(authorId, content)

                Then("고유 식별자가 부여된다") {
                    guestbook.entityId shouldNotBe null
                }

                Then("입력한 내용이 저장된다") {
                    guestbook.content shouldBe content
                }

                Then("작성자 정보가 저장된다") {
                    guestbook.authorId shouldBe authorId
                }
            }
        }

        Given("기존 방명록 작성자가 내용을 수정하려 할 때") {
            val authorId = MemberId.generate()
            val originalContent = GuestbookContent("원래 내용입니다.")
            val guestbook = Guestbook.create(authorId, originalContent)

            When("새로운 내용으로 수정하면") {
                val newContent = GuestbookContent("수정된 내용입니다.")
                val updatedGuestbook = guestbook.updateContent(newContent)

                Then("원본 방명록은 변경되지 않고 보존된다") {
                    guestbook.content shouldBe originalContent
                }

                Then("수정된 내용을 가진 새 인스턴스가 반환된다") {
                    updatedGuestbook.content shouldBe newContent
                }

                Then("식별자는 동일하게 유지된다") {
                    updatedGuestbook.entityId shouldBe guestbook.entityId
                }
            }
        }

        Given("데이터베이스에 저장된 방명록을 불러왔을 때") {
            val guestbookId = GuestbookId.generate()
            val authorId = MemberId.generate()
            val content = GuestbookContent("저장된 방명록 내용")
            val createdAt = LocalDateTime.now().minusDays(3)
            val updatedAt = LocalDateTime.now().minusDays(1)

            When("도메인 객체로 재구성하면") {
                val guestbook =
                    Guestbook.from(
                        entityId = guestbookId,
                        authorId = authorId,
                        content = content,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                    )

                Then("모든 속성이 저장된 상태 그대로 복원된다") {
                    guestbook.entityId shouldBe guestbookId
                    guestbook.authorId shouldBe authorId
                    guestbook.content shouldBe content
                    guestbook.createdAt shouldBe createdAt
                    guestbook.updatedAt shouldBe updatedAt
                }
            }
        }

        Given("방명록 작성자가 자신의 방명록인지 확인하려 할 때") {
            val authorId = MemberId.generate()
            val otherMemberId = MemberId.generate()
            val guestbook = Guestbook.create(authorId, GuestbookContent("테스트"))

            When("본인 ID로 확인하면") {
                val isOwner = guestbook.isOwnedBy(authorId)

                Then("소유자임이 확인된다") {
                    isOwner shouldBe true
                }
            }

            When("다른 회원 ID로 확인하면") {
                val isOwner = guestbook.isOwnedBy(otherMemberId)

                Then("소유자가 아님이 확인된다") {
                    isOwner shouldBe false
                }
            }
        }
    })

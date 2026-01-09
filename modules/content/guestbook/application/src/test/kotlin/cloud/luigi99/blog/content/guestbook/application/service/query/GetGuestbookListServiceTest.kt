package cloud.luigi99.blog.content.guestbook.application.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class GetGuestbookListServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("방명록 목록을 조회하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val memberQueryPort = mockk<MemberQueryPort>()
            val service = GetGuestbookListService(guestbookRepository, memberQueryPort)

            val memberId1 = MemberId.generate()
            val memberId2 = MemberId.generate()

            When("방명록이 존재하면") {
                val guestbooks =
                    listOf(
                        Guestbook.create(memberId1, GuestbookContent("첫 번째 방명록")),
                        Guestbook.create(memberId2, GuestbookContent("두 번째 방명록")),
                    )

                every { guestbookRepository.findAllOrderByCreatedAtDesc() } returns guestbooks
                every { memberQueryPort.getAuthors(any()) } returns
                    mapOf(
                        memberId1.value.toString() to
                            MemberQueryPort.Author(
                                memberId = memberId1.value.toString(),
                                nickname = "User1",
                                profileImageUrl = null,
                                username = "user1",
                            ),
                        memberId2.value.toString() to
                            MemberQueryPort.Author(
                                memberId = memberId2.value.toString(),
                                nickname = "User2",
                                profileImageUrl = null,
                                username = "user2",
                            ),
                    )

                Then("모든 방명록과 작성자 정보가 반환된다") {
                    val responses = service.execute()

                    responses.size shouldBe 2
                    responses[0].author.nickname shouldBe "User1"
                    responses[1].author.nickname shouldBe "User2"
                }
            }

            When("방명록이 없으면") {
                every { guestbookRepository.findAllOrderByCreatedAtDesc() } returns emptyList()

                Then("빈 목록이 반환된다") {
                    val responses = service.execute()
                    responses shouldBe emptyList()
                }
            }
        }
    })

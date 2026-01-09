package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.CreateGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class CreateGuestbookServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("회원이 방명록을 작성하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val memberQueryPort = mockk<MemberQueryPort>()
            val service = CreateGuestbookService(guestbookRepository, memberQueryPort)

            val command =
                CreateGuestbookUseCase.Command(
                    authorId = "123e4567-e89b-12d3-a456-426614174000",
                    content = "안녕하세요! 블로그 잘 보고 있습니다.",
                )

            every { guestbookRepository.save(any()) } answers {
                firstArg<Guestbook>()
            }
            every { memberQueryPort.getAuthor(command.authorId) } returns
                MemberQueryPort.Author(
                    memberId = command.authorId,
                    nickname = "TestUser",
                    profileImageUrl = "https://example.com/profile.jpg",
                    username = "testuser",
                )

            When("방명록 작성을 실행하면") {
                Then("방명록 ID, 작성자 정보, 내용이 반환된다") {
                    val response = service.execute(command)

                    response.guestbookId shouldNotBe null
                    response.author.memberId shouldBe command.authorId
                    response.author.nickname shouldBe "TestUser"
                    response.content shouldBe command.content
                }
            }
        }
    })

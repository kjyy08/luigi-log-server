package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.ModifyGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.exception.UnauthorizedGuestbookAccessException
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class ModifyGuestbookServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("작성자가 자신의 방명록을 수정하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = ModifyGuestbookService(guestbookRepository)

            val authorId = MemberId.generate()
            val guestbook = Guestbook.create(authorId, GuestbookContent("원래 내용"))

            val command =
                ModifyGuestbookUseCase.Command(
                    guestbookId =
                        guestbook.entityId.value
                            .toString(),
                    requesterId = authorId.value.toString(),
                    content = "수정된 내용입니다.",
                )

            When("수정을 처리하면") {
                every { guestbookRepository.findById(guestbook.entityId) } returns guestbook
                every { guestbookRepository.save(any()) } answers { firstArg<Guestbook>() }

                Then("수정된 내용이 저장되고 반환된다") {
                    val response = service.execute(command)

                    verify(exactly = 1) { guestbookRepository.save(any()) }
                    response.content shouldBe command.content
                }
            }
        }

        Given("다른 회원이 방명록을 수정하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = ModifyGuestbookService(guestbookRepository)

            val authorId = MemberId.generate()
            val otherId = MemberId.generate()
            val guestbook = Guestbook.create(authorId, GuestbookContent("원래 내용"))

            val command =
                ModifyGuestbookUseCase.Command(
                    guestbookId =
                        guestbook.entityId.value
                            .toString(),
                    requesterId = otherId.value.toString(),
                    content = "수정하려는 내용",
                )

            When("수정을 시도하면") {
                every { guestbookRepository.findById(guestbook.entityId) } returns guestbook

                Then("권한 없음 예외가 발생한다") {
                    shouldThrow<UnauthorizedGuestbookAccessException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("존재하지 않는 방명록을 수정하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = ModifyGuestbookService(guestbookRepository)

            val nonExistentId = GuestbookId.generate()
            val command =
                ModifyGuestbookUseCase.Command(
                    guestbookId = nonExistentId.value.toString(),
                    requesterId =
                        MemberId
                            .generate()
                            .value
                            .toString(),
                    content = "수정하려는 내용",
                )

            When("수정을 시도하면") {
                every { guestbookRepository.findById(nonExistentId) } returns null

                Then("방명록을 찾을 수 없음 예외가 발생한다") {
                    shouldThrow<GuestbookNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }
    })

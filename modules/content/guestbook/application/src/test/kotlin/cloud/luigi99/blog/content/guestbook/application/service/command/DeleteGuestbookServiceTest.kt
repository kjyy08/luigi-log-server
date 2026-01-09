package cloud.luigi99.blog.content.guestbook.application.service.command

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.DeleteGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.exception.UnauthorizedGuestbookAccessException
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify

class DeleteGuestbookServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("작성자가 자신의 방명록을 삭제하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = DeleteGuestbookService(guestbookRepository)

            val authorId = MemberId.generate()
            val guestbook = Guestbook.create(authorId, GuestbookContent("삭제할 방명록"))

            val command =
                DeleteGuestbookUseCase.Command(
                    guestbookId =
                        guestbook.entityId.value
                            .toString(),
                    requesterId = authorId.value.toString(),
                )

            When("삭제를 처리하면") {
                every { guestbookRepository.findById(guestbook.entityId) } returns guestbook
                every { guestbookRepository.deleteById(guestbook.entityId) } just Runs

                Then("방명록이 저장소에서 삭제된다") {
                    service.execute(command)
                    verify(exactly = 1) { guestbookRepository.deleteById(guestbook.entityId) }
                }
            }
        }

        Given("다른 회원이 방명록을 삭제하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = DeleteGuestbookService(guestbookRepository)

            val authorId = MemberId.generate()
            val otherId = MemberId.generate()
            val guestbook = Guestbook.create(authorId, GuestbookContent("삭제 대상"))

            val command =
                DeleteGuestbookUseCase.Command(
                    guestbookId =
                        guestbook.entityId.value
                            .toString(),
                    requesterId = otherId.value.toString(),
                )

            When("삭제를 시도하면") {
                every { guestbookRepository.findById(guestbook.entityId) } returns guestbook

                Then("권한 없음 예외가 발생한다") {
                    shouldThrow<UnauthorizedGuestbookAccessException> {
                        service.execute(command)
                    }
                }
            }
        }

        Given("존재하지 않는 방명록을 삭제하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val service = DeleteGuestbookService(guestbookRepository)

            val nonExistentId = GuestbookId.generate()
            val command =
                DeleteGuestbookUseCase.Command(
                    guestbookId = nonExistentId.value.toString(),
                    requesterId =
                        MemberId
                            .generate()
                            .value
                            .toString(),
                )

            When("삭제를 시도하면") {
                every { guestbookRepository.findById(nonExistentId) } returns null

                Then("방명록을 찾을 수 없음 예외가 발생한다") {
                    shouldThrow<GuestbookNotFoundException> {
                        service.execute(command)
                    }
                }
            }
        }
    })

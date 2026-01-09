package cloud.luigi99.blog.content.guestbook.application.service.query

import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.out.GuestbookRepository
import cloud.luigi99.blog.content.guestbook.application.port.out.MemberQueryPort
import cloud.luigi99.blog.content.guestbook.domain.exception.GuestbookNotFoundException
import cloud.luigi99.blog.content.guestbook.domain.model.Guestbook
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookContent
import cloud.luigi99.blog.content.guestbook.domain.vo.GuestbookId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GetGuestbookServiceTest :
    BehaviorSpec({

        Given("특정 방명록을 조회하려 할 때") {
            val guestbookRepository = mockk<GuestbookRepository>()
            val memberQueryPort = mockk<MemberQueryPort>()
            val service = GetGuestbookService(guestbookRepository, memberQueryPort)

            val guestbookId = GuestbookId.generate()
            val memberId = MemberId.generate()
            val guestbook =
                Guestbook.from(
                    entityId = guestbookId,
                    authorId = memberId,
                    content = GuestbookContent("테스트 방명록 내용"),
                    createdAt = null,
                    updatedAt = null,
                )

            When("해당 방명록이 존재하면") {
                every { guestbookRepository.findById(guestbookId) } returns guestbook
                every { memberQueryPort.getAuthor(memberId.value.toString()) } returns
                    MemberQueryPort.Author(
                        memberId = memberId.value.toString(),
                        nickname = "TestUser",
                        profileImageUrl = null,
                        username = "testuser",
                    )

                Then("방명록 정보와 작성자 정보가 반환된다") {
                    val query = GetGuestbookUseCase.Query(guestbookId.value.toString())
                    val response = service.execute(query)

                    response.guestbookId shouldBe guestbookId.value.toString()
                    response.content shouldBe "테스트 방명록 내용"
                    response.author.memberId shouldBe memberId.value.toString()
                    response.author.nickname shouldBe "TestUser"
                }
            }

            When("해당 방명록이 존재하지 않으면") {
                val notExistId = GuestbookId.generate()
                every { guestbookRepository.findById(notExistId) } returns null

                Then("GuestbookNotFoundException이 발생한다") {
                    val query = GetGuestbookUseCase.Query(notExistId.value.toString())
                    shouldThrow<GuestbookNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }
    })

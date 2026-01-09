package cloud.luigi99.blog.content.guestbook.adapter.`in`.web

import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.CreateGuestbookRequest
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.ModifyGuestbookRequest
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.CreateGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.DeleteGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.GuestbookCommandFacade
import cloud.luigi99.blog.content.guestbook.application.port.`in`.command.ModifyGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookListUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GetGuestbookUseCase
import cloud.luigi99.blog.content.guestbook.application.port.`in`.query.GuestbookQueryFacade
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus

class GuestbookControllerTest :
    BehaviorSpec({

        Given("로그인한 사용자가 방명록을 작성하려 할 때") {
            val guestbookCommandFacade = mockk<GuestbookCommandFacade>()
            val guestbookQueryFacade = mockk<GuestbookQueryFacade>()
            val controller = GuestbookController(guestbookCommandFacade, guestbookQueryFacade)

            val memberId = "123e4567-e89b-12d3-a456-426614174000"
            val createUseCase = mockk<CreateGuestbookUseCase>()
            val request = CreateGuestbookRequest(content = "안녕하세요!")

            val response =
                CreateGuestbookUseCase.Response(
                    guestbookId = "550e8400-e29b-41d4-a716-446655440000",
                    author =
                        CreateGuestbookUseCase.AuthorInfo(
                            memberId = memberId,
                            nickname = "TestUser",
                            profileImageUrl = null,
                            username = "testuser",
                        ),
                    content = request.content,
                )

            When("방명록 작성을 요청하면") {
                every { guestbookCommandFacade.createGuestbook() } returns createUseCase
                every { createUseCase.execute(any()) } returns response

                Then("201 Created 응답과 생성된 방명록 정보가 반환된다") {
                    val result = controller.createGuestbook(memberId, request)

                    result.statusCode shouldBe HttpStatus.CREATED
                    result.body
                        ?.data
                        ?.content shouldBe request.content
                    result.body
                        ?.data
                        ?.author
                        ?.nickname shouldBe "TestUser"
                }
            }
        }

        Given("로그인한 사용자가 자신의 방명록을 수정하려 할 때") {
            val guestbookCommandFacade = mockk<GuestbookCommandFacade>()
            val guestbookQueryFacade = mockk<GuestbookQueryFacade>()
            val controller = GuestbookController(guestbookCommandFacade, guestbookQueryFacade)

            val memberId = "123e4567-e89b-12d3-a456-426614174000"
            val guestbookId = "550e8400-e29b-41d4-a716-446655440000"
            val modifyUseCase = mockk<ModifyGuestbookUseCase>()
            val getUseCase = mockk<GetGuestbookUseCase>()
            val request = ModifyGuestbookRequest(content = "수정된 내용입니다.")

            val modifyResponse =
                ModifyGuestbookUseCase.Response(
                    guestbookId = guestbookId,
                    content = request.content,
                )

            val getResponse =
                GetGuestbookUseCase.Response(
                    guestbookId = guestbookId,
                    author =
                        GetGuestbookUseCase.AuthorInfo(
                            memberId = memberId,
                            nickname = "TestUser",
                            profileImageUrl = null,
                            username = "testuser",
                        ),
                    content = request.content,
                    createdAt = "2024-01-01T00:00:00",
                    updatedAt = "2024-01-01T00:00:00",
                )

            When("수정을 요청하면") {
                every { guestbookCommandFacade.modifyGuestbook() } returns modifyUseCase
                every { modifyUseCase.execute(any()) } returns modifyResponse
                every { guestbookQueryFacade.getGuestbook() } returns getUseCase
                every { getUseCase.execute(any()) } returns getResponse

                Then("200 OK 응답과 수정된 방명록 정보가 반환된다") {
                    val result = controller.modifyGuestbook(memberId, guestbookId, request)

                    result.statusCode shouldBe HttpStatus.OK
                    result.body
                        ?.data
                        ?.content shouldBe request.content
                }
            }
        }

        Given("로그인한 사용자가 자신의 방명록을 삭제하려 할 때") {
            val guestbookCommandFacade = mockk<GuestbookCommandFacade>()
            val guestbookQueryFacade = mockk<GuestbookQueryFacade>()
            val controller = GuestbookController(guestbookCommandFacade, guestbookQueryFacade)

            val memberId = "123e4567-e89b-12d3-a456-426614174000"
            val guestbookId = "550e8400-e29b-41d4-a716-446655440000"
            val deleteUseCase = mockk<DeleteGuestbookUseCase>()

            When("삭제를 요청하면") {
                every { guestbookCommandFacade.deleteGuestbook() } returns deleteUseCase
                every { deleteUseCase.execute(any()) } just Runs

                Then("204 No Content 응답이 반환되고 삭제가 수행된다") {
                    val result = controller.deleteGuestbook(memberId, guestbookId)

                    result.statusCode shouldBe HttpStatus.NO_CONTENT
                    verify(exactly = 1) { deleteUseCase.execute(any()) }
                }
            }
        }

        Given("방명록 목록을 조회하려 할 때") {
            val guestbookCommandFacade = mockk<GuestbookCommandFacade>()
            val guestbookQueryFacade = mockk<GuestbookQueryFacade>()
            val controller = GuestbookController(guestbookCommandFacade, guestbookQueryFacade)

            val listUseCase = mockk<GetGuestbookListUseCase>()
            val responses =
                listOf(
                    GetGuestbookListUseCase.Response(
                        guestbookId = "id1",
                        author =
                            GetGuestbookListUseCase.AuthorInfo(
                                memberId = "author1",
                                nickname = "User1",
                                profileImageUrl = null,
                                username = "user1",
                            ),
                        content = "첫 번째 방명록",
                        createdAt = "2024-01-01T00:00:00",
                        updatedAt = "2024-01-01T00:00:00",
                    ),
                    GetGuestbookListUseCase.Response(
                        guestbookId = "id2",
                        author =
                            GetGuestbookListUseCase.AuthorInfo(
                                memberId = "author2",
                                nickname = "User2",
                                profileImageUrl = null,
                                username = "user2",
                            ),
                        content = "두 번째 방명록",
                        createdAt = "2024-01-02T00:00:00",
                        updatedAt = "2024-01-02T00:00:00",
                    ),
                )

            When("목록을 조회하면") {
                every { guestbookQueryFacade.getGuestbookList() } returns listUseCase
                every { listUseCase.execute() } returns responses

                Then("200 OK 응답과 모든 방명록이 반환된다") {
                    val result = controller.getGuestbooks()

                    result.statusCode shouldBe HttpStatus.OK
                    result.body
                        ?.data
                        ?.size shouldBe 2
                }
            }
        }

        Given("특정 방명록을 조회하려 할 때") {
            val guestbookCommandFacade = mockk<GuestbookCommandFacade>()
            val guestbookQueryFacade = mockk<GuestbookQueryFacade>()
            val controller = GuestbookController(guestbookCommandFacade, guestbookQueryFacade)

            val guestbookId = "550e8400-e29b-41d4-a716-446655440000"
            val getUseCase = mockk<GetGuestbookUseCase>()
            val response =
                GetGuestbookUseCase.Response(
                    guestbookId = guestbookId,
                    author =
                        GetGuestbookUseCase.AuthorInfo(
                            memberId = "author1",
                            nickname = "TestUser",
                            profileImageUrl = null,
                            username = "testuser",
                        ),
                    content = "조회할 방명록",
                    createdAt = "2024-01-01T00:00:00",
                    updatedAt = "2024-01-01T00:00:00",
                )

            When("조회하면") {
                every { guestbookQueryFacade.getGuestbook() } returns getUseCase
                every { getUseCase.execute(any()) } returns response

                Then("200 OK 응답과 해당 방명록 정보가 반환된다") {
                    val result = controller.getGuestbook(guestbookId)

                    result.statusCode shouldBe HttpStatus.OK
                    result.body
                        ?.data
                        ?.guestbookId shouldBe guestbookId
                    result.body
                        ?.data
                        ?.author
                        ?.nickname shouldBe "TestUser"
                }
            }
        }
    })

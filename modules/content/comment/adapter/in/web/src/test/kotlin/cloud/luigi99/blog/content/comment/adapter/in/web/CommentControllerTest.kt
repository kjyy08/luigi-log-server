package cloud.luigi99.blog.content.comment.adapter.`in`.web

import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.CreateCommentRequest
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.UpdateCommentRequest
import cloud.luigi99.blog.content.comment.application.port.`in`.command.CommentCommandFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.command.CreateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.DeleteCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.UpdateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.query.CommentQueryFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.query.GetCommentListUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.UUID

class CommentControllerTest :
    BehaviorSpec({

        Given("사용자가 댓글을 작성하려고 할 때") {
            val commandFacade = mockk<CommentCommandFacade>()
            val queryFacade = mockk<CommentQueryFacade>()
            val controller = CommentController(commandFacade, queryFacade)

            val authorId = UUID.randomUUID().toString()
            val postId = UUID.randomUUID().toString()
            val request =
                CreateCommentRequest(
                    postId = postId,
                    content = "좋은 글 감사합니다!",
                )

            val createUseCase = mockk<CreateCommentUseCase>()
            val response =
                CreateCommentUseCase.Response(
                    commentId = UUID.randomUUID().toString(),
                    postId = postId,
                    author =
                        CreateCommentUseCase.AuthorInfo(
                            memberId = authorId,
                            nickname = "Luigi99",
                            profileImageUrl = null,
                            username = "luigi99",
                        ),
                    content = "좋은 글 감사합니다!",
                    createdAt = "2024-01-01T00:00:00",
                )

            When("댓글 작성을 요청하면") {
                every { commandFacade.createComment() } returns createUseCase
                every { createUseCase.execute(any()) } returns response

                val result = controller.createComment(authorId, request)

                Then("UseCase가 실행된다") {
                    verify(exactly = 1) { createUseCase.execute(any()) }
                }

                Then("성공 응답이 반환된다") {
                    result.statusCode shouldBe HttpStatus.OK
                    result.body!!
                        .data!!
                        .commentId shouldBe response.commentId
                    result.body!!
                        .data!!
                        .content shouldBe "좋은 글 감사합니다!"
                    result.body!!
                        .data!!
                        .author.nickname shouldBe "Luigi99"
                }
            }
        }

        Given("사용자가 댓글을 수정하려고 할 때") {
            val commandFacade = mockk<CommentCommandFacade>()
            val queryFacade = mockk<CommentQueryFacade>()
            val controller = CommentController(commandFacade, queryFacade)

            val authorId = UUID.randomUUID().toString()
            val commentId = UUID.randomUUID().toString()
            val request = UpdateCommentRequest(content = "수정된 댓글입니다.")

            val updateUseCase = mockk<UpdateCommentUseCase>()
            val response =
                UpdateCommentUseCase.Response(
                    commentId = commentId,
                    author =
                        UpdateCommentUseCase.AuthorInfo(
                            memberId = authorId,
                            nickname = "Luigi99",
                            profileImageUrl = null,
                            username = "luigi99",
                        ),
                    content = "수정된 댓글입니다.",
                    updatedAt = "2024-01-01T00:00:00",
                )

            When("댓글 수정을 요청하면") {
                every { commandFacade.updateComment() } returns updateUseCase
                every { updateUseCase.execute(any()) } returns response

                val result = controller.updateComment(authorId, commentId, request)

                Then("UseCase가 실행된다") {
                    verify(exactly = 1) { updateUseCase.execute(any()) }
                }

                Then("성공 응답이 반환된다") {
                    result.statusCode shouldBe HttpStatus.OK
                    result.body!!
                        .data!!
                        .content shouldBe "수정된 댓글입니다."
                    result.body!!
                        .data!!
                        .author.nickname shouldBe "Luigi99"
                }
            }
        }

        Given("사용자가 댓글을 삭제하려고 할 때") {
            val commandFacade = mockk<CommentCommandFacade>()
            val queryFacade = mockk<CommentQueryFacade>()
            val controller = CommentController(commandFacade, queryFacade)

            val authorId = UUID.randomUUID().toString()
            val commentId = UUID.randomUUID().toString()

            val deleteUseCase = mockk<DeleteCommentUseCase>()

            When("댓글 삭제를 요청하면") {
                every { commandFacade.deleteComment() } returns deleteUseCase
                every { deleteUseCase.execute(any()) } returns Unit

                val result = controller.deleteComment(authorId, commentId)

                Then("UseCase가 실행된다") {
                    verify(exactly = 1) { deleteUseCase.execute(any()) }
                }

                Then("No Content 응답이 반환된다") {
                    result.statusCode shouldBe HttpStatus.NO_CONTENT
                }
            }
        }

        Given("댓글 목록을 조회하려고 할 때") {
            val commandFacade = mockk<CommentCommandFacade>()
            val queryFacade = mockk<CommentQueryFacade>()
            val controller = CommentController(commandFacade, queryFacade)

            val postId = UUID.randomUUID().toString()
            val authorId1 = UUID.randomUUID().toString()
            val authorId2 = UUID.randomUUID().toString()

            val getListUseCase = mockk<GetCommentListUseCase>()
            val response =
                GetCommentListUseCase.Response(
                    comments =
                        listOf(
                            GetCommentListUseCase.CommentInfo(
                                commentId = UUID.randomUUID().toString(),
                                author =
                                    GetCommentListUseCase.AuthorInfo(
                                        memberId = authorId1,
                                        nickname = "Luigi99",
                                        profileImageUrl = null,
                                        username = "luigi99",
                                    ),
                                content = "첫 번째 댓글",
                                createdAt = "2024-01-01T00:00:00",
                                updatedAt = "2024-01-01T00:00:00",
                            ),
                            GetCommentListUseCase.CommentInfo(
                                commentId = UUID.randomUUID().toString(),
                                author =
                                    GetCommentListUseCase.AuthorInfo(
                                        memberId = authorId2,
                                        nickname = "코딩왕",
                                        profileImageUrl = null,
                                        username = "codingking",
                                    ),
                                content = "두 번째 댓글",
                                createdAt = "2024-01-01T00:00:00",
                                updatedAt = "2024-01-01T00:00:00",
                            ),
                        ),
                )

            When("댓글 목록 조회를 요청하면") {
                every { queryFacade.getCommentList() } returns getListUseCase
                every { getListUseCase.execute(any()) } returns response

                val result = controller.getCommentList(postId)

                Then("UseCase가 실행된다") {
                    verify(exactly = 1) { getListUseCase.execute(any()) }
                }

                Then("댓글 목록이 반환된다") {
                    result.statusCode shouldBe HttpStatus.OK
                    result.body!!
                        .data!!
                        .size shouldBe 2
                    result.body!!
                        .data!![0]
                        .content shouldBe "첫 번째 댓글"
                    result.body!!
                        .data!![0]
                        .author.nickname shouldBe "Luigi99"
                    result.body!!
                        .data!![1]
                        .content shouldBe "두 번째 댓글"
                    result.body!!
                        .data!![1]
                        .author.nickname shouldBe "코딩왕"
                }
            }
        }
    })

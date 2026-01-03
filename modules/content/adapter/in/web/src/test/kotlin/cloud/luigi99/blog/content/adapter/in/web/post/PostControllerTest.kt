package cloud.luigi99.blog.content.adapter.`in`.web.post

import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.CreatePostRequest
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.UpdatePostRequest
import cloud.luigi99.blog.content.application.post.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.command.DeletePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.command.PostCommandFacade
import cloud.luigi99.blog.content.application.post.port.`in`.command.UpdatePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.PostQueryFacade
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.UUID

/**
 * PostController 테스트
 *
 * REST API 엔드포인트의 요청/응답 처리를 검증합니다.
 */
class PostControllerTest :
    BehaviorSpec({

        val postQueryFacade = mockk<PostQueryFacade>()
        val postCommandFacade = mockk<PostCommandFacade>()
        val controller = PostController(postQueryFacade, postCommandFacade)

        Given("블로그 글을 생성할 때") {
            val createPostUseCase = mockk<CreatePostUseCase>()
            every { postCommandFacade.createPost() } returns createPostUseCase

            When("유효한 요청으로 글을 생성하면") {
                val request =
                    CreatePostRequest(
                        title = "Kotlin DDD",
                        slug = "kotlin-ddd",
                        body = "# Content",
                        type = "BLOG",
                        tags = listOf("Kotlin", "DDD"),
                    )

                val expectedResponse =
                    CreatePostUseCase.Response(
                        postId = UUID.randomUUID().toString(),
                        author = CreatePostUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "Kotlin DDD",
                        slug = "kotlin-ddd",
                        body = "# Content",
                        type = "BLOG",
                        status = "DRAFT",
                        tags = setOf("Kotlin", "DDD"),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { createPostUseCase.execute(any()) } returns expectedResponse

                val response = controller.createPost("member-id", request)

                Then("201 Created 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.CREATED
                }

                Then("생성된 글 정보가 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.title shouldBe "Kotlin DDD"
                    response.body
                        ?.data
                        ?.status shouldBe "DRAFT"
                    response.body
                        ?.data
                        ?.author
                        ?.nickname shouldBe "Luigi"
                }
            }
        }

        Given("글의 제목만 수정할 때") {
            val updatePostUseCase = mockk<UpdatePostUseCase>()
            every { postCommandFacade.updatePost() } returns updatePostUseCase

            When("title만 포함된 요청을 보내면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = "수정된 제목", body = null, status = null)

                val expectedResponse =
                    UpdatePostUseCase.Response(
                        postId = postId,
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "수정된 제목",
                        slug = "original-slug",
                        body = "원본 본문",
                        type = "BLOG",
                        status = "DRAFT",
                        tags = emptySet(),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { updatePostUseCase.execute(any()) } returns expectedResponse

                val response = controller.updatePost("member-id", postId, request)

                Then("200 OK 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("제목만 변경된 글이 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.title shouldBe "수정된 제목"
                    response.body
                        ?.data
                        ?.body shouldBe "원본 본문"
                }
            }
        }

        Given("글의 상태만 PUBLISHED로 변경할 때") {
            val updatePostUseCase = mockk<UpdatePostUseCase>()
            every { postCommandFacade.updatePost() } returns updatePostUseCase

            When("status만 포함된 요청을 보내면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = null, body = null, status = "PUBLISHED")

                val expectedResponse =
                    UpdatePostUseCase.Response(
                        postId = postId,
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "원본 제목",
                        slug = "original-slug",
                        body = "원본 본문",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = emptySet(),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { updatePostUseCase.execute(any()) } returns expectedResponse

                val response = controller.updatePost("member-id", postId, request)

                Then("200 OK 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("상태만 PUBLISHED로 변경된 글이 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.status shouldBe "PUBLISHED"
                    response.body
                        ?.data
                        ?.title shouldBe "원본 제목"
                }
            }
        }

        Given("글의 제목, 본문, 상태를 모두 수정할 때") {
            val updatePostUseCase = mockk<UpdatePostUseCase>()
            every { postCommandFacade.updatePost() } returns updatePostUseCase

            When("모든 필드가 포함된 요청을 보내면") {
                val postId = UUID.randomUUID().toString()
                val request =
                    UpdatePostRequest(
                        title = "완전히 수정된 제목",
                        body = "완전히 수정된 본문",
                        status = "PUBLISHED",
                    )

                val expectedResponse =
                    UpdatePostUseCase.Response(
                        postId = postId,
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "완전히 수정된 제목",
                        slug = "original-slug",
                        body = "완전히 수정된 본문",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = emptySet(),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { updatePostUseCase.execute(any()) } returns expectedResponse

                val response = controller.updatePost("member-id", postId, request)

                Then("200 OK 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("모든 필드가 변경된 글이 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.title shouldBe "완전히 수정된 제목"
                    response.body
                        ?.data
                        ?.body shouldBe "완전히 수정된 본문"
                    response.body
                        ?.data
                        ?.status shouldBe "PUBLISHED"
                }
            }
        }

        Given("글을 ID로 조회할 때") {
            val getPostByIdUseCase = mockk<GetPostByIdUseCase>()
            every { postQueryFacade.getPostById() } returns getPostByIdUseCase

            When("유효한 Post ID로 조회하면") {
                val postId = UUID.randomUUID().toString()

                val expectedResponse =
                    GetPostByIdUseCase.Response(
                        postId = postId,
                        author = GetPostByIdUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "조회된 글",
                        slug = "retrieved-post",
                        body = "본문 내용",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = setOf("Tag1"),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { getPostByIdUseCase.execute(any()) } returns expectedResponse

                val response = controller.getPostById(postId)

                Then("200 OK 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("조회된 글 정보가 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.postId shouldBe postId
                    response.body
                        ?.data
                        ?.title shouldBe "조회된 글"
                    response.body
                        ?.data
                        ?.author
                        ?.nickname shouldBe "Luigi"
                }
            }
        }

        Given("Username과 Slug로 글을 조회할 때") {
            val getPostBySlugUseCase = mockk<GetPostBySlugUseCase>()
            every { postQueryFacade.getPostBySlug() } returns getPostBySlugUseCase

            When("유효한 Username과 Slug로 조회하면") {
                val username = "testuser"
                val slug = "test-post"

                val expectedResponse =
                    GetPostBySlugUseCase.Response(
                        postId = UUID.randomUUID().toString(),
                        author = GetPostBySlugUseCase.AuthorInfo("member-id", "Luigi", null),
                        title = "테스트 글",
                        slug = slug,
                        body = "본문 내용",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = setOf("Tag1"),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { getPostBySlugUseCase.execute(any()) } returns expectedResponse

                val response = controller.getPostByUsernameAndSlug(username, slug)

                Then("200 OK 응답이 반환되어야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("조회된 글 정보가 반환되어야 한다") {
                    response.body shouldNotBe null
                    response.body?.success shouldBe true
                    response.body
                        ?.data
                        ?.slug shouldBe slug
                    response.body
                        ?.data
                        ?.title shouldBe "테스트 글"
                }
            }
        }

        Given("작성자가 자신의 글을 삭제하려는 상황에서") {
            val deletePostUseCase = mockk<DeletePostUseCase>()
            every { postCommandFacade.deletePost() } returns deletePostUseCase

            When("게시글 삭제 요청을 전송하면") {
                val postId = UUID.randomUUID().toString()
                val memberId = "member-id"

                every { deletePostUseCase.execute(any()) } just Runs

                val response = controller.deletePost(memberId, postId)

                Then("요청이 성공적으로 처리되어 204 No Content 응답이 반환된다") {
                    response.statusCode shouldBe HttpStatus.NO_CONTENT
                }

                Then("응답 본문은 비어있다") {
                    response.body shouldBe null
                }
            }
        }
    })

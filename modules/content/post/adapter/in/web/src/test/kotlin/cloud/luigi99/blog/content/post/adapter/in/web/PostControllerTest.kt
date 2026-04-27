package cloud.luigi99.blog.content.post.adapter.`in`.web

import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.CreatePostRequest
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.UpdatePostRequest
import cloud.luigi99.blog.content.post.application.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.command.DeletePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.command.PostCommandFacade
import cloud.luigi99.blog.content.post.application.port.`in`.command.UpdatePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.PostQueryFacade
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
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

        beforeEach {
            setAuthentication(authorities = listOf("ROLE_ADMIN"))
        }

        afterEach {
            SecurityContextHolder.clearContext()
        }

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
                        author = CreatePostUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
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
                        ?.author
                        ?.nickname shouldBe "Luigi"
                    response.body
                        ?.data
                        ?.author
                        ?.username shouldBe "test-user"
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
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
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

                Then("200 OK 응답이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("제목만 변경된 글이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

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
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
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

                Then("200 OK 응답이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("상태만 PUBLISHED로 변경된 글이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

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
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
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

                Then("200 OK 응답이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

                    response.statusCode shouldBe HttpStatus.OK
                }

                Then("모든 필드가 변경된 글이 반환되어야 한다") {
                    setAuthentication(authorities = listOf("ROLE_ADMIN"))
                    val response = controller.updatePost("member-id", postId, request)

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

        Given("API key 권한으로 글을 수정할 때") {
            val updatePostUseCase = mockk<UpdatePostUseCase>()
            every { postCommandFacade.updatePost() } returns updatePostUseCase

            When("post:update scope만 가진 API key가 status를 PUBLISHED로 변경하면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = null, body = null, status = "PUBLISHED")
                Then("접근이 거부되고 수정 UseCase는 실행되지 않아야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:update"))

                    shouldThrow<AccessDeniedException> {
                        controller.updatePost("member-id", postId, request)
                    }
                }
            }

            When("post:update scope만 가진 API key가 status를 ARCHIVED로 변경하면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = null, body = null, status = "ARCHIVED")
                Then("접근이 거부되고 수정 UseCase는 실행되지 않아야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:update"))

                    shouldThrow<AccessDeniedException> {
                        controller.updatePost("member-id", postId, request)
                    }
                }
            }

            When("post:publish scope만 가진 API key가 title을 변경하면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = "수정된 제목", body = null, status = null)
                Then("접근이 거부되고 수정 UseCase는 실행되지 않아야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:publish"))

                    shouldThrow<AccessDeniedException> {
                        controller.updatePost("member-id", postId, request)
                    }
                }
            }

            When("post:publish scope만 가진 API key가 body를 변경하면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = null, body = "수정된 본문", status = null)
                Then("접근이 거부되고 수정 UseCase는 실행되지 않아야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:publish"))

                    shouldThrow<AccessDeniedException> {
                        controller.updatePost("member-id", postId, request)
                    }
                }
            }

            When("title과 status를 같이 바꾸면서 한 scope만 있으면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = "수정된 제목", body = null, status = "PUBLISHED")
                Then("두 scope 중 누락된 권한 때문에 접근이 거부되어야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:update"))

                    shouldThrow<AccessDeniedException> {
                        controller.updatePost("member-id", postId, request)
                    }
                }
            }

            When("title과 status를 같이 바꾸면서 두 scope를 모두 가지면") {
                val postId = UUID.randomUUID().toString()
                val request = UpdatePostRequest(title = "수정된 제목", body = null, status = "PUBLISHED")
                val expectedResponse =
                    UpdatePostUseCase.Response(
                        postId = postId,
                        author = UpdatePostUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
                        title = "수정된 제목",
                        slug = "original-slug",
                        body = "원본 본문",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = emptySet(),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )
                every { updatePostUseCase.execute(any()) } returns expectedResponse

                Then("수정 요청이 허용되어야 한다") {
                    setAuthentication(authorities = listOf("SCOPE_post:update", "SCOPE_post:publish"))
                    val response = controller.updatePost("member-id", postId, request)

                    response.statusCode shouldBe HttpStatus.OK
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
                        author = GetPostByIdUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
                        title = "조회된 글",
                        slug = "retrieved-post",
                        body = "본문 내용",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = setOf("Tag1"),
                        viewCount = 0,
                        commentCount = 0,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { getPostByIdUseCase.execute(any()) } returns expectedResponse
                val request = mockVisitorRequest()

                val response = controller.getPostById(postId, request)

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
                        author = GetPostBySlugUseCase.AuthorInfo("member-id", "Luigi", null, "test-user"),
                        title = "테스트 글",
                        slug = slug,
                        body = "본문 내용",
                        type = "BLOG",
                        status = "PUBLISHED",
                        tags = setOf("Tag1"),
                        viewCount = 0,
                        commentCount = 0,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )

                every { getPostBySlugUseCase.execute(any()) } returns expectedResponse
                val request = mockVisitorRequest()

                val response = controller.getPostByUsernameAndSlug(username, slug, request)

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

        Given("글 목록을 태그로 조회할 때") {
            val getPostsUseCase = mockk<GetPostsUseCase>()
            every { postQueryFacade.getPosts() } returns getPostsUseCase

            When("q와 tag query parameter가 함께 전달되면") {
                every { getPostsUseCase.execute(any()) } returns
                    GetPostsUseCase.Response(
                        posts = emptyList(),
                        pageInfo = GetPostsUseCase.PageInfo(limit = 20, hasNext = false, nextCursor = null),
                    )

                val response =
                    controller.getPosts(
                        status = "PUBLISHED",
                        type = "BLOG",
                        q = "kotlin",
                        tag = "Spring Boot",
                        limit = null,
                        cursor = null,
                    )

                Then("tag를 q와 별도 필터로 UseCase에 전달해야 한다") {
                    response.statusCode shouldBe HttpStatus.OK
                    verify(exactly = 1) {
                        getPostsUseCase.execute(
                            match {
                                it.status == "PUBLISHED" &&
                                    it.type == "BLOG" &&
                                    it.q == "kotlin" &&
                                    it.tag == "Spring Boot"
                            },
                        )
                    }
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

private fun setAuthentication(principal: String = "member-id", authorities: List<String>) {
    SecurityContextHolder.getContext().authentication =
        UsernamePasswordAuthenticationToken(
            principal,
            null,
            authorities.map { SimpleGrantedAuthority(it) },
        )
}

private fun mockVisitorRequest(): HttpServletRequest {
    val request = mockk<HttpServletRequest>()
    every { request.getHeader("X-Forwarded-For") } returns null
    every { request.getHeader("X-Real-IP") } returns null
    every { request.getHeader("User-Agent") } returns "test-agent"
    every { request.getHeader("Accept-Language") } returns "ko-KR"
    every { request.remoteAddr } returns "127.0.0.1"
    return request
}

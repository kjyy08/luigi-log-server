package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

/**
 * GetPostBySlugService 테스트
 */
class GetPostBySlugServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("Username과 Slug로 글을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            // MemberClient 제거됨
            val service = GetPostBySlugService(postRepository, memberClient)

            When("존재하는 Username과 Slug로 조회하면") {
                val memberId = MemberId.generate()
                val query = GetPostBySlugUseCase.Query(username = "testuser", slug = "test-post")

                val post =
                    Post.create(
                        memberId = memberId,
                        title = Title("테스트 글"),
                        slug = Slug("test-post"),
                        body = Body("테스트 내용"),
                        type = ContentType.BLOG,
                    )

                // MemberClient 호출 모킹 제거
                // findByUsernameAndSlug 모킹 추가
                every { postRepository.findByUsernameAndSlug("testuser", Slug("test-post")) } returns post
                every { memberClient.getAuthor(any()) } returns
                    MemberClient.Author(
                        memberId = memberId.value.toString(),
                        nickname = "TestUser",
                        profileImageUrl = null,
                    )

                val response = service.execute(query)

                Then("제목이 반환된다") {
                    response.title shouldBe "테스트 글"
                }

                Then("Slug가 반환된다") {
                    response.slug shouldBe "test-post"
                }

                Then("본문이 반환된다") {
                    response.body shouldBe "테스트 내용"
                }
            }
        }

        Given("존재하지 않는 Username으로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostBySlugService(postRepository, memberClient)

            When("존재하지 않는 Username으로 조회하면") {
                val query = GetPostBySlugUseCase.Query(username = "nonexistent", slug = "test-post")

                every { postRepository.findByUsernameAndSlug("nonexistent", Slug("test-post")) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }

        Given("존재하지 않는 Slug로 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostBySlugService(postRepository, memberClient)

            When("존재하는 Username이지만 없는 Slug로 조회하면") {
                val query = GetPostBySlugUseCase.Query(username = "testuser", slug = "non-existent")

                every { postRepository.findByUsernameAndSlug("testuser", Slug("non-existent")) } returns null

                Then("PostNotFoundException이 발생한다") {
                    shouldThrow<PostNotFoundException> {
                        service.execute(query)
                    }
                }
            }
        }
    })

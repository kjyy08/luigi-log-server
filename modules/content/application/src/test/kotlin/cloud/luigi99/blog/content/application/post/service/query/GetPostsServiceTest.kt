package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.common.domain.event.EventManager
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.application.post.port.out.MemberClient
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.PostStatus
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

/**
 * GetPostsService 테스트
 */
class GetPostsServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(EventManager)
            every { EventManager.eventContextManager } returns mockk(relaxed = true)
        }

        Given("전체 글 목록을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostsService(postRepository, memberClient)

            When("필터 없이 조회하면") {
                val query = GetPostsUseCase.Query()

                val posts =
                    listOf(
                        Post.create(
                            memberId = MemberId.generate(),
                            title = Title("글1"),
                            slug = Slug("post-1"),
                            body = Body("내용1"),
                            type = ContentType.BLOG,
                        ),
                        Post.create(
                            memberId = MemberId.generate(),
                            title = Title("글2"),
                            slug = Slug("post-2"),
                            body = Body("내용2"),
                            type = ContentType.PORTFOLIO,
                        ),
                    )

                every { postRepository.findAll() } returns posts
                every { memberClient.getAuthors(any()) } returns emptyMap()

                val response = service.execute(query)

                Then("모든 글이 반환된다") {
                    response.posts.size shouldBe 2
                }

                Then("작성자 정보를 찾을 수 없는 경우 Unknown으로 반환된다") {
                    response.posts.forEach { post ->
                        post.author.nickname shouldBe "Unknown"
                    }
                }
            }
        }

        Given("상태별로 글 목록을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostsService(postRepository, memberClient)

            When("PUBLISHED 상태로 필터링하면") {
                val query = GetPostsUseCase.Query(status = "PUBLISHED")

                val publishedPosts =
                    listOf(
                        Post
                            .create(
                                memberId = MemberId.generate(),
                                title = Title("발행 글"),
                                slug = Slug("published"),
                                body = Body("발행 내용"),
                                type = ContentType.BLOG,
                            ).publish(),
                    )

                every { postRepository.findAllByStatus(PostStatus.PUBLISHED) } returns publishedPosts
                every { memberClient.getAuthors(any()) } returns emptyMap()

                val response = service.execute(query)

                Then("PUBLISHED 글만 반환된다") {
                    response.posts.size shouldBe 1
                }
            }
        }

        Given("타입별로 글 목록을 조회할 때") {
            val postRepository = mockk<PostRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetPostsService(postRepository, memberClient)

            When("PORTFOLIO 타입으로 필터링하면") {
                val query = GetPostsUseCase.Query(type = "PORTFOLIO")

                val portfolioPosts =
                    listOf(
                        Post.create(
                            memberId = MemberId.generate(),
                            title = Title("포트폴리오"),
                            slug = Slug("portfolio"),
                            body = Body("프로젝트"),
                            type = ContentType.PORTFOLIO,
                        ),
                    )

                every { postRepository.findAllByContentType(ContentType.PORTFOLIO) } returns portfolioPosts
                every { memberClient.getAuthors(any()) } returns emptyMap()

                val response = service.execute(query)

                Then("PORTFOLIO 글만 반환된다") {
                    response.posts.size shouldBe 1
                }
            }
        }
    })

package cloud.luigi99.blog.content.comment.application.service.query

import cloud.luigi99.blog.content.comment.application.port.`in`.query.GetCommentListUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject

class GetCommentListServiceTest :
    BehaviorSpec({

        beforeTest {
            mockkObject(cloud.luigi99.blog.common.domain.event.EventManager)
            every { cloud.luigi99.blog.common.domain.event.EventManager.eventContextManager } returns
                mockk(relaxed = true)
        }

        Given("게시글에 작성된 댓글 목록을 조회하려고 할 때") {
            val commentRepository = mockk<CommentRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetCommentListService(commentRepository, memberClient)

            val postId = PostId.generate()
            val author1Id = MemberId.generate()
            val author2Id = MemberId.generate()

            val comment1 = Comment.create(postId, author1Id, CommentContent("첫 번째 댓글"))
            val comment2 = Comment.create(postId, author2Id, CommentContent("두 번째 댓글"))

            val query =
                GetCommentListUseCase.Query(
                    postId = postId.value.toString(),
                )

            When("댓글 목록 조회를 요청하면") {
                every { commentRepository.findByPostId(postId) } returns listOf(comment1, comment2)
                every { memberClient.getAuthors(any()) } returns
                    mapOf(
                        author1Id.value.toString() to
                            MemberClient.Author(
                                author1Id.value.toString(),
                                "작성자1",
                                null,
                                "user1",
                            ),
                        author2Id.value.toString() to
                            MemberClient.Author(author2Id.value.toString(), "작성자2", null, "user2"),
                    )

                val response = service.execute(query)

                Then("게시글의 모든 댓글이 반환된다") {
                    response.comments.size shouldBe 2
                }

                Then("각 댓글의 정보와 작성자 정보가 포함된다") {
                    response.comments[0].content shouldBe "첫 번째 댓글"
                    response.comments[0]
                        .author.nickname shouldBe "작성자1"
                    response.comments[1].content shouldBe "두 번째 댓글"
                    response.comments[1]
                        .author.nickname shouldBe "작성자2"
                }
            }
        }

        Given("댓글이 없는 게시글의 댓글 목록을 조회할 때") {
            val commentRepository = mockk<CommentRepository>()
            val memberClient = mockk<MemberClient>()
            val service = GetCommentListService(commentRepository, memberClient)

            val postId = PostId.generate()

            val query =
                GetCommentListUseCase.Query(
                    postId = postId.value.toString(),
                )

            When("댓글 목록 조회를 요청하면") {
                every { commentRepository.findByPostId(postId) } returns emptyList()
                every { memberClient.getAuthors(any()) } returns emptyMap()

                val response = service.execute(query)

                Then("빈 목록이 반환된다") {
                    response.comments.size shouldBe 0
                }
            }
        }
    })

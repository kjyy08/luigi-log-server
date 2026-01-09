package cloud.luigi99.blog.content.comment.application.service.query

import cloud.luigi99.blog.content.comment.application.port.`in`.query.GetCommentListUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.domain.post.vo.PostId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 댓글 목록 조회 서비스
 *
 * 게시글에 작성된 댓글 목록을 조회하는 기능을 제공합니다.
 */
@Service
@Transactional(readOnly = true)
class GetCommentListService(private val commentRepository: CommentRepository, private val memberClient: MemberClient) :
    GetCommentListUseCase {
    /**
     * 게시글의 댓글 목록을 조회합니다.
     */
    override fun execute(query: GetCommentListUseCase.Query): GetCommentListUseCase.Response {
        log.info { "Getting comment list for post: ${query.postId}" }

        val postId = PostId.from(query.postId)
        val comments = commentRepository.findByPostId(postId)

        log.info { "Found ${comments.size} comments for post: $postId" }

        val authorIds =
            comments
                .map {
                    it.authorId.value
                        .toString()
                }.distinct()
        val authors = memberClient.getAuthors(authorIds)

        return GetCommentListUseCase.Response(
            comments =
                comments.map { comment ->
                    val authorInfo =
                        authors[
                            comment.authorId.value
                                .toString(),
                        ]
                            ?: MemberClient.Author(
                                comment.authorId.value
                                    .toString(),
                                "Unknown",
                                null,
                                "unknown",
                            )
                    GetCommentListUseCase.CommentInfo(
                        commentId =
                            comment.entityId.value
                                .toString(),
                        author =
                            GetCommentListUseCase.AuthorInfo(
                                memberId = authorInfo.memberId,
                                nickname = authorInfo.nickname,
                                profileImageUrl = authorInfo.profileImageUrl,
                                username = authorInfo.username,
                            ),
                        content = comment.content.value,
                        createdAt = comment.createdAt.toString(),
                        updatedAt = comment.updatedAt.toString(),
                    )
                },
        )
    }
}

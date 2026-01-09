package cloud.luigi99.blog.content.comment.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.AuthorResponse
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.CommentResponse
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.CreateCommentRequest
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.UpdateCommentRequest
import cloud.luigi99.blog.content.comment.application.port.`in`.command.CommentCommandFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.command.CreateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.DeleteCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.command.UpdateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.`in`.query.CommentQueryFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.query.GetCommentListUseCase
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

/**
 * 댓글 컨트롤러
 *
 * 댓글 관리를 위한 RESTful API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/comments")
class CommentController(
    private val commentCommandFacade: CommentCommandFacade,
    private val commentQueryFacade: CommentQueryFacade,
) : CommentApi {
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    override fun createComment(
        @AuthenticationPrincipal authorId: String,
        @RequestBody request: CreateCommentRequest,
    ): ResponseEntity<CommonResponse<CommentResponse>> {
        log.info { "Creating comment for post: ${request.postId}, author: $authorId" }

        val response =
            commentCommandFacade.createComment().execute(
                CreateCommentUseCase.Command(
                    postId = request.postId,
                    authorId = authorId,
                    content = request.content,
                ),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                CommentResponse(
                    commentId = response.commentId,
                    postId = response.postId,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    content = response.content,
                    createdAt = LocalDateTime.parse(response.createdAt),
                    updatedAt = null,
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{commentId}")
    override fun updateComment(
        @AuthenticationPrincipal authorId: String,
        @PathVariable commentId: String,
        @RequestBody request: UpdateCommentRequest,
    ): ResponseEntity<CommonResponse<CommentResponse>> {
        log.info { "Updating comment: $commentId, author: $authorId" }

        val response =
            commentCommandFacade.updateComment().execute(
                UpdateCommentUseCase.Command(
                    commentId = commentId,
                    authorId = authorId,
                    content = request.content,
                ),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                CommentResponse(
                    commentId = response.commentId,
                    postId = null,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    content = response.content,
                    createdAt = null,
                    updatedAt = LocalDateTime.parse(response.updatedAt),
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{commentId}")
    override fun deleteComment(
        @AuthenticationPrincipal authorId: String,
        @PathVariable commentId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting comment: $commentId, author: $authorId" }

        commentCommandFacade.deleteComment().execute(
            DeleteCommentUseCase.Command(
                commentId = commentId,
                authorId = authorId,
            ),
        )

        return ResponseEntity.noContent().build()
    }

    @GetMapping
    override fun getCommentList(
        @RequestParam postId: String,
    ): ResponseEntity<CommonResponse<List<CommentResponse>>> {
        log.info { "Getting comment list for post: $postId" }

        val response =
            commentQueryFacade.getCommentList().execute(
                GetCommentListUseCase.Query(postId = postId),
            )

        return ResponseEntity.ok(
            CommonResponse.success(
                response.comments.map { comment ->
                    CommentResponse(
                        commentId = comment.commentId,
                        postId = postId,
                        author =
                            AuthorResponse(
                                memberId = comment.author.memberId,
                                nickname = comment.author.nickname,
                                profileImageUrl = comment.author.profileImageUrl,
                                username = comment.author.username,
                            ),
                        content = comment.content,
                        createdAt = LocalDateTime.parse(comment.createdAt),
                        updatedAt = LocalDateTime.parse(comment.updatedAt),
                    )
                },
            ),
        )
    }
}

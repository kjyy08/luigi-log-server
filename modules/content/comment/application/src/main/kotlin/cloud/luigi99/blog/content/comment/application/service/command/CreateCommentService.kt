package cloud.luigi99.blog.content.comment.application.service.command

import cloud.luigi99.blog.content.comment.application.port.`in`.command.CreateCommentUseCase
import cloud.luigi99.blog.content.comment.application.port.out.CommentRepository
import cloud.luigi99.blog.content.comment.application.port.out.MemberClient
import cloud.luigi99.blog.content.comment.domain.model.Comment
import cloud.luigi99.blog.content.comment.domain.vo.CommentContent
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * 댓글 생성 서비스
 *
 * 게시글에 새로운 댓글을 작성하는 기능을 제공합니다.
 */
@Service
class CreateCommentService(private val commentRepository: CommentRepository, private val memberClient: MemberClient) :
    CreateCommentUseCase {
    /**
     * 댓글을 생성합니다.
     */
    @Transactional
    override fun execute(command: CreateCommentUseCase.Command): CreateCommentUseCase.Response {
        log.info { "Creating comment for post: ${command.postId}, author: ${command.authorId}" }

        val postId = PostId.from(command.postId)
        val authorId = MemberId.from(command.authorId)
        val content = CommentContent(command.content)

        val comment = Comment.create(postId, authorId, content)
        val savedComment = commentRepository.save(comment)

        log.info { "Successfully created comment: ${savedComment.entityId}" }

        val author = memberClient.getAuthor(command.authorId)

        return CreateCommentUseCase.Response(
            commentId =
                savedComment.entityId.value
                    .toString(),
            postId =
                savedComment.postId.value
                    .toString(),
            author =
                CreateCommentUseCase.AuthorInfo(
                    memberId = author.memberId,
                    nickname = author.nickname,
                    profileImageUrl = author.profileImageUrl,
                    username = author.username,
                ),
            content = savedComment.content.value,
            createdAt = savedComment.createdAt.toString(),
        )
    }
}

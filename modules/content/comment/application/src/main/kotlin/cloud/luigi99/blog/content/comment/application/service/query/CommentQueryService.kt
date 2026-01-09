package cloud.luigi99.blog.content.comment.application.service.query

import cloud.luigi99.blog.content.comment.application.port.`in`.query.CommentQueryFacade
import cloud.luigi99.blog.content.comment.application.port.`in`.query.GetCommentListUseCase
import org.springframework.stereotype.Service

/**
 * 댓글 조회 서비스
 *
 * 댓글 조회와 관련된 유스케이스들을 제공하는 Facade 구현체입니다.
 */
@Service
class CommentQueryService(private val getCommentListUseCase: GetCommentListUseCase) : CommentQueryFacade {
    override fun getCommentList(): GetCommentListUseCase = getCommentListUseCase
}

package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostContributionsUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.PostQueryFacade
import org.springframework.stereotype.Service

/**
 * Post Query Facade 구현체
 *
 * Post 조회 관련 UseCase들을 그룹핑하여 제공합니다.
 */
@Service
class PostQueryService(
    private val getPostBySlugUseCase: GetPostBySlugUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val getPostContributionsUseCase: GetPostContributionsUseCase,
) : PostQueryFacade {
    override fun getPostBySlug(): GetPostBySlugUseCase = getPostBySlugUseCase

    override fun getPostById(): GetPostByIdUseCase = getPostByIdUseCase

    override fun getPosts(): GetPostsUseCase = getPostsUseCase

    override fun getPostContributions(): GetPostContributionsUseCase = getPostContributionsUseCase
}

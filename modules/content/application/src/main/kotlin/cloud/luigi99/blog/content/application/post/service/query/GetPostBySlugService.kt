package cloud.luigi99.blog.content.application.post.service.query

import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.PostNotFoundException
import cloud.luigi99.blog.content.domain.post.vo.Slug
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Username과 Slug로 Post 조회 유스케이스 구현체
 *
 * 사용자 이름과 URL slug를 사용하여 Post를 조회합니다.
 */
@Service
class GetPostBySlugService(private val postRepository: PostRepository) : GetPostBySlugUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetPostBySlugUseCase.Query): GetPostBySlugUseCase.Response {
        log.info { "Getting post by username: ${query.username}, slug: ${query.slug}" }

        val slug = Slug(query.slug)
        val post =
            postRepository.findByUsernameAndSlug(query.username, slug)
                ?: throw PostNotFoundException(
                    "사용자 '${query.username}'의 Slug '${query.slug}'에 해당하는 Post를 찾을 수 없습니다",
                )

        return GetPostBySlugUseCase.Response(
            postId =
                post.entityId.value
                    .toString(),
            memberId =
                post.memberId.value
                    .toString(),
            title = post.title.value,
            slug = post.slug.value,
            body = post.body.value,
            type = post.type.name,
            status = post.status.name,
            tags = post.tags,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
        )
    }
}

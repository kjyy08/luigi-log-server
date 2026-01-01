package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.content.application.post.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.application.post.port.out.PostRepository
import cloud.luigi99.blog.content.domain.post.exception.DuplicateSlugException
import cloud.luigi99.blog.content.domain.post.model.Post
import cloud.luigi99.blog.content.domain.post.vo.Body
import cloud.luigi99.blog.content.domain.post.vo.ContentType
import cloud.luigi99.blog.content.domain.post.vo.Slug
import cloud.luigi99.blog.content.domain.post.vo.Title
import cloud.luigi99.blog.member.domain.member.vo.MemberId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

/**
 * Post 생성 유스케이스 구현체
 *
 * 새로운 블로그 글을 DRAFT 상태로 생성합니다.
 * Slug 중복 검증을 수행합니다.
 */
@Service
class CreatePostService(private val postRepository: PostRepository) : CreatePostUseCase {
    @Transactional
    override fun execute(command: CreatePostUseCase.Command): CreatePostUseCase.Response {
        log.info { "Creating new post with slug: ${command.slug} by member: ${command.memberId}" }

        val memberId = MemberId.from(command.memberId)
        val slug = Slug(command.slug)

        // Slug 중복 검증 (사용자별)
        if (postRepository.existsByMemberIdAndSlug(memberId, slug)) {
            throw DuplicateSlugException("슬러그 '${command.slug}'는 이미 사용 중입니다")
        }

        val title = Title(command.title)
        val body = Body(command.body)
        val type = ContentType.valueOf(command.type)

        // Post 생성
        var newPost =
            Post.create(
                memberId = memberId,
                title = title,
                slug = slug,
                body = body,
                type = type,
            )

        // 태그 추가
        command.tags.forEach { tagName ->
            newPost = newPost.addTag(tagName)
        }

        // Post 저장
        val savedPost = postRepository.save(newPost)

        log.info { "Successfully created post: ${savedPost.entityId}" }

        return CreatePostUseCase.Response(
            postId =
                savedPost.entityId.value
                    .toString(),
            memberId =
                savedPost.memberId.value
                    .toString(),
            title = savedPost.title.value,
            slug = savedPost.slug.value,
            body = savedPost.body.value,
            type = savedPost.type.name,
            status = savedPost.status.name,
            tags = savedPost.tags,
            createdAt = savedPost.createdAt,
            updatedAt = savedPost.updatedAt,
        )
    }
}

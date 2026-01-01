package cloud.luigi99.blog.content.application.post.service.command

import cloud.luigi99.blog.content.application.post.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.command.PostCommandFacade
import cloud.luigi99.blog.content.application.post.port.`in`.command.UpdatePostUseCase
import org.springframework.stereotype.Service

/**
 * Post Command Facade 구현체
 *
 * Post 생성/수정 관련 UseCase들을 그룹핑하여 제공합니다 (RESTful 설계).
 */
@Service
class PostCommandService(
    private val createPostUseCase: CreatePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
) : PostCommandFacade {
    override fun createPost(): CreatePostUseCase = createPostUseCase

    override fun updatePost(): UpdatePostUseCase = updatePostUseCase
}

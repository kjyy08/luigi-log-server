package cloud.luigi99.blog.content.application.post.port.`in`.command

/**
 * Post Command Facade
 *
 * Post 관련 Command UseCase들을 그룹핑합니다.
 */
interface PostCommandFacade {
    /**
     * Post 생성 UseCase를 반환합니다.
     */
    fun createPost(): CreatePostUseCase

    /**
     * Post 수정 UseCase를 반환합니다.
     */
    fun updatePost(): UpdatePostUseCase
}

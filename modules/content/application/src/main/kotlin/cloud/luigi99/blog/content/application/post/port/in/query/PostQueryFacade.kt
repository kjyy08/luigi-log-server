package cloud.luigi99.blog.content.application.post.port.`in`.query

/**
 * Post Query Facade
 *
 * Post 관련 Query UseCase들을 그룹핑합니다.
 */
interface PostQueryFacade {
    /**
     * Slug로 Post 조회 UseCase를 반환합니다.
     */
    fun getPostBySlug(): GetPostBySlugUseCase

    /**
     * ID로 Post 조회 UseCase를 반환합니다.
     */
    fun getPostById(): GetPostByIdUseCase

    /**
     * Post 목록 조회 UseCase를 반환합니다.
     */
    fun getPosts(): GetPostsUseCase
}

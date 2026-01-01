package cloud.luigi99.blog.content.adapter.`in`.web.post

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.CreatePostRequest
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.PostListResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.PostResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.PostSummaryResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.UpdatePostRequest
import cloud.luigi99.blog.content.application.post.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.command.PostCommandFacade
import cloud.luigi99.blog.content.application.post.port.`in`.command.UpdatePostUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.application.post.port.`in`.query.PostQueryFacade
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

/**
 * Post 컨트롤러
 *
 * 블로그 글 생성, 조회, 수정, 발행, 아카이빙 등 Post 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/posts")
class PostController(private val postQueryFacade: PostQueryFacade, private val postCommandFacade: PostCommandFacade) :
    PostApi {
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    override fun createPost(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreatePostRequest,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info { "Creating post with slug: ${request.slug}" }

        val command =
            CreatePostUseCase.Command(
                memberId = memberId,
                title = request.title,
                slug = request.slug,
                body = request.body,
                type = request.type,
                tags = request.tags ?: emptyList(),
            )

        val response = postCommandFacade.createPost().execute(command)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    memberId = response.memberId,
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt,
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{postId}")
    override fun updatePost(
        @AuthenticationPrincipal memberId: String,
        @PathVariable postId: String,
        @RequestBody request: UpdatePostRequest,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info {
            "Updating post: $postId (title=${request.title != null}, body=${request.body != null}, status=${request.status})"
        }

        val command =
            UpdatePostUseCase.Command(
                memberId = memberId,
                postId = postId,
                title = request.title,
                body = request.body,
                status = request.status,
            )

        val response = postCommandFacade.updatePost().execute(command)

        return ResponseEntity.ok(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    memberId = response.memberId,
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt,
                ),
            ),
        )
    }

    @GetMapping("/{postId}")
    override fun getPostById(
        @PathVariable postId: String,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info { "Getting post by ID: $postId" }

        val query = GetPostByIdUseCase.Query(postId = postId)
        val response = postQueryFacade.getPostById().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    memberId = response.memberId,
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt,
                ),
            ),
        )
    }

    @GetMapping("/@{username}/{slug}")
    override fun getPostByUsernameAndSlug(
        @PathVariable username: String,
        @PathVariable slug: String,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info { "Getting post by username: $username, slug: $slug" }

        val query = GetPostBySlugUseCase.Query(username = username, slug = slug)
        val response = postQueryFacade.getPostBySlug().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    memberId = response.memberId,
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    createdAt = response.createdAt,
                    updatedAt = response.updatedAt,
                ),
            ),
        )
    }

    @GetMapping
    override fun getPosts(
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) type: String?,
    ): ResponseEntity<CommonResponse<PostListResponse>> {
        log.info { "Listing posts with filters - status: $status, type: $type" }

        val query = GetPostsUseCase.Query(status = status, type = type)
        val response = postQueryFacade.getPosts().execute(query)

        val summaries =
            response.posts.map { post ->
                PostSummaryResponse(
                    postId = post.postId,
                    memberId = post.memberId,
                    title = post.title,
                    slug = post.slug,
                    type = post.type,
                    status = post.status,
                    tags = post.tags,
                    createdAt = post.createdAt,
                )
            }

        return ResponseEntity.ok(
            CommonResponse.success(
                PostListResponse(
                    posts = summaries,
                    total = summaries.size,
                ),
            ),
        )
    }
}

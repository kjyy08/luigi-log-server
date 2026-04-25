package cloud.luigi99.blog.content.post.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.AuthorResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.CreatePostRequest
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PageInfoResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PostContributionDayResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PostContributionsResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PostListResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PostResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.PostSummaryResponse
import cloud.luigi99.blog.content.post.adapter.`in`.web.dto.UpdatePostRequest
import cloud.luigi99.blog.content.post.application.port.`in`.command.CreatePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.command.DeletePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.command.PostCommandFacade
import cloud.luigi99.blog.content.post.application.port.`in`.command.UpdatePostUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostByIdUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostBySlugUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostContributionsUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.post.application.port.`in`.query.PostQueryFacade
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.MessageDigest
import java.time.LocalDate

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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_post:create')")
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
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
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

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_post:update') or hasAuthority('SCOPE_post:publish')")
    @PutMapping("/{postId}")
    override fun updatePost(
        @AuthenticationPrincipal memberId: String,
        @PathVariable postId: String,
        @RequestBody request: UpdatePostRequest,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info {
            "Updating post: $postId " +
                "(title=${request.title != null}, body=${request.body != null}, status=${request.status})"
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
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
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
        request: HttpServletRequest,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info { "Getting post by ID: $postId" }

        val query = GetPostByIdUseCase.Query(postId = postId, visitorKey = createVisitorKey(request))
        val response = postQueryFacade.getPostById().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    viewCount = response.viewCount,
                    commentCount = response.commentCount,
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
        request: HttpServletRequest,
    ): ResponseEntity<CommonResponse<PostResponse>> {
        log.info { "Getting post by username: $username, slug: $slug" }

        val query = GetPostBySlugUseCase.Query(username = username, slug = slug, visitorKey = createVisitorKey(request))
        val response = postQueryFacade.getPostBySlug().execute(query)

        return ResponseEntity.ok(
            CommonResponse.success(
                PostResponse(
                    postId = response.postId,
                    author =
                        AuthorResponse(
                            memberId = response.author.memberId,
                            nickname = response.author.nickname,
                            profileImageUrl = response.author.profileImageUrl,
                            username = response.author.username,
                        ),
                    title = response.title,
                    slug = response.slug,
                    body = response.body,
                    type = response.type,
                    status = response.status,
                    tags = response.tags,
                    viewCount = response.viewCount,
                    commentCount = response.commentCount,
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
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) cursor: String?,
    ): ResponseEntity<CommonResponse<PostListResponse>> {
        log.info { "Listing posts with filters - status: $status, type: $type" }

        val query = GetPostsUseCase.Query(status = status, type = type, q = q, limit = limit, cursor = cursor)
        val response = postQueryFacade.getPosts().execute(query)

        val summaries =
            response.posts.map { post ->
                PostSummaryResponse(
                    postId = post.postId,
                    author =
                        AuthorResponse(
                            memberId = post.author.memberId,
                            nickname = post.author.nickname,
                            profileImageUrl = post.author.profileImageUrl,
                            username = post.author.username,
                        ),
                    title = post.title,
                    slug = post.slug,
                    type = post.type,
                    status = post.status,
                    tags = post.tags,
                    viewCount = post.viewCount,
                    commentCount = post.commentCount,
                    createdAt = post.createdAt,
                )
            }

        return ResponseEntity.ok(
            CommonResponse.success(
                PostListResponse(
                    posts = summaries,
                    total = summaries.size,
                    pageInfo =
                        PageInfoResponse(
                            response.pageInfo.limit,
                            response.pageInfo.hasNext,
                            response.pageInfo.nextCursor,
                        ),
                ),
            ),
        )
    }

    @GetMapping("/contributions")
    override fun getPostContributions(
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
        @RequestParam(required = false) type: String?,
    ): ResponseEntity<CommonResponse<PostContributionsResponse>> {
        val response =
            postQueryFacade
                .getPostContributions()
                .execute(GetPostContributionsUseCase.Query(from = from, to = to, type = type))

        return ResponseEntity.ok(
            CommonResponse.success(
                PostContributionsResponse(
                    from = response.from,
                    to = response.to,
                    totalCount = response.totalCount,
                    days = response.days.map { PostContributionDayResponse(it.date, it.count) },
                ),
            ),
        )
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{postId}")
    override fun deletePost(
        @AuthenticationPrincipal memberId: String,
        @PathVariable postId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting post: $postId by member: $memberId" }

        val command = DeletePostUseCase.Command(memberId = memberId, postId = postId)
        postCommandFacade.deletePost().execute(command)

        return ResponseEntity.noContent().build()
    }

    private fun createVisitorKey(request: HttpServletRequest): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal

        if (authentication?.isAuthenticated == true &&
            principal is String &&
            principal.isNotBlank() &&
            principal != "anonymousUser"
        ) {
            return "member:$principal"
        }

        val ip =
            request
                .getHeader("X-Forwarded-For")
                ?.split(",")
                ?.firstOrNull()
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: request.getHeader("X-Real-IP")
                ?: request.remoteAddr.orEmpty()
        val userAgent = request.getHeader("User-Agent").orEmpty()
        val acceptLanguage = request.getHeader("Accept-Language").orEmpty()
        val fingerprint = "$ip|$userAgent|$acceptLanguage"

        return "anonymous:${sha256(fingerprint)}"
    }

    private fun sha256(value: String): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte) }
}

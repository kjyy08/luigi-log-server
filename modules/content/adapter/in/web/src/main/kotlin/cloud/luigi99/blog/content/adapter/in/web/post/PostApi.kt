package cloud.luigi99.blog.content.adapter.`in`.web.post

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.CreatePostRequest
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.PostListResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.PostResponse
import cloud.luigi99.blog.content.adapter.`in`.web.post.dto.UpdatePostRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

/**
 * Post API 인터페이스
 *
 * 블로그 글 관련 API를 정의합니다.
 */
@Tag(name = "Post", description = "블로그 글 관리 API")
interface PostApi {
    @Operation(
        summary = "블로그 글 생성",
        description = "새로운 블로그 글 또는 포트폴리오를 생성합니다. 초기 상태는 DRAFT입니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "글 생성 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CreatePostSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "postId": "550e8400-e29b-41d4-a716-446655440000",
                                    "memberId": "987e6543-e21b-98d7-a654-426614174111",
                                    "title": "Kotlin으로 DDD 구현하기",
                                    "slug": "kotlin-ddd-implementation",
                                    "body": "# 시작하기\n\nKotlin과 DDD를 결합하면...",
                                    "type": "BLOG",
                                    "status": "DRAFT",
                                    "tags": ["Kotlin", "DDD"],
                                    "createdAt": "2025-12-31T12:00:00",
                                    "updatedAt": "2025-12-31T12:00:00"
                                  },
                                  "error": null,
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (Slug 중복 등)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "SlugDuplicate",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CONTENT_002",
                                    "message": "이미 존재하는 slug입니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Unauthorized",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "AUTH_001",
                                    "message": "인증에 실패했습니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun createPost(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreatePostRequest,
    ): ResponseEntity<CommonResponse<PostResponse>>

    @Operation(
        summary = "블로그 글 수정",
        description = "기존 블로그 글의 제목, 본문, 상태를 선택적으로 수정합니다. null인 필드는 변경되지 않습니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "글 수정 성공",
                content = [Content(schema = Schema(implementation = CommonResponse::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Unauthorized",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "AUTH_001",
                                    "message": "인증에 실패했습니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "글을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "PostNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CONTENT_001",
                                    "message": "글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun updatePost(
        @AuthenticationPrincipal memberId: String,
        @Parameter(description = "Post ID") @PathVariable postId: String,
        @RequestBody request: UpdatePostRequest,
    ): ResponseEntity<CommonResponse<PostResponse>>

    @Operation(
        summary = "ID로 블로그 글 조회",
        description = "Post ID로 단일 블로그 글을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "글 조회 성공",
                content = [Content(schema = Schema(implementation = CommonResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "글을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "PostNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CONTENT_001",
                                    "message": "글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getPostById(
        @Parameter(description = "Post ID") @PathVariable postId: String,
    ): ResponseEntity<CommonResponse<PostResponse>>

    @Operation(
        summary = "사용자의 특정 Slug 블로그 글 조회",
        description = "사용자 이름과 URL Slug로 단일 블로그 글을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "글 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GetPostSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "postId": "550e8400-e29b-41d4-a716-446655440000",
                                    "memberId": "987e6543-e21b-98d7-a654-426614174111",
                                    "title": "Kotlin으로 DDD 구현하기",
                                    "slug": "kotlin-ddd-implementation",
                                    "body": "# 시작하기\n\nKotlin과 DDD를 결합하면...",
                                    "type": "BLOG",
                                    "status": "PUBLISHED",
                                    "tags": ["Kotlin", "DDD"],
                                    "createdAt": "2025-12-31T12:00:00",
                                    "updatedAt": "2025-12-31T12:00:00"
                                  },
                                  "error": null,
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "글을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "PostNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "CONTENT_001",
                                    "message": "글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getPostByUsernameAndSlug(
        @Parameter(description = "사용자 이름") @PathVariable username: String,
        @Parameter(description = "URL Slug") @PathVariable slug: String,
    ): ResponseEntity<CommonResponse<PostResponse>>

    @Operation(
        summary = "블로그 글 목록 조회",
        description = "블로그 글 목록을 조회합니다. 상태 및 타입 필터링이 가능합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "목록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ListPostsSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "posts": [
                                      {
                                        "postId": "550e8400-e29b-41d4-a716-446655440000",
                                        "memberId": "987e6543-e21b-98d7-a654-426614174111",
                                        "title": "Kotlin으로 DDD 구현하기",
                                        "slug": "kotlin-ddd-implementation",
                                        "type": "BLOG",
                                        "status": "PUBLISHED",
                                        "tags": ["Kotlin", "DDD"],
                                        "createdAt": "2025-12-31T12:00:00"
                                      }
                                    ],
                                    "total": 1
                                  },
                                  "error": null,
                                  "timestamp": "2025-12-31T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getPosts(
        @Parameter(description = "상태 필터 (DRAFT, PUBLISHED, ARCHIVED)") @RequestParam(required = false) status: String?,
        @Parameter(description = "타입 필터 (BLOG, PORTFOLIO)") @RequestParam(required = false) type: String?,
    ): ResponseEntity<CommonResponse<PostListResponse>>
}

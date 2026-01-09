package cloud.luigi99.blog.content.comment.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.CommentResponse
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.CreateCommentRequest
import cloud.luigi99.blog.content.comment.adapter.`in`.web.dto.UpdateCommentRequest
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
 * 댓글 API 인터페이스
 *
 * 댓글 관리 관련 API를 정의합니다.
 */
@Tag(name = "Comment", description = "댓글 관리 API")
interface CommentApi {
    @Operation(
        summary = "댓글 작성",
        description = "게시글에 새로운 댓글을 작성합니다. 로그인한 회원만 작성 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "댓글 작성 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CreateCommentSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "commentId": "550e8400-e29b-41d4-a716-446655440000",
                                    "postId": "987e6543-e21b-98d7-a654-426614174111",
                                    "author": {
                                      "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                      "nickname": "Luigi99",
                                      "profileImageUrl": "https://example.com/profile.jpg",
                                      "username": "luigi99"
                                    },
                                    "content": "좋은 글 감사합니다!",
                                    "createdAt": "2025-12-31T12:00:00",
                                    "updatedAt": null
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
                description = "잘못된 요청 (댓글 내용 길이 초과 등)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "InvalidContent",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMON_001",
                                    "message": "댓글 내용은 1~1000자 사이여야 합니다."
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
            ApiResponse(
                responseCode = "404",
                description = "게시글을 찾을 수 없음",
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
                                    "message": "게시글을 찾을 수 없습니다."
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
    fun createComment(
        @AuthenticationPrincipal authorId: String,
        @RequestBody request: CreateCommentRequest,
    ): ResponseEntity<CommonResponse<CommentResponse>>

    @Operation(
        summary = "댓글 수정",
        description = "작성한 댓글의 내용을 수정합니다. 작성자 본인만 수정 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "댓글 수정 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "UpdateCommentSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "commentId": "550e8400-e29b-41d4-a716-446655440000",
                                    "postId": null,
                                    "author": {
                                      "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                      "nickname": "Luigi99",
                                      "profileImageUrl": "https://example.com/profile.jpg",
                                      "username": "luigi99"
                                    },
                                    "content": "수정된 댓글입니다.",
                                    "createdAt": null,
                                    "updatedAt": "2025-12-31T12:30:00"
                                  },
                                  "error": null,
                                  "timestamp": "2025-12-31T12:30:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (댓글 내용 길이 초과 등)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "InvalidContent",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMON_001",
                                    "message": "댓글 내용은 1~1000자 사이여야 합니다."
                                  },
                                  "timestamp": "2025-12-31T12:30:00"
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
                                  "timestamp": "2025-12-31T12:30:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "댓글 수정 권한 없음 (작성자가 아님)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Forbidden",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMENT_002",
                                    "message": "댓글 수정 권한이 없습니다."
                                  },
                                  "timestamp": "2025-12-31T12:30:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "댓글을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CommentNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMENT_001",
                                    "message": "댓글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T12:30:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun updateComment(
        @AuthenticationPrincipal authorId: String,
        @Parameter(description = "댓글 ID") @PathVariable commentId: String,
        @RequestBody request: UpdateCommentRequest,
    ): ResponseEntity<CommonResponse<CommentResponse>>

    @Operation(
        summary = "댓글 삭제",
        description = "작성한 댓글을 삭제합니다. 작성자 본인만 삭제 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "댓글 삭제 성공",
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
                                  "timestamp": "2025-12-31T13:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "댓글 삭제 권한 없음 (작성자가 아님)",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Forbidden",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMENT_002",
                                    "message": "댓글 삭제 권한이 없습니다."
                                  },
                                  "timestamp": "2025-12-31T13:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "댓글을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CommentNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "COMMENT_001",
                                    "message": "댓글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T13:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun deleteComment(
        @AuthenticationPrincipal authorId: String,
        @Parameter(description = "댓글 ID") @PathVariable commentId: String,
    ): ResponseEntity<Unit>

    @Operation(
        summary = "댓글 목록 조회",
        description = "게시글에 작성된 댓글 목록을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "댓글 목록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GetCommentListSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": [
                                    {
                                      "commentId": "550e8400-e29b-41d4-a716-446655440000",
                                      "postId": "987e6543-e21b-98d7-a654-426614174111",
                                      "author": {
                                        "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                        "nickname": "Luigi99",
                                        "profileImageUrl": "https://example.com/profile.jpg",
                                        "username": "luigi99"
                                      },
                                      "content": "좋은 글 감사합니다!",
                                      "createdAt": "2025-12-31T12:00:00",
                                      "updatedAt": null
                                    },
                                    {
                                      "commentId": "660f9511-f3ac-52e5-b827-557766551111",
                                      "postId": "987e6543-e21b-98d7-a654-426614174111",
                                      "author": {
                                        "memberId": "234f5678-f90c-23e4-b567-537725285222",
                                        "nickname": "코딩왕",
                                        "profileImageUrl": null,
                                        "username": "codingking"
                                      },
                                      "content": "도움이 많이 되었습니다.",
                                      "createdAt": "2025-12-31T11:30:00",
                                      "updatedAt": null
                                    }
                                  ],
                                  "error": null,
                                  "timestamp": "2025-12-31T14:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "게시글을 찾을 수 없음",
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
                                    "message": "게시글을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2025-12-31T14:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getCommentList(
        @Parameter(description = "게시글 ID") @RequestParam postId: String,
    ): ResponseEntity<CommonResponse<List<CommentResponse>>>
}

package cloud.luigi99.blog.content.guestbook.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.CreateGuestbookRequest
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.GuestbookResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.ModifyGuestbookRequest
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

/**
 * Guestbook API 인터페이스
 *
 * 방명록 관련 API를 정의합니다.
 */
@Tag(name = "Guestbook", description = "방명록 관리 API")
interface GuestbookApi {
    @Operation(
        summary = "방명록 작성",
        description = "새로운 방명록 글을 작성합니다. 로그인한 회원만 작성 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "방명록 작성 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "CreateGuestbookSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "guestbookId": "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890",
                                    "author": {
                                      "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                      "nickname": "Luigi",
                                      "profileImageUrl": "https://example.com/profile.jpg",
                                      "username": "luigi99"
                                    },
                                    "content": "안녕하세요! 블로그 잘 보고 있습니다.",
                                    "createdAt": "2024-01-09T12:00:00",
                                    "updatedAt": "2024-01-09T12:00:00"
                                  },
                                  "error": null,
                                  "timestamp": "2024-01-09T12:00:00"
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
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun createGuestbook(
        @AuthenticationPrincipal memberId: String,
        @RequestBody request: CreateGuestbookRequest,
    ): ResponseEntity<CommonResponse<GuestbookResponse>>

    @Operation(
        summary = "방명록 수정",
        description = "기존 방명록 글을 수정합니다. 작성자만 수정 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "방명록 수정 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ModifyGuestbookSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "guestbookId": "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890",
                                    "author": {
                                      "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                      "nickname": "Luigi",
                                      "profileImageUrl": "https://example.com/profile.jpg",
                                      "username": "luigi99"
                                    },
                                    "content": "수정된 방명록 내용입니다.",
                                    "createdAt": "2024-01-09T12:00:00",
                                    "updatedAt": "2024-01-09T12:05:00"
                                  },
                                  "error": null,
                                  "timestamp": "2024-01-09T12:05:00"
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
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "수정 권한 없음",
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
                                    "code": "GUESTBOOK_002",
                                    "message": "방명록에 대한 권한이 없습니다."
                                  },
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "방명록을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GuestbookNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "GUESTBOOK_001",
                                    "message": "방명록을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun modifyGuestbook(
        @AuthenticationPrincipal memberId: String,
        @Parameter(description = "방명록 ID") @PathVariable guestbookId: String,
        @RequestBody request: ModifyGuestbookRequest,
    ): ResponseEntity<CommonResponse<GuestbookResponse>>

    @Operation(
        summary = "방명록 삭제",
        description = "방명록 글을 삭제합니다. 작성자만 삭제 가능합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "방명록 삭제 성공",
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
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "삭제 권한 없음",
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
                                    "code": "GUESTBOOK_002",
                                    "message": "방명록에 대한 권한이 없습니다."
                                  },
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "방명록을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GuestbookNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "GUESTBOOK_001",
                                    "message": "방명록을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun deleteGuestbook(
        @AuthenticationPrincipal memberId: String,
        @Parameter(description = "방명록 ID") @PathVariable guestbookId: String,
    ): ResponseEntity<Unit>

    @Operation(
        summary = "방명록 목록 조회",
        description = "모든 방명록 글을 조회합니다. 최신순으로 정렬됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "방명록 목록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GetGuestbooksSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": [
                                    {
                                      "guestbookId": "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890",
                                      "author": {
                                        "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                        "nickname": "Luigi",
                                        "profileImageUrl": "https://example.com/profile.jpg",
                                        "username": "luigi99"
                                      },
                                      "content": "방명록 내용 1",
                                      "createdAt": "2024-01-09T12:00:00",
                                      "updatedAt": "2024-01-09T12:00:00"
                                    },
                                    {
                                      "guestbookId": "b1b2c3d4-e5f6-7890-a1b2-c3d4e5f67891",
                                      "author": {
                                        "memberId": "223e4567-e89b-12d3-a456-426614174001",
                                        "nickname": "Guest",
                                        "profileImageUrl": null,
                                        "username": "guest123"
                                      },
                                      "content": "두 번째 방명록 글입니다.",
                                      "createdAt": "2024-01-09T11:00:00",
                                      "updatedAt": "2024-01-09T11:00:00"
                                    }
                                  ],
                                  "error": null,
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getGuestbooks(): ResponseEntity<CommonResponse<List<GuestbookResponse>>>

    @Operation(
        summary = "단일 방명록 조회",
        description = "특정 방명록 글을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "방명록 조회 성공",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GetGuestbookSuccess",
                                value = """
                                {
                                  "success": true,
                                  "data": {
                                    "guestbookId": "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890",
                                    "author": {
                                      "memberId": "123e4567-e89b-12d3-a456-426614174000",
                                      "nickname": "Luigi",
                                      "profileImageUrl": "https://example.com/profile.jpg",
                                      "username": "luigi99"
                                    },
                                    "content": "안녕하세요! 블로그 잘 보고 있습니다.",
                                    "createdAt": "2024-01-09T12:00:00",
                                    "updatedAt": "2024-01-09T12:00:00"
                                  },
                                  "error": null,
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "방명록을 찾을 수 없음",
                content = [
                    Content(
                        schema = Schema(implementation = CommonResponse::class),
                        examples = [
                            ExampleObject(
                                name = "GuestbookNotFound",
                                value = """
                                {
                                  "success": false,
                                  "data": null,
                                  "error": {
                                    "code": "GUESTBOOK_001",
                                    "message": "방명록을 찾을 수 없습니다."
                                  },
                                  "timestamp": "2024-01-09T12:00:00"
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getGuestbook(
        @Parameter(description = "방명록 ID") @PathVariable guestbookId: String,
    ): ResponseEntity<CommonResponse<GuestbookResponse>>
}

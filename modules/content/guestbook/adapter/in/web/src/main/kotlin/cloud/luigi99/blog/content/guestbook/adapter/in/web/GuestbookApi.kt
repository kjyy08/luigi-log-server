package cloud.luigi99.blog.content.guestbook.adapter.`in`.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.CreateGuestbookRequest
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.GuestbookResponse
import cloud.luigi99.blog.content.guestbook.adapter.`in`.web.dto.ModifyGuestbookRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
            ApiResponse(responseCode = "201", description = "방명록 작성 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
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
            ApiResponse(responseCode = "200", description = "방명록 수정 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            ApiResponse(responseCode = "404", description = "방명록을 찾을 수 없음"),
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
            ApiResponse(responseCode = "204", description = "방명록 삭제 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "방명록을 찾을 수 없음"),
        ],
    )
    fun deleteGuestbook(
        @AuthenticationPrincipal memberId: String,
        @Parameter(description = "방명록 ID") @PathVariable guestbookId: String,
    ): ResponseEntity<Unit>

    @Operation(
        summary = "방명록 목록 조회",
        description = "모든 방명록 글을 조회합니다. 비회원도 조회 가능합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "방명록 목록 조회 성공"),
        ],
    )
    fun getGuestbooks(): ResponseEntity<CommonResponse<List<GuestbookResponse>>>

    @Operation(
        summary = "단일 방명록 조회",
        description = "특정 방명록 글을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "방명록 조회 성공"),
            ApiResponse(responseCode = "404", description = "방명록을 찾을 수 없음"),
        ],
    )
    fun getGuestbook(
        @Parameter(description = "방명록 ID") @PathVariable guestbookId: String,
    ): ResponseEntity<CommonResponse<GuestbookResponse>>
}

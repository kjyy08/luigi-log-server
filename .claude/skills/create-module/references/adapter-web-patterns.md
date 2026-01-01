# Adapter/In/Web Patterns

## API Interface (Swagger)

```kotlin
@Tag(name = "Member", description = "회원 정보 조회 API")
interface MemberApi {

    @Operation(
        summary = "현재 로그인한 회원 정보 조회",
        description = "JWT 토큰을 통해 인증된 현재 사용자의 기본 정보를 조회합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        ],
    )
    fun getCurrentMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<MemberResponse>>

    @Operation(
        summary = "회원 탈퇴",
        description = "현재 로그인한 회원의 계정을 탈퇴합니다.",
        security = [SecurityRequirement(name = "Bearer Authentication")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        ],
    )
    fun deleteMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<Unit>
}
```

**Pattern:**
- Separate interface for Swagger docs
- `@Tag`, `@Operation`, `@ApiResponses`
- Security requirements
- Korean descriptions

## Controller

```kotlin
/**
 * 회원 컨트롤러
 *
 * 회원 정보 조회 및 회원 탈퇴 등 회원 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberQueryFacade: MemberQueryFacade,
    private val memberCommandFacade: MemberCommandFacade,
) : MemberApi {

    @GetMapping("/me")
    override fun getCurrentMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<CommonResponse<MemberResponse>> {
        log.info { "Getting current member info for: $memberId" }

        val response = memberQueryFacade.getCurrentMember().execute(
            GetCurrentMemberUseCase.Query(memberId),
        )

        return ResponseEntity.ok(
            CommonResponse.success(
                MemberResponse(
                    memberId = response.memberId,
                    email = response.email,
                    username = response.username,
                ),
            ),
        )
    }

    @DeleteMapping
    override fun deleteMember(
        @AuthenticationPrincipal memberId: String,
    ): ResponseEntity<Unit> {
        log.info { "Deleting member: $memberId" }

        memberCommandFacade.deleteMember().execute(
            DeleteMemberUseCase.Command(memberId),
        )

        return ResponseEntity.noContent().build()
    }
}
```

**Key patterns:**
- Implements API interface
- Inject Facades (not individual UseCases)
- `@AuthenticationPrincipal` extracts user ID from JWT
- `CommonResponse.success()` wraps data
- UseCase Response → DTO conversion
- **Korean KDoc**

## Request DTO

```kotlin
data class UpdateProfileRequest(
    @field:Schema(description = "닉네임", example = "CodingMaster")
    val nickname: String?,

    @field:Schema(description = "자기소개", example = "새로운 소개글입니다.")
    val bio: String?,

    @field:Schema(description = "기술 스택", example = "[\"Kotlin\", \"Spring\"]")
    val techStack: List<String>?,
)
```

## Response DTO

```kotlin
data class MemberResponse(
    @field:Schema(description = "회원 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val memberId: String,

    @field:Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @field:Schema(description = "사용자 이름", example = "Luigi99")
    val username: String,
)
```

**Pattern:**
- `@field:Schema` with Korean description + example
- Nullable fields for optional data (`String?`)

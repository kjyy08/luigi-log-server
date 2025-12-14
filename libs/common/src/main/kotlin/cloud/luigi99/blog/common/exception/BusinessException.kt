package cloud.luigi99.blog.common.exception

abstract class BusinessException(val errorCode: ErrorCode, override val message: String = errorCode.message) :
    RuntimeException(message)

enum class ErrorCode(val code: String, val message: String, val status: Int) {
    // Common
    INVALID_INPUT("COMMON_001", "잘못된 요청입니다.", 400),
    INTERNAL_SERVER_ERROR("COMMON_002", "서버 내부 오류가 발생했습니다.", 500),

    // Auth
    UNAUTHORIZED("AUTH_001", "인증에 실패했습니다.", 401),
    INVALID_TOKEN("AUTH_002", "유효하지 않은 토큰입니다.", 401),
    ACCESS_DENIED("AUTH_003", "접근 권한이 없습니다.", 403),

    // Member
    MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다.", 404),

    // Auth
    CREDENTIAL_NOT_FOUND("CREDENTIAL_001", "인증 정보를 찾을 수 없습니다.", 404),

    // Profile
    PROFILE_NOT_FOUND("PROFILE_001", "프로필을 찾을 수 없습니다.", 404),

    // Content (Post, Comment)
    POST_NOT_FOUND("CONTENT_001", "게시글을 찾을 수 없습니다.", 404),
    SLUG_ALREADY_EXISTS("CONTENT_002", "이미 존재하는 슬러그입니다.", 409),
    COMMENT_NOT_FOUND("CONTENT_003", "댓글을 찾을 수 없습니다.", 404),

    // Media
    FILE_UPLOAD_FAILED("MEDIA_001", "파일 업로드에 실패했습니다.", 500),
    INVALID_FILE_TYPE("MEDIA_002", "지원하지 않는 파일 형식입니다.", 400),
}

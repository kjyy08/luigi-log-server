package cloud.luigi99.blog.common.exception

/**
 * 개인 기술 블로그 프로젝트에서 사용하는 에러 코드 정의
 *
 * 각 에러 코드는 고유한 코드와 설명을 가지며, 클라이언트에서 에러를 구분하고
 * 적절한 처리를 할 수 있도록 합니다.
 *
 * 에러 코드 네이밍 규칙:
 * - 도메인_액션_상태 형태로 구성
 * - 일반적인 에러는 COMMON_ 접두사 사용
 * - 도메인별 에러는 도메인명을 접두사로 사용 (USER_, POST_, COMMENT_ 등)
 */
enum class ErrorCode(
    /**
     * 에러 코드 문자열
     */
    val code: String,

    /**
     * 에러 설명
     */
    val description: String
) {

    // ===== 일반적인 비즈니스 에러 =====

    /**
     * 일반적인 비즈니스 로직 에러
     */
    COMMON_BUSINESS_ERROR("COMMON_BUSINESS_ERROR", "비즈니스 로직 처리 중 오류가 발생했습니다"),

    /**
     * 잘못된 요청 파라미터
     */
    COMMON_INVALID_PARAMETER("COMMON_INVALID_PARAMETER", "잘못된 요청 파라미터입니다"),

    /**
     * 잘못된 입력값
     */
    COMMON_INVALID_INPUT("COMMON_INVALID_INPUT", "입력값이 유효하지 않습니다"),

    /**
     * 필수 값 누락
     */
    COMMON_REQUIRED_VALUE_MISSING("COMMON_REQUIRED_VALUE_MISSING", "필수 입력값이 누락되었습니다"),

    /**
     * 서버 내부 오류
     */
    COMMON_INTERNAL_SERVER_ERROR("COMMON_INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"),

    // ===== 엔티티 관련 에러 =====

    /**
     * 엔티티를 찾을 수 없음
     */
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "요청한 데이터를 찾을 수 없습니다"),

    /**
     * 엔티티가 이미 존재함
     */
    ENTITY_ALREADY_EXISTS("ENTITY_ALREADY_EXISTS", "이미 존재하는 데이터입니다"),

    /**
     * 엔티티 생성 실패
     */
    ENTITY_CREATION_FAILED("ENTITY_CREATION_FAILED", "데이터 생성에 실패했습니다"),

    /**
     * 엔티티 수정 실패
     */
    ENTITY_UPDATE_FAILED("ENTITY_UPDATE_FAILED", "데이터 수정에 실패했습니다"),

    /**
     * 엔티티 삭제 실패
     */
    ENTITY_DELETE_FAILED("ENTITY_DELETE_FAILED", "데이터 삭제에 실패했습니다"),

    // ===== 유효성 검증 에러 =====

    /**
     * 이메일 형식 오류
     */
    VALIDATION_INVALID_EMAIL("VALIDATION_INVALID_EMAIL", "이메일 형식이 올바르지 않습니다"),

    /**
     * 비밀번호 형식 오류
     */
    VALIDATION_INVALID_PASSWORD("VALIDATION_INVALID_PASSWORD", "비밀번호 형식이 올바르지 않습니다"),

    /**
     * 제목 길이 초과
     */
    VALIDATION_TITLE_TOO_LONG("VALIDATION_TITLE_TOO_LONG", "제목이 너무 깁니다"),

    /**
     * 내용 길이 초과
     */
    VALIDATION_CONTENT_TOO_LONG("VALIDATION_CONTENT_TOO_LONG", "내용이 너무 깁니다"),

    /**
     * 태그 개수 초과
     */
    VALIDATION_TOO_MANY_TAGS("VALIDATION_TOO_MANY_TAGS", "태그 개수가 제한을 초과했습니다"),

    // ===== 권한 관련 에러 =====

    /**
     * 인증되지 않은 사용자
     */
    AUTH_UNAUTHENTICATED("AUTH_UNAUTHENTICATED", "로그인이 필요합니다"),

    /**
     * 권한 없음
     */
    AUTH_UNAUTHORIZED("AUTH_UNAUTHORIZED", "권한이 없습니다"),

    /**
     * 잘못된 인증 정보
     */
    AUTH_INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS", "인증 정보가 올바르지 않습니다"),

    /**
     * 토큰 만료
     */
    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED", "인증 토큰이 만료되었습니다"),

    /**
     * 잘못된 토큰
     */
    AUTH_INVALID_TOKEN("AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다"),

    // ===== 사용자 관련 에러 =====

    /**
     * 사용자를 찾을 수 없음
     */
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다"),

    /**
     * 사용자가 이미 존재함
     */
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다"),

    /**
     * 이메일 중복
     */
    USER_EMAIL_DUPLICATE("USER_EMAIL_DUPLICATE", "이미 사용 중인 이메일입니다"),

    /**
     * 비활성화된 사용자
     */
    USER_DEACTIVATED("USER_DEACTIVATED", "비활성화된 사용자입니다"),

    // ===== 블로그 포스트 관련 에러 =====

    /**
     * 포스트를 찾을 수 없음
     */
    POST_NOT_FOUND("POST_NOT_FOUND", "블로그 포스트를 찾을 수 없습니다"),

    /**
     * 포스트 제목 중복
     */
    POST_TITLE_DUPLICATE("POST_TITLE_DUPLICATE", "이미 존재하는 포스트 제목입니다"),

    /**
     * 임시저장 포스트가 아님
     */
    POST_NOT_DRAFT("POST_NOT_DRAFT", "임시저장 상태의 포스트가 아닙니다"),

    /**
     * 이미 발행된 포스트
     */
    POST_ALREADY_PUBLISHED("POST_ALREADY_PUBLISHED", "이미 발행된 포스트입니다"),

    // ===== 댓글 관련 에러 =====

    /**
     * 댓글을 찾을 수 없음
     */
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다"),

    /**
     * 댓글 작성자가 아님
     */
    COMMENT_NOT_AUTHOR("COMMENT_NOT_AUTHOR", "댓글 작성자가 아닙니다"),

    /**
     * 삭제된 댓글
     */
    COMMENT_DELETED("COMMENT_DELETED", "삭제된 댓글입니다"),

    // ===== 카테고리 관련 에러 =====

    /**
     * 카테고리를 찾을 수 없음
     */
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다"),

    /**
     * 카테고리 이름 중복
     */
    CATEGORY_NAME_DUPLICATE("CATEGORY_NAME_DUPLICATE", "이미 존재하는 카테고리 이름입니다"),

    /**
     * 사용 중인 카테고리 삭제 시도
     */
    CATEGORY_IN_USE("CATEGORY_IN_USE", "사용 중인 카테고리는 삭제할 수 없습니다"),

    // ===== 파일/미디어 관련 에러 =====

    /**
     * 파일을 찾을 수 없음
     */
    FILE_NOT_FOUND("FILE_NOT_FOUND", "파일을 찾을 수 없습니다"),

    /**
     * 지원하지 않는 파일 형식
     */
    FILE_UNSUPPORTED_FORMAT("FILE_UNSUPPORTED_FORMAT", "지원하지 않는 파일 형식입니다"),

    /**
     * 파일 크기 초과
     */
    FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEEDED", "파일 크기가 제한을 초과했습니다"),

    /**
     * 파일 업로드 실패
     */
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다"),

    // ===== 검색 관련 에러 =====

    /**
     * 검색 키워드가 너무 짧음
     */
    SEARCH_KEYWORD_TOO_SHORT("SEARCH_KEYWORD_TOO_SHORT", "검색 키워드가 너무 짧습니다"),

    /**
     * 검색 서비스 연결 실패
     */
    SEARCH_SERVICE_UNAVAILABLE("SEARCH_SERVICE_UNAVAILABLE", "검색 서비스를 사용할 수 없습니다"),

    // ===== 상태 전환 에러 =====

    /**
     * 잘못된 상태 전환
     */
    STATE_INVALID_TRANSITION("STATE_INVALID_TRANSITION", "잘못된 상태 전환입니다"),

    /**
     * 이미 처리된 상태
     */
    STATE_ALREADY_PROCESSED("STATE_ALREADY_PROCESSED", "이미 처리된 상태입니다"),

    /**
     * 처리할 수 없는 상태
     */
    STATE_CANNOT_PROCESS("STATE_CANNOT_PROCESS", "현재 상태에서는 처리할 수 없습니다");

    /**
     * 에러 코드를 문자열로 반환합니다.
     *
     * @return 에러 코드 문자열
     */
    override fun toString(): String = code

    companion object {

        /**
         * 에러 코드 문자열로부터 ErrorCode를 찾습니다.
         *
         * @param code 찾을 에러 코드 문자열
         * @return 해당하는 ErrorCode, 없으면 null
         */
        fun fromCode(code: String): ErrorCode? {
            return ErrorCode.entries.find { it.code == code }
        }

        /**
         * 에러 코드 문자열로부터 ErrorCode를 찾습니다. 없으면 기본값을 반환합니다.
         *
         * @param code 찾을 에러 코드 문자열
         * @param defaultValue 기본값 (기본: COMMON_BUSINESS_ERROR)
         * @return 해당하는 ErrorCode 또는 기본값
         */
        fun fromCodeOrDefault(code: String, defaultValue: ErrorCode = COMMON_BUSINESS_ERROR): ErrorCode {
            return fromCode(code) ?: defaultValue
        }

        /**
         * 모든 에러 코드를 코드 문자열 리스트로 반환합니다.
         *
         * @return 모든 에러 코드 문자열 리스트
         */
        fun getAllCodes(): List<String> {
            return ErrorCode.entries.map { it.code }
        }

        /**
         * 특정 접두사로 시작하는 모든 에러 코드를 반환합니다.
         *
         * @param prefix 찾을 접두사
         * @return 해당 접두사로 시작하는 ErrorCode 리스트
         */
        fun getByPrefix(prefix: String): List<ErrorCode> {
            return ErrorCode.entries.filter { it.code.startsWith(prefix) }
        }
    }
}
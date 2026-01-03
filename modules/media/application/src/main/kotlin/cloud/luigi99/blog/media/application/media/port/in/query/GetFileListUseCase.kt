package cloud.luigi99.blog.media.application.media.port.`in`.query

/**
 * 파일 목록 조회 UseCase
 *
 * 모든 파일 메타데이터 목록을 조회합니다.
 */
interface GetFileListUseCase {
    /**
     * 파일 목록을 조회합니다.
     *
     * @param query 조회 쿼리
     * @return 파일 목록
     */
    fun execute(query: Query): Response

    /**
     * 파일 목록 조회 쿼리
     */
    data class Query(val dummy: String = "")

    /**
     * 파일 목록 조회 응답
     *
     * @property files 파일 목록
     */
    data class Response(val files: List<FileSummary>)

    /**
     * 파일 요약 정보
     *
     * @property fileId 파일 ID
     * @property originalFileName 원본 파일명
     * @property mimeType MIME 타입
     * @property fileSize 파일 크기 (bytes)
     * @property publicUrl Public URL
     * @property createdAt 생성 시간
     */
    data class FileSummary(
        val fileId: String,
        val originalFileName: String,
        val mimeType: String,
        val fileSize: Long,
        val publicUrl: String,
        val createdAt: java.time.LocalDateTime?,
    )
}

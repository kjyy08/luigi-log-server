package cloud.luigi99.blog.media.application.media.port.`in`.query

/**
 * 파일 조회 UseCase
 *
 * ID로 파일 메타데이터를 조회합니다.
 */
interface GetFileUseCase {
    /**
     * 파일을 조회합니다.
     *
     * @param query 조회 쿼리
     * @return 파일 정보
     */
    fun execute(query: Query): Response

    /**
     * 파일 조회 쿼리
     *
     * @property fileId 파일 ID
     */
    data class Query(val fileId: String)

    /**
     * 파일 조회 응답
     *
     * @property fileId 파일 ID
     * @property originalFileName 원본 파일명
     * @property mimeType MIME 타입
     * @property fileSize 파일 크기 (bytes)
     * @property storageKey 저장소 키
     * @property publicUrl Public URL
     * @property createdAt 생성 시간
     */
    data class Response(
        val fileId: String,
        val originalFileName: String,
        val mimeType: String,
        val fileSize: Long,
        val storageKey: String,
        val publicUrl: String,
        val createdAt: java.time.LocalDateTime?,
    )
}

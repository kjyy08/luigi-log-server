package cloud.luigi99.blog.media.application.media.port.`in`.command

/**
 * 파일 업로드 UseCase
 *
 * 파일을 Storage에 업로드하고 메타데이터를 저장합니다.
 */
interface UploadFileUseCase {
    /**
     * 파일을 업로드합니다.
     *
     * @param command 업로드 명령
     * @return 업로드 결과
     */
    fun execute(command: Command): Response

    /**
     * 파일 업로드 명령
     *
     * @property originalFileName 원본 파일명
     * @property mimeType MIME 타입
     * @property fileSize 파일 크기 (bytes)
     * @property fileData 파일 바이트 데이터
     */
    data class Command(
        val originalFileName: String,
        val mimeType: String,
        val fileSize: Long,
        val fileData: ByteArray,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Command

            if (originalFileName != other.originalFileName) return false
            if (mimeType != other.mimeType) return false
            if (fileSize != other.fileSize) return false
            if (!fileData.contentEquals(other.fileData)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = originalFileName.hashCode()
            result = 31 * result + mimeType.hashCode()
            result = 31 * result + fileSize.hashCode()
            result = 31 * result + fileData.contentHashCode()
            return result
        }
    }

    /**
     * 파일 업로드 응답
     *
     * @property fileId 파일 ID
     * @property originalFileName 원본 파일명
     * @property mimeType MIME 타입
     * @property fileSize 파일 크기 (bytes)
     * @property storageKey 저장소 키
     * @property publicUrl Public URL
     */
    data class Response(
        val fileId: String,
        val originalFileName: String,
        val mimeType: String,
        val fileSize: Long,
        val storageKey: String,
        val publicUrl: String,
    )
}

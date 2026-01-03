package cloud.luigi99.blog.media.adapter.out.storage.r2

import cloud.luigi99.blog.media.application.media.port.out.StoragePort
import cloud.luigi99.blog.media.domain.media.exception.FileUploadFailedException
import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

private val logger = KotlinLogging.logger {}

/**
 * Cloudflare R2 Storage Adapter
 *
 * AWS S3 호환 API를 사용하여 R2에 파일을 저장/삭제하고 Public URL을 생성합니다.
 */
@Component
class CloudflareR2StorageAdapter(
    private val s3Client: S3Client,
    private val bucketName: String,
    private val publicUrl: String,
) : StoragePort {
    /**
     * 파일을 R2에 업로드합니다.
     *
     * @param storageKey 저장소 키
     * @param fileData 파일 바이트 데이터
     * @param contentType MIME 타입
     * @throws FileUploadFailedException 업로드 실패 시
     */
    override fun upload(storageKey: StorageKey, fileData: ByteArray, contentType: String) {
        try {
            val request =
                PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(storageKey.value)
                    .contentType(contentType)
                    .contentLength(fileData.size.toLong())
                    .build()

            s3Client.putObject(request, RequestBody.fromBytes(fileData))

            logger.info { "파일 업로드 성공: ${storageKey.value}" }
        } catch (e: Exception) {
            logger.error(e) { "파일 업로드 실패: ${storageKey.value}" }
            throw FileUploadFailedException("파일 업로드에 실패했습니다: ${e.message}")
        }
    }

    /**
     * R2에서 파일을 삭제합니다.
     *
     * @param storageKey 저장소 키
     */
    override fun delete(storageKey: StorageKey) {
        try {
            val request =
                DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(storageKey.value)
                    .build()

            s3Client.deleteObject(request)

            logger.info { "파일 삭제 성공: ${storageKey.value}" }
        } catch (e: Exception) {
            logger.error(e) { "파일 삭제 실패: ${storageKey.value}" }
        }
    }

    /**
     * 파일의 Public URL을 생성합니다.
     *
     * @param storageKey 저장소 키
     * @return Public URL
     */
    override fun getPublicUrl(storageKey: StorageKey): PublicUrl = PublicUrl("$publicUrl/${storageKey.value}")
}

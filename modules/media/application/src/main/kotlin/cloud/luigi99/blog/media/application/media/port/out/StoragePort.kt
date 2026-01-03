package cloud.luigi99.blog.media.application.media.port.out

import cloud.luigi99.blog.media.domain.media.vo.PublicUrl
import cloud.luigi99.blog.media.domain.media.vo.StorageKey

/**
 * 파일 저장소 Port
 *
 * 파일 업로드/삭제/URL 생성 작업을 추상화합니다.
 */
interface StoragePort {
    /**
     * 파일을 저장소에 업로드합니다.
     *
     * @param storageKey 저장소 키
     * @param fileData 파일 바이트 데이터
     * @param contentType MIME 타입
     */
    fun upload(storageKey: StorageKey, fileData: ByteArray, contentType: String)

    /**
     * 저장소에서 파일을 삭제합니다.
     *
     * @param storageKey 저장소 키
     */
    fun delete(storageKey: StorageKey)

    /**
     * 파일의 Public URL을 생성합니다.
     *
     * @param storageKey 저장소 키
     * @return Public URL
     */
    fun getPublicUrl(storageKey: StorageKey): PublicUrl
}

package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject
import cloud.luigi99.blog.media.domain.media.exception.FileSizeExceededException

/**
 * 파일 크기 Value Object
 *
 */
@JvmInline
value class FileSize(val bytes: Long) : ValueObject {
    init {
        require(bytes > 0) { "File size must be positive" }
        if (bytes > MAX_BYTES) {
            throw FileSizeExceededException()
        }
    }

    /**
     * 파일 크기를 메가바이트로 변환합니다.
     *
     * @return 메가바이트 단위의 파일 크기
     */
    fun toMegaBytes(): Double = bytes.toDouble() / 1024 / 1024

    companion object {
        const val MAX_BYTES: Long = 5L * 1024L * 1024L
    }
}

package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * 파일 크기 Value Object
 *
 */
@JvmInline
value class FileSize(val bytes: Long) : ValueObject {
    init {
        require(bytes > 0) { "File size must be positive" }
    }

    /**
     * 파일 크기를 메가바이트로 변환합니다.
     *
     * @return 메가바이트 단위의 파일 크기
     */
    fun toMegaBytes(): Double = bytes.toDouble() / 1024 / 1024
}

package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject
import cloud.luigi99.blog.media.domain.media.exception.InvalidFileTypeException

/**
 * MIME 타입 Value Object
 *
 */
@JvmInline
value class MimeType(val value: String) : ValueObject {
    init {
        if (value !in ALLOWED_TYPES) {
            throw InvalidFileTypeException()
        }
    }

    companion object {
        /**
         * 허용된 MIME 타입 목록
         */
        val ALLOWED_TYPES =
            setOf(
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/webp",
            )

        private val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)

        /**
         * 주어진 MIME 타입이 허용되는지 확인합니다.
         *
         * @param value 확인할 MIME 타입
         * @return 허용 여부
         */
        fun isAllowed(value: String): Boolean = value in ALLOWED_TYPES

        /**
         * 선언된 MIME 타입과 파일 시그니처(magic bytes)가 일치하는지 확인합니다.
         */
        fun matchesMagicBytes(value: String, bytes: ByteArray): Boolean =
            when (value) {
                "image/jpeg" -> {
                    bytes.startsWithBytes(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()))
                }

                "image/png" -> {
                    bytes.startsWithBytes(PNG_SIGNATURE)
                }

                "image/gif" -> {
                    bytes.startsWithBytes("GIF87a".toByteArray()) ||
                        bytes.startsWithBytes("GIF89a".toByteArray())
                }

                "image/webp" -> {
                    bytes.size >= 12 &&
                        bytes.startsWithBytes("RIFF".toByteArray()) &&
                        bytes.sliceArray(8 until 12).contentEquals("WEBP".toByteArray())
                }

                else -> {
                    false
                }
            }

        private fun ByteArray.startsWithBytes(prefix: ByteArray): Boolean =
            size >= prefix.size && prefix.indices.all { this[it] == prefix[it] }
    }
}

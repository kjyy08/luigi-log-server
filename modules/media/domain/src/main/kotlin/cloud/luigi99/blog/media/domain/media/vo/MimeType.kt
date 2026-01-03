package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject

/**
 * MIME 타입 Value Object
 *
 */
@JvmInline
value class MimeType(val value: String) : ValueObject {
    init {
        require(value in ALLOWED_TYPES) {
            "Unsupported MIME type: $value. Allowed types: ${ALLOWED_TYPES.joinToString()}"
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

        /**
         * 주어진 MIME 타입이 허용되는지 확인합니다.
         *
         * @param value 확인할 MIME 타입
         * @return 허용 여부
         */
        fun isAllowed(value: String): Boolean = value in ALLOWED_TYPES
    }
}

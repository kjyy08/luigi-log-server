package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.time.LocalDateTime
import java.util.UUID

/**
 * 저장소 파일 키 Value Object
 *
 * 형식: {year}/{month}/{uuid}.{ext}
 * 예: 2025/01/550e8400-e29b-41d4-a716-446655440000.png
 */
@JvmInline
value class StorageKey(val value: String) : ValueObject {
    init {
        require(value.isNotBlank()) { "Storage key cannot be blank" }
    }

    companion object {
        /**
         * 파일 확장자를 기반으로 저장 키를 생성합니다.
         *
         * @param originalFileName 원본 파일명 (확장자 추출용)
         * @return 생성된 StorageKey (년도/월/UUID.확장자 형식)
         */
        fun generate(originalFileName: String): StorageKey {
            val now = LocalDateTime.now()
            val year = now.year
            val month =
                now.monthValue
                    .toString()
                    .padStart(2, '0')
            val uuid = UUID.randomUUID()
            val extension = originalFileName.substringAfterLast('.', "")

            val key =
                if (extension.isNotEmpty()) {
                    "$year/$month/$uuid.$extension"
                } else {
                    "$year/$month/$uuid"
                }

            return StorageKey(key)
        }
    }
}

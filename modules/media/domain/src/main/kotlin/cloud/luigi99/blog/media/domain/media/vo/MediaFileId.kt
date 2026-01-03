package cloud.luigi99.blog.media.domain.media.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.util.UUID

/**
 * 미디어 파일 ID Value Object
 *
 * UUID 기반의 고유한 파일 식별자입니다.
 */
@JvmInline
value class MediaFileId(val value: UUID) : ValueObject {
    companion object {
        /**
         * 새로운 미디어 파일 ID를 생성합니다.
         *
         * @return 랜덤 UUID 기반의 새 MediaFileId
         */
        fun generate(): MediaFileId = MediaFileId(UUID.randomUUID())

        /**
         * UUID로부터 MediaFileId를 생성합니다.
         *
         * @param value UUID 값
         * @return MediaFileId 인스턴스
         */
        fun from(value: UUID): MediaFileId = MediaFileId(value)

        /**
         * 문자열 UUID로부터 MediaFileId를 생성합니다.
         *
         * @param value UUID 문자열
         * @return MediaFileId 인스턴스
         * @throws IllegalArgumentException 유효하지 않은 UUID 형식인 경우
         */
        fun from(value: String): MediaFileId = MediaFileId(UUID.fromString(value))
    }
}

package cloud.luigi99.blog.content.domain.post.vo

import cloud.luigi99.blog.common.domain.ValueObject
import java.io.Serializable
import java.util.UUID

/**
 * Post의 고유 식별자
 *
 * UUID를 사용하여 Post를 고유하게 식별합니다.
 *
 * @property value UUID 값
 */
@JvmInline
value class PostId(val value: UUID) :
    ValueObject,
    Serializable {
    companion object {
        /**
         * 새로운 PostId를 생성합니다.
         *
         * @return 랜덤 UUID로 생성된 PostId
         */
        fun generate(): PostId = PostId(UUID.randomUUID())

        /**
         * 문자열로부터 PostId를 생성합니다.
         *
         * @param value UUID 형식의 문자열
         * @return 변환된 PostId
         * @throws IllegalArgumentException UUID 형식이 잘못된 경우
         */
        fun from(value: String): PostId = PostId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}

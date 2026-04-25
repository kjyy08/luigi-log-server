package cloud.luigi99.blog.content.post.adapter.out.persistence.redis

import cloud.luigi99.blog.content.post.application.port.out.PostViewCountDeduplicationPort
import cloud.luigi99.blog.content.post.domain.vo.PostId
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.time.Duration

/**
 * Redis SET NX EX 기반 게시글 조회수 중복 방지 어댑터.
 */
@Component
class RedisPostViewCountDeduplicationAdapter(
    private val redisTemplate: StringRedisTemplate,
) :
    PostViewCountDeduplicationPort {
    override fun isUniqueView(postId: PostId, visitorKey: String): Boolean {
        val key = "post:view:dedupe:${postId.value}:${sha256(visitorKey)}"

        return redisTemplate
            .opsForValue()
            .setIfAbsent(key, "1", VIEW_DEDUPE_TTL) == true
    }

    private fun sha256(value: String): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte) }

    companion object {
        private val VIEW_DEDUPE_TTL: Duration = Duration.ofHours(24)
    }
}

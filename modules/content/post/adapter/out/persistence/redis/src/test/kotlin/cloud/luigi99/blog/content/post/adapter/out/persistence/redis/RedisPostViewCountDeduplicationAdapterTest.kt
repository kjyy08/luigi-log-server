package cloud.luigi99.blog.content.post.adapter.out.persistence.redis

import cloud.luigi99.blog.content.post.domain.vo.PostId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class RedisPostViewCountDeduplicationAdapterTest :
    BehaviorSpec({
        val redisTemplate = mockk<StringRedisTemplate>()
        val valueOperations = mockk<ValueOperations<String, String>>()
        val adapter = RedisPostViewCountDeduplicationAdapter(redisTemplate)
        val postId = PostId.generate()
        val visitorKey = "anonymous:visitor-key"

        Given("Redis dedupe key가 없을 때") {
            every { redisTemplate.opsForValue() } returns valueOperations
            every { valueOperations.setIfAbsent(any(), "1", Duration.ofHours(24)) } returns true

            When("고유 조회 여부를 확인하면") {
                val result = adapter.isUniqueView(postId, visitorKey)

                Then("true를 반환하고 24시간 TTL로 SET NX를 호출한다") {
                    result shouldBe true
                    verify(exactly = 1) {
                        valueOperations.setIfAbsent(
                            match { it.startsWith("post:view:dedupe:${postId.value}:") },
                            "1",
                            Duration.ofHours(24),
                        )
                    }
                }
            }
        }

        Given("Redis dedupe key가 이미 있을 때") {
            every { redisTemplate.opsForValue() } returns valueOperations
            every { valueOperations.setIfAbsent(any(), "1", Duration.ofHours(24)) } returns false

            When("고유 조회 여부를 확인하면") {
                val result = adapter.isUniqueView(postId, visitorKey)

                Then("false를 반환한다") {
                    result shouldBe false
                }
            }
        }

        Given("Redis가 예외를 던질 때") {
            every { redisTemplate.opsForValue() } throws IllegalStateException("redis down")

            Then("예외를 그대로 전파해 application service에서 조회 성공 우선 정책으로 처리하게 한다") {
                io.kotest.assertions.throwables.shouldThrow<IllegalStateException> {
                    adapter.isUniqueView(postId, visitorKey)
                }
            }
        }
    })

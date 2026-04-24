package cloud.luigi99.blog.content.post.application.service.query

import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode
import cloud.luigi99.blog.content.post.application.port.`in`.query.GetPostsUseCase
import cloud.luigi99.blog.content.post.application.port.out.MemberClient
import cloud.luigi99.blog.content.post.application.port.out.PostRepository
import cloud.luigi99.blog.content.post.domain.vo.ContentType
import cloud.luigi99.blog.content.post.domain.vo.PostId
import cloud.luigi99.blog.content.post.domain.vo.PostStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class GetPostsService(private val postRepository: PostRepository, private val memberClient: MemberClient) :
    GetPostsUseCase {
    @Transactional(readOnly = true)
    override fun execute(query: GetPostsUseCase.Query): GetPostsUseCase.Response {
        val limit = query.limit?.coerceIn(1, 50) ?: 20
        val status = query.status?.let { parseEnum<PostStatus>(it, "status") }
        val type = query.type?.let { parseEnum<ContentType>(it, "type") }
        val cursor = query.cursor?.let { decodeCursor(it) }

        log.info {
            "Listing posts with filters - status: ${query.status}, type: ${query.type}, q: ${query.q}, limit: $limit"
        }

        val result = postRepository.search(status, type, query.q, limit, cursor)
        val posts = result.posts
        val commentCounts = postRepository.countCommentsByPostIds(posts.map { it.entityId })
        val memberIds =
            posts
                .map {
                    it.memberId.value
                        .toString()
                }.distinct()
        val authors = memberClient.getAuthors(memberIds)

        val summaries =
            posts.map { post ->
                val memberIdStr =
                    post.memberId.value
                        .toString()
                val author = authors[memberIdStr] ?: MemberClient.Author(memberIdStr, "Unknown", null, "unknown")

                GetPostsUseCase.PostSummary(
                    postId =
                        post.entityId.value
                            .toString(),
                    author =
                        GetPostsUseCase.AuthorInfo(
                            memberId = author.memberId,
                            nickname = author.nickname,
                            profileImageUrl = author.profileImageUrl,
                            username = author.username,
                        ),
                    title = post.title.value,
                    slug = post.slug.value,
                    type = post.type.name,
                    status = post.status.name,
                    tags = post.tags,
                    viewCount = post.viewCount,
                    commentCount = commentCounts[post.entityId] ?: 0,
                    createdAt = post.createdAt,
                )
            }

        return GetPostsUseCase.Response(
            posts = summaries,
            pageInfo =
                GetPostsUseCase.PageInfo(
                    limit = limit,
                    hasNext = result.hasNext,
                    nextCursor =
                        summaries.lastOrNull()?.takeIf { result.hasNext }?.let {
                            encodeCursor(
                                it.createdAt,
                                it.postId,
                            )
                        },
                ),
        )
    }

    private fun encodeCursor(createdAt: LocalDateTime?, postId: String): String? {
        if (createdAt == null) return null
        val raw = "$createdAt|$postId"
        return Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(raw.toByteArray(StandardCharsets.UTF_8))
    }

    private inline fun <reified T : Enum<T>> parseEnum(value: String, fieldName: String): T =
        try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            throw InvalidPostQueryException("Invalid $fieldName: $value", e)
        }

    private fun decodeCursor(cursor: String): PostRepository.PostCursor =
        try {
            val raw = String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8)
            val parts = raw.split("|", limit = 2)
            require(parts.size == 2) { "Invalid cursor" }
            PostRepository.PostCursor(LocalDateTime.parse(parts[0]), PostId(UUID.fromString(parts[1])))
        } catch (e: RuntimeException) {
            throw InvalidPostQueryException("Invalid cursor", e)
        }
}

private class InvalidPostQueryException(message: String, cause: Throwable? = null) :
    BusinessException(ErrorCode.INVALID_INPUT, message) {
    init {
        if (cause != null) initCause(cause)
    }
}

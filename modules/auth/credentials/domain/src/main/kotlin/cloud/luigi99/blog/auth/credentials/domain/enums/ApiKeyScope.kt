package cloud.luigi99.blog.auth.credentials.domain.enums

enum class ApiKeyScope(val value: String) {
    POST_CREATE("post:create"),
    POST_UPDATE("post:update"),
    POST_PUBLISH("post:publish"),
    MEDIA_UPLOAD("media:upload"),
    ;

    val authority: String
        get() = "SCOPE_$value"

    companion object {
        fun from(value: String): ApiKeyScope =
            entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unsupported API key scope: $value")
    }
}

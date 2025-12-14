package cloud.luigi99.blog.auth.credentials.domain.enums

enum class OAuthProvider {
    GITHUB,
    ;

    companion object {
        fun from(value: String): OAuthProvider =
            try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Unsupported OAuth provider: $value", e)
            }
    }
}

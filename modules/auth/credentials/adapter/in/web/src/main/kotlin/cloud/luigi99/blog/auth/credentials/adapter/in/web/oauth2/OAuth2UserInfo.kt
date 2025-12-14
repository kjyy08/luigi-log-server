package cloud.luigi99.blog.auth.credentials.adapter.`in`.web.oauth2

interface OAuth2UserInfo {
    fun getEmail(): String

    fun getUsername(): String

    fun getProviderId(): String
}

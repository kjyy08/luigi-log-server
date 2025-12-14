package cloud.luigi99.blog.auth.credentials.domain.vo

import cloud.luigi99.blog.auth.credentials.domain.enums.OAuthProvider
import cloud.luigi99.blog.common.domain.ValueObject

data class OAuthInfo(val provider: OAuthProvider, val providerId: String) : ValueObject {
    init {
        require(
            OAuthProvider.entries
                .toTypedArray()
                .contains(provider),
        ) { "Invalid OAuth provider: $provider" }
        require(providerId.isNotBlank()) { "Provider ID cannot be blank" }
    }
}

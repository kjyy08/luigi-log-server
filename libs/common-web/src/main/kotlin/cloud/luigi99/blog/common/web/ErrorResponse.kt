package cloud.luigi99.blog.common.web

data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val path: String? = null,
    val details: Map<String, Any>? = null
)
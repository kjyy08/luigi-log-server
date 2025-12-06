package cloud.luigi99.blog.app.config

import cloud.luigi99.blog.adapter.web.CommonResponse
import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<CommonResponse<Unit>> {
        log.info { "BusinessException: code=${e.errorCode.code}, message=${e.message}" }
        return ResponseEntity
            .status(e.errorCode.status)
            .body(CommonResponse.error(e.errorCode.code, e.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<CommonResponse<Unit>> {
        log.error(e) { "Unhandled exception occurred: ${e.message}" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            CommonResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.code,
                ErrorCode.INTERNAL_SERVER_ERROR.message,
            ),
        )
    }
}

package cloud.luigi99.blog.adapter.web

import cloud.luigi99.blog.adapter.web.dto.CommonResponse
import cloud.luigi99.blog.common.exception.BusinessException
import cloud.luigi99.blog.common.exception.ErrorCode
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MissingRequestHeaderException
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

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<CommonResponse<Unit>> {
        log.warn { "Access denied: ${e.message}" }
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(CommonResponse.error(ErrorCode.ACCESS_DENIED.code, ErrorCode.ACCESS_DENIED.message))
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeaderException(e: MissingRequestHeaderException): ResponseEntity<CommonResponse<Unit>> {
        log.warn { "Missing request header: ${e.headerName}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.error(ErrorCode.INVALID_INPUT.code, ErrorCode.INVALID_INPUT.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
    ): ResponseEntity<CommonResponse<Unit>> {
        log.warn { "Invalid request body: ${e.message}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.error(ErrorCode.INVALID_INPUT.code, ErrorCode.INVALID_INPUT.message))
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

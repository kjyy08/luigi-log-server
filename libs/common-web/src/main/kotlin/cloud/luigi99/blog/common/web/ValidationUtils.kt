package cloud.luigi99.blog.common.web

import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

object ValidationUtils {
    fun extractErrorMessages(bindingResult: BindingResult): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        
        bindingResult.allErrors.forEach { error ->
            when (error) {
                is FieldError -> {
                    errors[error.field] = error.defaultMessage ?: "Invalid value"
                }
                else -> {
                    errors["global"] = error.defaultMessage ?: "Validation failed"
                }
            }
        }
        
        return errors
    }
    
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}
package optimus.config

import optimus.dto.ExceptionResponse
import io.github.resilience4j.ratelimiter.RequestNotPermitted as RateLimited
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolation(e: ConstraintViolationException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val errors = e.constraintViolations.associate {
            it.propertyPath.toString().substringAfterLast(".") to it.message
        }
        val message = "${errors.size} constraint(s) were violated."
        val response = ExceptionResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            errorType = "Constraint Violation",
            message = message,
            path = fullPath,
            errors = errors
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidRequestBody(e: MethodArgumentNotValidException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "No message available")
        }
        val message = "The provided request body has ${errors.size} errors."
        val response = ExceptionResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            errorType = "Invalid Request Body",
            message = message,
            path = fullPath,
            errors = errors
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingParameter(e: MissingServletRequestParameterException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val message = "Required parameter '${e.parameterName}' of type '${e.parameterType}' is missing."
        val response = ExceptionResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            errorType = "Missing Parameter",
            message = message,
            path = fullPath
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun noHandler(e: NoHandlerFoundException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val message = "Endpoint ${e.httpMethod} ${e.requestURL} not found."
        val response = ExceptionResponse(
            status = HttpStatus.NOT_FOUND.value(),
            errorType = "No Handler Found",
            message = message,
            path = fullPath
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(RateLimited::class)
    fun rateLimited(e: RateLimited, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val message = "You have exceeded the request limit. Please try again later."
        val response = ExceptionResponse(
            status = HttpStatus.TOO_MANY_REQUESTS.value(),
            errorType = "Rate Limited",
            message = message,
            path = fullPath
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response)
    }

    @ExceptionHandler(InterruptedException::class)
    fun requestInterrupted(e: InterruptedException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val response = ExceptionResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            errorType = "Request Interrupted",
            message = "The request was interrupted during processing.",
            path = fullPath
        )

        Thread.currentThread().interrupt()
        logger.error("Request was interrupted at {}", fullPath, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun typeMismatch(e: MethodArgumentTypeMismatchException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val requiredType = e.requiredType?.simpleName ?: e.requiredType?.name ?: e.requiredType.toString()
        val message = "Failed to convert value '${e.value}' to required type '$requiredType' for parameter '${e.parameter.parameterName}'."
        val response = ExceptionResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            errorType = "Type Mismatch",
            message = message,
            path = fullPath
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun unsupportedMethod(e: HttpRequestMethodNotSupportedException, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val supportedMethods = e.supportedMethods?.joinToString(", ") ?: "GET"
        val message = "HTTP method '${e.method}' is not supported for this endpoint. Supported methods are: $supportedMethods"
        val response = ExceptionResponse(
            status = HttpStatus.METHOD_NOT_ALLOWED.value(),
            errorType = "Unsupported Method",
            message = message,
            path = fullPath
        )

        logClientError(fullPath, message)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun genericException(e: Exception, r: HttpServletRequest): ResponseEntity<ExceptionResponse> {
        val fullPath = buildFullPath(r)
        val response = ExceptionResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            errorType = "Internal Server Error",
            message = "An unexpected internal server error occurred.",
            path = fullPath
        )

        logger.error("Internal server error at {}", fullPath, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    private fun logClientError(fullPath: String, message: String) = logger.info("Client error at {} : {}", fullPath, message)

    private fun buildFullPath(r: HttpServletRequest): String {
        return if (r.queryString.isNullOrBlank()) {
            r.requestURI
        } else {
            "${r.requestURI}?${r.queryString}"
        }
    }
}

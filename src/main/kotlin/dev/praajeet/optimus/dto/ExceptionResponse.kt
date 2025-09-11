package dev.praajeet.optimus.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExceptionResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val errorType: String,
    val message: String,
    val path: String,
    val errors: Map<String, String>? = null,
)

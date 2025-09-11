package dev.praajeet.optimus.config

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitInterceptor(
    private val rateLimiterRegistry: RateLimiterRegistry,
) : HandlerInterceptor {
    private val rateLimiters = ConcurrentHashMap<String, RateLimiter>()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = getClientIp(request)
        val rateLimiter = rateLimiters.computeIfAbsent(ip) {
            createRateLimiter()
        }
        if (!rateLimiter.acquirePermission()) {
            throw RequestNotPermitted.createRequestNotPermitted(rateLimiter)
        }
        return true
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xHeader = request.getHeader("X-Forwarded-For")
        return if (xHeader == null || xHeader.isBlank()) {
            request.remoteAddr
        } else {
            xHeader.split(",").first()
        }
    }

    private fun createRateLimiter(): RateLimiter {
        val config = rateLimiterRegistry.getConfiguration("perClient").orElseThrow {
            IllegalArgumentException("Rate limiter configuration 'perClient' not found")
        }
        return RateLimiter.of("rate-limiter-${System.nanoTime()}", config)
    }
}

package dev.praajeet.optimus.controller

import dev.praajeet.optimus.dto.PrimeCount
import dev.praajeet.optimus.service.PrimeService
import dev.praajeet.optimus.util.MAX_PRIME
import dev.praajeet.optimus.util.MIN_PRIME
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RequestMapping("/api/v1")
@RestController
@Tag(name = "Basic Prime Operations")
class BasicPrimeController(private val primeService: PrimeService) {
    @Operation(
        summary = "Check if a number is prime",
        description = "A prime number is a number greater than `1` that has no positive divisors other than `1` and itself."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Boolean::class, example = "true")
        )]
    )
    @GetMapping("/isPrime")
    fun isPrime(
        @Parameter(description = "The number to check for primality.")
        @RequestParam num: Long
    ): Boolean = primeService.isPrime(num)


    @Operation(summary = "Find the `Nth` prime number")
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "104743")
        )]
    )
    @GetMapping("/nthPrime")
    fun nthPrime(
        @Parameter(description = "The 1-based index `N` of the prime number to find. Due to computational limitations, the maximum value allowed for `N` is `30,000,000,000,000`.")
        @RequestParam @Positive @Max(30_000_000_000_000L) n: Long
    ): Long? = primeService.nthPrime(n)

    @Operation(
        summary = "Count primes up to a given number",
        description = "`π(x)`, read as Pi of x is the number of primes less than or equal to `x`. The endpoint is accurate upto `1e15`. It is also accurate for any number within `1,000,000` from `XeY` or `2^X`."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrimeCount::class, example = """{ "count": 168, "is_exact": true }""")
        )]
    )
    @GetMapping("/primeCount")
    fun primeCount(
        @Parameter(description = "The upper limit (inclusive) for the prime count.")
        @RequestParam num: Long
    ): PrimeCount = primeService.primeCount(num).let {
        PrimeCount(it.first, it.second)
    }


    @Operation(
        summary = "Sum the first `N` prime numbers",
        description = "Calculates and returns the sum of the first N prime numbers."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "3682913")
        )]
    )
    @GetMapping("/primeSum")
    fun primeSum(
        @Parameter(description = "The count `N` of initial primes to sum. Due to computational limitations, the maximum value allowed for `N` is `100,000`.")
        @RequestParam @Min(1) @Max(100000) n: Int
    ): Long = primeService.primeSum(n)

    @Operation(
        summary = "Find the smallest prime strictly greater than the input number",
        description = "Finds and returns the smallest prime number that is strictly greater than the given number. Null is returned if no such prime exists."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "101")
        )]
    )
    @GetMapping("/nextPrime")
    fun nextPrime(
        @Parameter(description = "The number from which to find the next prime.")
        @RequestParam num: Long
    ): Long? = primeService.nextPrime(num)

    @Operation(
        summary = "Find the largest prime strictly less than the input number",
        description = "Finds and returns the largest prime number that is strictly less than the given number. Null is returned if no such prime exists."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "97")
        )]
    )
    @GetMapping("/previousPrime")
    fun previousPrime(
        @Parameter(description = "The number from which to find the previous prime.")
        @RequestParam num: Long
    ): Long? = primeService.previousPrime(num)

    @Operation(
        summary = "Generate a random prime within a given range",
        description = "Finds a random prime number within the specified inclusive range `[a, b]`. If no primes exist in the range, null is returned."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "541")
        )]
    )
    @GetMapping("/randomPrime")
    fun randomPrime(
        @Parameter(description = "The lower bound of the range (inclusive).")
        @RequestParam a: Long = Long.MIN_PRIME,
        @Parameter(description = "The upper bound of the range (inclusive).")
        @RequestParam b: Long = Long.MAX_PRIME
    ): Long? = primeService.randomPrime(a, b)
}

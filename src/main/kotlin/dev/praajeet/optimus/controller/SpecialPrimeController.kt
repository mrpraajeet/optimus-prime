package dev.praajeet.optimus.controller

import dev.praajeet.optimus.dto.ExceptionResponse
import dev.praajeet.optimus.service.PrimeService
import dev.praajeet.optimus.validator.Even
import dev.praajeet.optimus.validator.Prime
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.abs

@Validated
@RequestMapping("/api/v1")
@RestController
@Tag(name = "Special Prime Operations")
class SpecialPrimeController(private val primeService: PrimeService) {
    @Operation(
        summary = "Find the Nth palindromic prime",
        description = "A palindromic prime is a prime number that reads the same forwards and backward."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "101")
        )]
    )
    @GetMapping("/nthPalprime")
    fun nthPalprime(
        @Parameter(description = "The 1-based index `N` of the palindromic prime to find. Due to computational limitations, the maximum value allowed for `N` is `20000`.")
        @RequestParam @Positive @Max(20000) n: Int
    ): Long = primeService.nthPalprime(n)


    @Operation(
        summary = "Find primes related by an even difference",
        description = "Finds `p-d` and `p+d` if they are also prime, given a prime `p` and an even difference `d`."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = List::class, example = "[11, 23]")
        )]
    )
    @ApiResponse(
        responseCode = "400",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(
                implementation = ExceptionResponse::class,
                example = """{
                    "timestamp": "2025-09-11T10:12:05.530888129Z",
                    "status": 400,
                    "error_type": "Constraint Violation",
                    "message": "2 constraint(s) were violated.",
                    "path": "/api/v1/relatedPrimes?num=6&difference=3",
                    "errors": {
                        "num": "Input must be a prime number",
                        "difference": "Input must be an even number"
                    }
                }"""
            )
        )]
    )
    @GetMapping("/relatedPrimes")
    fun relatedPrimes(
        @Parameter(description = "The prime number to start from.")
        @RequestParam @Prime num: Long,
        @Parameter(description = "The even difference for related primes.")
        @RequestParam @Positive @Even difference: Long
    ): List<Long> = primeService.relatedPrimes(num, difference)


    @Operation(
        summary = "Check if a number is a k-almost prime",
        description = "A k-almost prime is a natural number with exactly `k` prime factors, counted with multiplicity. A semiprime is a 2-almost prime."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Boolean::class, example = "true")
        )]
    )
    @GetMapping("/isAlmost")
    fun isAlmost(
        @Parameter(description = "The number to check.")
        @RequestParam num: Long,
        @Parameter(description = "The number of prime factors (k) to check for. Defaults to 2 (semiprime).")
        @RequestParam @Positive k: Int = 2
    ): Boolean = primeService.primeFactors(abs(num)).size == k


    @Operation(
        summary = "Check if a prime is a balanced prime",
        description = "A balanced prime is a prime number that is the arithmetic mean of its nearest prime neighbors (the previous prime and the next prime)."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Boolean::class, example = "true")
        )]
    )
    @GetMapping("/isBalanced")
    fun isBalanced(
        @Parameter(description = "The prime number to check.")
        @RequestParam @Prime num: Long
    ): Boolean {
        val previousPrime = primeService.previousPrime(num) ?: return false
        val nextPrime = primeService.nextPrime(num) ?: return false
        return nextPrime - num == num - previousPrime
    }


    @Operation(
        summary = "Retrieve the list of known Mersenne primes within Long integer range",
        description = "A Mersenne prime is a prime number that is in the form of `2^p - 1`, where `p` must also be a prime number."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = List::class, example = "[3, 7, 31, 127, 8191, 131071, 524287, 2147483647, 2305843009213693952]")
        )]
    )
    @GetMapping("/mersennes")
    fun mersennes(): List<Long> = primeService.mersenne


    @Operation(
        summary = "Retrieve the list of known perfect numbers within Long integer range",
        description = "A perfect number is a positive integer that is equal to the sum of its positive divisors, excluding the number itself. All known perfect numbers are even and correspond to Mersenne primes."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = List::class, example = "[6, 28, 496, 8128, 33550336, 8589869056, 137438691328, 2305843008139952128]")
        )]
    )
    @GetMapping("/perfects")
    fun perfects(): List<Long> = primeService.perfect


    @Operation(
        summary = "Retrieve the list of known Fermat primes",
        description = "A Fermat prime is a Fermat number in the form of `2^(2^n) + 1` that is also prime. Only five such primes are known to exist."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = List::class, example = "[3, 5, 17, 257, 65537]")
        )]
    )
    @GetMapping("/fermats")
    fun fermats(): List<Long> = primeService.fermat
}

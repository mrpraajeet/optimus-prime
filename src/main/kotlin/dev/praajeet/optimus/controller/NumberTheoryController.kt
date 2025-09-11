package dev.praajeet.optimus.controller

import dev.praajeet.optimus.dto.GoldbachPair
import dev.praajeet.optimus.service.PrimeService
import dev.praajeet.optimus.validator.Even
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.abs

@Validated
@RequestMapping("/api/v1")
@RestController
@Tag(name = "Number Theory Operations")
class NumberTheoryController(private val primeService: PrimeService) {
    @Operation(
        summary = "Calculate the greatest common divisor (GCD) of two numbers",
        description = "`GCD(a, b)` is the largest positive integer that divides `a` and `b` without leaving a remainder."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Long::class, example = "6")
        )]
    )
    @GetMapping("/gcd")
    fun gcd(
        @Parameter(description = "The first number.")
        @RequestParam a: Long,
        @Parameter(description = "The second number.")
        @RequestParam b: Long
    ): Long = primeService.gcd(abs(a), abs(b))


    @Operation(
        summary = "Calculate the least common multiple (LCM) of two numbers",
        description = "`LCM(a, b)` is the smallest positive integer that can be divided by `a` and `b` without leaving a remainder."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = String::class, example = "48")
        )]
    )
    @GetMapping("/lcm")
    fun lcm(
        @Parameter(description = "The first number.")
        @RequestParam a: Long,
        @Parameter(description = "The second number.")
        @RequestParam b: Long
    ): String = primeService.lcm(a, b).toString()


    @Operation(
        summary = "Check if two numbers are coprime",
        description = "Two integers are coprime if their greatest common divisor (GCD) is `1`."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = Boolean::class, example = "true")
        )]
    )
    @GetMapping("/areCoprime")
    fun areCoprime(
        @Parameter(description = "The first number.")
        @RequestParam a: Long,
        @Parameter(description = "The second number.")
        @RequestParam b: Long
    ): Boolean = primeService.gcd(a, b) == 1L


    @Operation(
        summary = "Decompose a number into its prime factors",
        description = "Returns a list of prime numbers that, when multiplied together, equal the original number. Factors are repeated according to their multiplicity."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = List::class, example = "[2, 2, 3, 5]")
        )]
    )
    @GetMapping("/primeFactors")
    fun primeFactors(
        @Parameter(description = "The number to factorize.")
        @RequestParam num: Long
    ): List<Long> = primeService.primeFactors(abs(num))


    @Operation(
        summary = "Find a Goldbach pair for an even number",
        description = "Based on Goldbach's conjecture, which states every even integer greater than `2` is the sum of two primes. This endpoint finds one such pair."
    )
    @ApiResponse(
        responseCode = "200",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = GoldbachPair::class, example = """{ "smaller_prime": 5, "larger_prime": 23 }""")
        )]
    )
    @GetMapping("/goldbachPair")
    fun goldbachPair(
        @Parameter(description = "The even number (>= 4) to express as a sum of two primes.")
        @RequestParam @Even @Min(4) num: Long
    ): GoldbachPair? = primeService.goldbachPair(num)?.let {
        GoldbachPair(it.first, it.second)
    }
}

package optimus.service

import optimus.util.MAX_PRIME
import optimus.util.MIN_PRIME
import optimus.util.PrimeCounter
import optimus.util.PrimeTester
import org.springframework.stereotype.Service
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

@Service
class PrimeService {
    fun isPrime(n: Long): Boolean = PrimeTester.isPrime(n)

    fun nthPrime(n: Long): Long? = PrimeCounter.nthPrime(n)

    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    fun lcm(a: Long, b: Long): BigInteger {
        if (a == 0L || b == 0L) return BigInteger.ZERO
        val bigA = BigInteger.valueOf(a).abs()
        val bigB = BigInteger.valueOf(b).abs()
        return bigA.multiply(bigB).divide(bigA.gcd(bigB))
    }

    fun primeFactors(num: Long): List<Long> {
        if (isPrime(num)) return listOf(num)
        rootIfPerfectSquare(num)?.let {
            if (isPrime(it)) return listOf(it, it)
        }

        val factors = mutableListOf<Long>()
        var number = num
        listOf(2L, 3L, 5L).forEach {
            while (number % it == 0L) {
                factors.add(it)
                number /= it
            }
        }

        val spokes = longArrayOf(4L, 2L, 4L, 2L, 4L, 6L, 2L, 8L)
        var wheelIndex = 0
        var i = 7L
        while (i * i <= number) {
            while (number % i == 0L) {
                factors.add(i)
                number /= i
            }
            i += spokes[wheelIndex]
            wheelIndex = (wheelIndex + 1) % spokes.size
        }

        if (number > 1) factors.add(number)
        return factors
    }

    fun primeCount(num: Long): Pair<Long, Boolean> = PrimeCounter.primeCount(num)

    fun primeSum(n: Int): Long = primeSequence().take(n).sum()

    fun nextPrime(num: Long): Long? {
        if (num >= Long.MAX_PRIME) return null
        if (num < Long.MIN_PRIME) return Long.MIN_PRIME
        var current = num + 1 + num % 2
        while (current <= Long.MAX_PRIME) {
            if (isPrime(current)) return current
            current += 2
        }
        return null
    }

    fun previousPrime(num: Long): Long? {
        if (num <= Long.MIN_PRIME) return null
        if (num > Long.MAX_PRIME) Long.MAX_PRIME
        var current =  num - 1 - num % 2
        while (current >= Long.MIN_PRIME) {
            if (isPrime(current)) return current
            current -= 2
        }
        return null
    }

    fun randomPrime(start: Long, end: Long): Long? {
        if (start > end) return null
        val effectiveStart = max(start, Long.MIN_PRIME)
        val effectiveEnd = min(end, Long.MAX_PRIME)
        if (effectiveStart > effectiveEnd) return null

        val randomStart = Random.nextLong(effectiveStart, effectiveEnd + 1)
        var currentCandidate = randomStart
        while (true) {
            if (isPrime(currentCandidate)) return currentCandidate
            if (++currentCandidate > effectiveEnd) currentCandidate = effectiveStart
            if (currentCandidate == randomStart) return null
        }
    }

    fun nthPalprime(n: Int): Long {
        if (n <= 5) return listOf(2L, 3L, 5L, 7L, 11L)[n - 1]
        val compositeLastDigits = setOf(0, 2, 4, 5, 6, 8)
        var count = 5
        var k = 10
        while (true) {
            var firstDigit = k
            var powerOf10 = 1
            while (firstDigit > 9) {
                firstDigit /= 10
                powerOf10 *= 10
            }
            if (compositeLastDigits.contains(firstDigit)) {
                k = (firstDigit + 1) * powerOf10
                continue
            }
            var palindrome = k.toLong()
            var reverse = k / 10
            while (reverse > 0) {
                palindrome = palindrome * 10L + (reverse % 10)
                reverse /= 10
            }
            if (isPrime(palindrome) && ++count == n) return palindrome
            k++
        }
    }

    fun goldbachPair(num: Long): Pair<Long,Long>? {
        if (isPrime(num - 2)) return Pair(2, num - 2)
        var i = 3L
        while (i <= num / 2) {
            if (isPrime(i) && isPrime(num - i)) return Pair(i, num - i)
            i += 2
        }
        return null
    }

    fun relatedPrimes(num: Long, difference: Long): List<Long> {
        val relatedPrimes = mutableListOf<Long>()
        if (difference in 2 until num) {
            (num - difference).let { if (isPrime(it)) relatedPrimes.add(it) }
        }
        if (num <= Long.MAX_VALUE - difference) {
            (num + difference).let { if (isPrime(it)) relatedPrimes.add(it) }
        }
        return relatedPrimes
    }

    private fun primeSequence(): Sequence<Long> = sequence {
        yield(2L)
        var i = 3L
        while (i <= Long.MAX_PRIME) {
            if (PrimeTester.isPrime(i)) yield(i)
            i += 2
        }
    }

    private fun rootIfPerfectSquare(num: Long): Long? {
        val root = sqrt(num.toDouble()).toLong()
        return if (root * root == num) root else null
    }

    // Primes in the form 2^p-1 where p itself is prime
    val mersenne = listOf(
        3,                  // p = 2
        7,                  // p = 3
        31,                 // p = 5
        127,                // p = 7
        8191,               // p = 13
        131071,             // p = 17
        524287,             // p = 19
        2147483647,         // p = 31
        2305843009213693952 // p = 61
    )

    /* A number equal to it's sum of divisors except itself,
    They are also related to mersenne primes in the form 2^(p-1) * (2^p-1) */
    val perfect = listOf(6L, 28L, 496L, 8128L, 33550336L, 8589869056L, 137438691328L, 2305843008139952128L)

    // Primes in the form 2^(2^k) + 1
    val fermat = listOf(3L, 5L, 17L, 257L, 65537L)
}

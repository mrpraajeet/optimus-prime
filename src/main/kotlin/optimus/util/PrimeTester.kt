package optimus.util

import java.math.BigInteger

object PrimeTester {
    private val BASES = linkedSetOf(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L, 31L, 37L)

    fun isPrime(n: Long): Boolean {
        if (n < 2) return false
        if (BASES.contains(n)) return true
        if (BASES.any { n % it == 0L }) return false

        var s = 0
        var d = n - 1
        while (d % 2 == 0L) {
            d /= 2
            s++
        }

        if (n <= Integer.MAX_VALUE) return BASES.take(4).all { isProbablePrime(n, s, d, it) }
        return BASES.all { isProbablePrime(n, s, d, it) }
    }

    private fun isProbablePrime(n: Long, s: Int, d: Long, a: Long): Boolean {
        var x = modularExponentiation(a, d, n)
        if (x == 1L || x == n - 1L) return true

        repeat(s - 1) {
            x = modularExponentiation(x, 2, n)
            if (x == 1L) return false
            if (x == n - 1L) return true
        }

        return false
    }

    private fun modularExponentiation(baseArg: Long, expArg: Long, modArg: Long): Long {
        var result = BigInteger.ONE
        var exp = expArg
        var base = (baseArg % modArg).toBigInteger()
        val mod = modArg.toBigInteger()

        while (exp > 0) {
            if (exp % 2 == 1L) result = result.multiply(base).mod(mod)
            exp = exp shr 1
            base = base.multiply(base).mod(mod)
        }

        return result.toLong()
    }
}

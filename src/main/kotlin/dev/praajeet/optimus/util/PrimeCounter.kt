package dev.praajeet.optimus.util

import ch.obermuhlner.math.big.BigDecimalMath
import dev.praajeet.optimus.util.PrimeTester.isPrime
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs

object PrimeCounter {
    private val MC = MathContext(100)
    private val EULER_MASCHERONI = BigDecimal("0.577215664901532860606512090082402431042159335939923598805767234884867726777664670936947063291746749")
    private const val MAX_ROOT_INDEX = 64
    private const val LI_SERIES_LIMIT = 120

    private val PLUS = BigDecimal.ONE
    private val MINUS = BigDecimal.ONE.negate()
    private val ZERO = BigDecimal.ZERO
    private val TWO = BigDecimal(2)

    private val MOBIUS = listOf(ZERO,
        PLUS, MINUS, MINUS, ZERO, MINUS, PLUS, MINUS, ZERO, ZERO, PLUS,
        MINUS, ZERO, MINUS, PLUS, PLUS, ZERO, MINUS, ZERO, MINUS, ZERO,
        PLUS, PLUS, MINUS, ZERO, ZERO, PLUS, ZERO, ZERO, MINUS, MINUS,
        MINUS, ZERO, PLUS, PLUS, PLUS, ZERO, MINUS, PLUS, PLUS, ZERO,
        MINUS, MINUS, MINUS, ZERO, ZERO, PLUS, MINUS, ZERO, ZERO, ZERO,
        PLUS, ZERO, MINUS, ZERO, PLUS, ZERO, PLUS, PLUS, MINUS, ZERO,
        MINUS, PLUS, ZERO, ZERO, PLUS, MINUS, MINUS, ZERO, PLUS, MINUS
    )

    private val COMMON_PI = mapOf(
        // Int.MAX_VALUE, Long.MAX_VALUE
        2_147_483_647L to 105_097_565L, 9_223_372_036_854_775_807L to 216_289_611_853_439_384L,

        // D*10^P, D = 1..9, P = 1..18
        10L to 4L, 20L to 8L, 30L to 10L, 40L to 12L, 50L to 15L, 60L to 17L, 70L to 19L, 80L to 22L, 90L to 24L,
        100L to 25L, 200L to 46L, 300L to 62L, 400L to 78L, 500L to 95L, 600L to 109L, 700L to 125L, 800L to 139L, 900L to 154L,
        1_000L to 168L, 2_000L to 303L, 3_000L to 430L, 4_000L to 550L, 5_000L to 669L, 6_000L to 783L, 7_000L to 900L, 8_000L to 1_007L, 9_000L to 1_117L,
        10_000L to 1_229L, 20_000L to 2_262L, 30_000L to 3_245L, 40_000L to 4_203L, 50_000L to 5_133L, 60_000L to 6_057L, 70_000L to 6_935L, 80_000L to 7_837L, 90_000L to 8_713L,
        100_000L to 9_592L, 200_000L to 17_984L, 300_000L to 25_997L, 400_000L to 33_860L, 500_000L to 41_538L, 600_000L to 49_098L, 700_000L to 56_543L, 800_000L to 63_951L, 900_000L to 71_274L,
        1_000_000L to 78_498L, 2_000_000L to 148_933L, 3_000_000L to 216_816L, 4_000_000L to 283_146L, 5_000_000L to 348_513L, 6_000_000L to 412_849L, 7_000_000L to 476_648L, 8_000_000L to 539_777L, 9_000_000L to 602_489L,
        10_000_000L to 664_579L, 20_000_000L to 1_270_607L, 30_000_000L to 1_857_859L, 40_000_000L to 2_433_654L, 50_000_000L to 3_001_134L, 60_000_000L to 3_562_115L, 70_000_000L to 4_118_064L, 80_000_000L to 4_669_382L, 90_000_000L to 5_216_954L,
        100_000_000L to 5_761_455L, 200_000_000L to 11_078_937L, 300_000_000L to 16_252_325L, 400_000_000L to 21_336_326L, 500_000_000L to 26_355_867L, 600_000_000L to 31_324_703L, 700_000_000L to 36_252_931L, 800_000_000L to 41_146_179L, 900_000_000L to 46_009_215L,
        1_000_000_000L to 50_847_534L, 2_000_000_000L to 98_222_287L, 3_000_000_000L to 144_449_537L, 4_000_000_000L to 189_961_812L, 5_000_000_000L to 234_954_223L, 6_000_000_000L to 279_545_368L, 7_000_000_000L to 323_804_352L, 8_000_000_000L to 367_783_654L, 9_000_000_000L to 411_523_195L,
        10_000_000_000L to 455_052_511L, 20_000_000_000L to 882_206_716L, 30_000_000_000L to 1_300_005_926L, 40_000_000_000L to 1_711_955_433L, 50_000_000_000L to 2_119_654_578L, 60_000_000_000L to 2_524_038_155L, 70_000_000_000L to 2_925_699_539L, 80_000_000_000L to 3_325_059_246L, 90_000_000_000L to 3_722_428_991L,
        100_000_000_000L to 4_118_054_813L, 200_000_000_000L to 8_007_105_059L, 300_000_000_000L to 11_818_439_135L, 400_000_000_000L to 15_581_005_657L, 500_000_000_000L to 19_308_136_142L, 600_000_000_000L to 23_007_501_786L, 700_000_000_000L to 26_684_074_310L, 800_000_000_000L to 30_341_383_527L, 900_000_000_000L to 33_981_987_586L,
        1_000_000_000_000L to 37_607_912_018L, 2_000_000_000_000L to 73_301_896_139L, 3_000_000_000_000L to 108_340_298_703L, 4_000_000_000_000L to 142_966_208_126L, 5_000_000_000_000L to 177_291_661_649L, 6_000_000_000_000L to 211_381_427_039L, 7_000_000_000_000L to 245_277_688_804L, 8_000_000_000_000L to 279_010_070_811L, 9_000_000_000_000L to 312_600_354_108L,
        10_000_000_000_000L to 346_065_536_839L, 20_000_000_000_000L to 675_895_909_271L, 30_000_000_000_000L to 1_000_121_668_853L, 40_000_000_000_000L to 1_320_811_971_702L, 50_000_000_000_000L to 1_638_923_764_567L, 60_000_000_000_000L to 1_955_010_428_258L, 70_000_000_000_000L to 2_269_432_871_304L, 80_000_000_000_000L to 2_582_444_113_487L, 90_000_000_000_000L to 2_894_232_250_783L,
        100_000_000_000_000L to 3_204_941_750_802L, 200_000_000_000_000L to 6_270_424_651_315L, 300_000_000_000_000L to 9_287_441_600_280L, 400_000_000_000_000L to 12_273_824_155_491L, 500_000_000_000_000L to 15_237_833_654_620L, 600_000_000_000_000L to 18_184_255_291_570L, 700_000_000_000_000L to 21_116_208_911_023L, 800_000_000_000_000L to 24_035_890_368_161L, 900_000_000_000_000L to 26_944_926_466_221L,
        1_000_000_000_000_000L to 29_844_570_422_669L, 2_000_000_000_000_000L to 58_478_215_681_891L, 3_000_000_000_000_000L to 86_688_602_810_119L, 4_000_000_000_000_000L to 114_630_988_904_000L, 5_000_000_000_000_000L to 142_377_417_196_364L, 6_000_000_000_000_000L to 169_969_662_554_551L, 7_000_000_000_000_000L to 197_434_994_078_331L, 8_000_000_000_000_000L to 224_792_606_318_600L, 9_000_000_000_000_000L to 252_056_733_453_928L,
        10_000_000_000_000_000L to 279_238_341_033_925L, 20_000_000_000_000_000L to 547_863_431_950_008L, 30_000_000_000_000_000L to 812_760_276_789_503L, 40_000_000_000_000_000L to 1_075_292_778_753_150L, 50_000_000_000_000_000L to 1_336_094_767_763_971L, 60_000_000_000_000_000L to 1_595_534_099_589_274L, 70_000_000_000_000_000L to 1_853_851_099_626_620L, 80_000_000_000_000_000L to 2_111_215_026_220_444L, 90_000_000_000_000_000L to 2_367_751_438_410_550L,
        100_000_000_000_000_000L to 2_623_557_157_654_233L, 200_000_000_000_000_000L to 5_153_329_362_645_908L, 300_000_000_000_000_000L to 7_650_011_911_220_803L, 400_000_000_000_000_000L to 10_125_681_208_311_322L, 500_000_000_000_000_000L to 12_585_956_566_571_620L, 600_000_000_000_000_000L to 15_034_102_021_263_820L, 700_000_000_000_000_000L to 17_472_251_499_627_256L, 800_000_000_000_000_000L to 19_901_908_567_967_065L, 900_000_000_000_000_000L to 22_324_189_231_374_849L,
        1_000_000_000_000_000_000L to 24_739_954_287_740_860L, 2_000_000_000_000_000_000L to 48_645_161_281_738_535L, 3_000_000_000_000_000_000L to 72_254_704_797_687_083L, 4_000_000_000_000_000_000L to 95_676_260_903_887_607L, 5_000_000_000_000_000_000L to 118_959_989_688_273_472L, 6_000_000_000_000_000_000L to 142_135_049_412_622_144L, 7_000_000_000_000_000_000L to 165_220_513_980_969_424L, 8_000_000_000_000_000_000L to 188_229_829_247_429_504L, 9_000_000_000_000_000_000L to 211_172_979_243_258_278L,

        // 2^P, P = 1..62
        2L to 1L, 4L to 2L, 8L to 4L, 16L to 6L, 32L to 11L, 64L to 18L, 128L to 31L, 256L to 54L,
        512L to 97L, 1_024L to 172L, 2_048L to 309L, 4_096L to 564L, 8_192L to 1_028L, 16_384L to 1_900L, 32_768L to 3_512L, 65_536L to 6_542L,
        131_072L to 12_251L, 262_144L to 23_000L, 524_288L to 43_390L, 1_048_576L to 82_025L, 2_097_152L to 155_611L, 4_194_304L to 295_947L, 8_388_608L to 564_163L, 16_777_216L to 1_077_871L,
        33_554_432L to 2_063_689L, 67_108_864L to 3_957_809L, 134_217_728L to 7_603_553L, 268_435_456L to 14_630_843L, 536_870_912L to 28_192_750L, 1_073_741_824L to 54_400_028L, 2_147_483_648L to 105_097_565L, 4_294_967_296L to 203_280_221L,
        8_589_934_592L to 393_615_806L, 17_179_869_184L to 762_939_111L, 34_359_738_368L to 1_480_206_279L, 68_719_476_736L to 2_874_398_515L, 137_438_953_472L to 5_586_502_348L, 274_877_906_944L to 10_866_266_172L, 549_755_813_888L to 21_151_907_950L, 1_099_511_627_776L to 41_203_088_796L,
        2_199_023_255_552L to 80_316_571_436L, 4_398_046_511_104L to 156_661_034_233L, 8_796_093_022_208L to 305_761_713_237L, 17_592_186_044_416L to 597_116_381_732L, 35_184_372_088_832L to 1_166_746_786_182L, 70_368_744_177_664L to 2_280_998_753_949L, 140_737_488_355_328L to 4_461_632_979_717L, 281_474_976_710_656L to 8_731_188_863_470L,
        562_949_953_421_312L to 17_094_432_576_778L, 1_125_899_906_842_624L to 33_483_379_603_407L, 2_251_799_813_685_248L to 65_612_899_915_304L, 4_503_599_627_370_496L to 128_625_503_610_475L, 9_007_199_254_740_992L to 252_252_704_148_404L, 18_014_398_509_481_984L to 494_890_204_904_784L, 36_028_797_018_963_968L to 971_269_945_245_201L, 72_057_594_037_927_936L to 1_906_879_381_028_850L,
        144_115_188_075_855_872L to 3_745_011_184_713_964L, 288_230_376_151_711_744L to 7_357_400_267_843_990L, 576_460_752_303_423_488L to 14_458_792_895_301_660L, 1_152_921_504_606_846_976L to 28_423_094_496_953_330L, 2_305_843_009_213_693_952L to 55_890_484_045_084_135L, 4_611_686_018_427_387_904L to 109_932_807_585_469_973L
    )
    private val X_OF_COMMON_PI = COMMON_PI.keys.sorted()
    private const val SEARCH_RANGE = 1_000_000L

    fun primeCount(num: Long): Pair<Long, Boolean> {
        if (num < 2L) return 0L to true
        if (num in COMMON_PI) return COMMON_PI[num]!! to true
        if (num <= 1_000_000_000_000_000L) {
            val command = listOf("primecount", num.toString(), "-t=1")
            val result = CommandExecutor.execute(command, 10)
            result.onSuccess { return it.toLong() to true }
        }
        primeCountFromNearestCommonPi(num)?.let { return it to true }
        return riemannR(num.toBigDecimal().add(PLUS)).toLong() to false
    }

    fun nthPrime(n: Long): Long? {
        if (n <= 30_000_000_000_000L) {
            val command = listOf("primecount", n.toString(), "-n", "-t=1")
            val result = CommandExecutor.execute(command, 10)
            result.onSuccess { return it.toLong() }
        }
        return null
    }

    private fun riemannR(x: BigDecimal): BigDecimal {
        var sum = ZERO
        for (i in 1..MAX_ROOT_INDEX) {
            val n = i.toBigDecimal()
            val root = BigDecimalMath.root(x, n, MC)
            if (root < TWO) break
            sum = sum.add(MOBIUS[i].divide(n, MC).multiply(li(root), MC))
        }
        return sum
    }

    private fun li(x: BigDecimal): BigDecimal {
        if (x < TWO) return ZERO
        val lnX = BigDecimalMath.log(x, MC)
        var term = PLUS
        var factorial = PLUS
        var sum = EULER_MASCHERONI.add(BigDecimalMath.log(lnX, MC), MC)

        for (k in 1..LI_SERIES_LIMIT) {
            val bigK = k.toBigDecimal()
            term = term.multiply(lnX, MC)
            factorial = factorial.multiply(bigK, MC)
            sum = sum.add(term.divide(bigK.multiply(factorial, MC), MC), MC)
        }
        return sum
    }

    private fun primeCountFromNearestCommonPi(num: Long): Long? {
        val insertionPoint = -(X_OF_COMMON_PI.binarySearch(num) + 1)
        val nearestX = when {
            insertionPoint == 0 -> X_OF_COMMON_PI.first()
            insertionPoint >= X_OF_COMMON_PI.size -> X_OF_COMMON_PI.last()
            else -> {
                val low = X_OF_COMMON_PI[insertionPoint - 1]
                val high = X_OF_COMMON_PI[insertionPoint]
                if (num - low < high - num) low else high
            }
        }
        return if (abs(num - nearestX) > SEARCH_RANGE) {
            null
        } else if (num > nearestX) {
            COMMON_PI[nearestX]!! + ((nearestX + 1)..num).count { isPrime(it) }
        } else {
            COMMON_PI[nearestX]!! - ((num + 1)..nearestX).count { isPrime(it) }
        }
    }
}

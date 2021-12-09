import java.io.File

fun main() {
    val lines = File("inputs/day3.txt").readLines()
    val length = lines.first().length

    fun part1() {
        var gamma = ""
        for (i in 0 until length) {
            val sum = lines.fold(0) { acc, str ->
                if (str[i] == '0') {
                    acc - 1
                } else {
                    acc + 1
                }
            }

            gamma += if (sum > 0) {
                "1"
            } else {
                "0"
            }
        }
        val epsilon = gamma.map { if (it == '0') '1' else '0' }.joinToString(separator = "")

        println(gamma.toInt(2) * epsilon.toInt(2))
    }

    fun part2() {
        fun List<String>.mostCommon(index: Int): Char {
            val sum = this.fold(0) { acc, str ->
                if (str[index] == '0') {
                    acc - 1
                } else {
                    acc + 1
                }
            }

            return if (sum >= 0) {
                '1'
            } else {
                '0'
            }
        }

        fun Char.flip(): Char = when {
            this == '1' -> '0'
            else -> '1'
        }

        fun rate(doLeast: Boolean): Int {
            var idx = 0
            var remainingNumbers = lines.toList()
            while (remainingNumbers.size != 1) {
                var bit = remainingNumbers.mostCommon(idx)
                if (doLeast) {
                    bit = bit.flip()
                }
                remainingNumbers = remainingNumbers.filter { it[idx] == bit }
                ++idx
            }
            return remainingNumbers.first().toInt(2)
        }

        println(rate(false) * rate(true))
    }

    part1()
    part2()
}
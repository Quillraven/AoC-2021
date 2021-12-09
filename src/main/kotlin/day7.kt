import java.io.File
import kotlin.math.abs

fun main() {
    val crabs = File("inputs/day7.txt").readLines().first().split(",").map { it.toInt() }

    fun part1() {
        val min = crabs.minOf { it }
        val max = crabs.maxOf { it }

        var position = -1
        var fuel = Int.MAX_VALUE
        for (i in min..max) {
            val fuelNeeded = crabs.sumOf { abs(it - i) }
            if (fuelNeeded < fuel) {
                fuel = fuelNeeded
                position = i
            }
        }

        println("$fuel $position")
    }

    fun part2() {
        data class Result(val position: Int, val fuel: Int)

        val min = crabs.minOf { it }
        val max = crabs.maxOf { it }
        val results = mutableListOf<Result>()
        for (i in min..max) {
            results.add(
                Result(
                    i,
                    crabs.sumOf {
                        /**
                         * 4 = 4 + 3 + 2 + 1 = 10 = (4+1) * 2 + 0 * 2
                         * 3 = 3 + 2 + 1 = 6 = (3+1) * 1 + 1 * 2
                         * even: RESULT = n + n-1 + n-2 + ... + 1 = n/2 * n+1
                         * odd: RESULT = (n/2 * n+1) + (n+1)/2
                         */
                        val posToMove = abs(it - i)
                        val sumOfFirstAndLast = posToMove + 1

                        sumOfFirstAndLast * (posToMove / 2) + (posToMove % 2) * (sumOfFirstAndLast / 2)
                    }
                )
            )
        }

        println(results.minByOrNull { it.fuel })
    }

    part1()
    part2()
}
import java.io.File

fun main() {
    val lines = File("inputs/day1.txt").readLines()
    val numbers = lines.filter { it.isNotBlank() }.map { it.toInt() }

    fun part1() {
        val numInc = numbers.windowed(2).count { it[0] < it[1] }
        println(numInc)
    }

    fun part2() {
        val numInc = numbers.windowed(3).map { it.sum() }
            .windowed(2).count { it[0] < it[1] }
        println(numInc)
    }

    part1()
    part2()
}
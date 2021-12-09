import java.io.File

fun main() {
    val numbers = File("inputs/day6.txt").readLines().first().split(",").map { it.toInt() }

    fun part1() {
        val days = 80
        var fish = numbers.toMutableList()

        for (i in 0 until days) {
            val creating = fish.filter { it == 0 }
            val newNums = fish.filter { it != 0 }.map { it - 1 }.toMutableList()
            repeat(creating.size) {
                newNums.addAll(listOf(6, 8))
            }
            fish = newNums
        }

        println(fish.size)
    }

    fun part2() {
        val days = 256
        val fish = LongArray(9)
        numbers.forEach { fish[it]++ }

        for (i in 0 until days) {
            val numCreating = fish[0]
            for (j in 0..7) {
                fish[j] = fish[j + 1]
            }
            fish[6] += numCreating
            fish[8] = numCreating
        }

        println(fish.sum())
    }

    part1()
    part2()
}
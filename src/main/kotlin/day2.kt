import java.io.File

fun main() {
    data class Command(val type: String, val amount: Int)

    val lines = File("inputs/day2.txt").readLines()
    val commands = lines.filter { it.isNotBlank() }
        .map { it.trim().split(" ") }
        .map { Command(it[0], it[1].toInt()) }

    fun part1() {
        val posX: Int = commands.filter { it.type == "forward" }.fold(0) { acc, cmd -> acc + cmd.amount }
        val posY: Int = commands.filter { it.type == "down" }.fold(0) { acc, cmd -> acc + cmd.amount }
            .minus(commands.filter { it.type == "up" }.fold(0) { acc, cmd -> acc + cmd.amount })

        println(posX * posY)
    }

    fun part2() {
        var posX = 0
        var posY = 0
        var aim = 0

        commands.forEach { cmd ->
            when (cmd.type) {
                "forward" -> {
                    posX += cmd.amount
                    posY += aim * cmd.amount
                }
                "up" -> aim -= cmd.amount
                "down" -> aim += cmd.amount
            }
        }

        println(posX * posY)
    }

    part1()
    part2()
}
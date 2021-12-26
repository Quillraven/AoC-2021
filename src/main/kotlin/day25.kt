import java.io.File

fun main() {
    val EAST = '>'
    val SOUTH = 'v'
    val EMPTY = '.'

    data class Position(val x: Int, val y: Int)

    class Seafloor(input: List<String>) {
        private val fields = Array(input.size) {
            input[it].toCharArray()
        }
        private val height = input.size
        private val width = input[0].length

        fun cucumbersOfType(type: Char): List<Position> {
            val positions = mutableListOf<Position>()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (fields[y][x] == type) {
                        positions.add(Position(x, y))
                    }
                }
            }
            return positions
        }

        fun field(x: Int, y: Int): Char {
            val checkX = if (x >= width) 0 else x
            val checkY = if (y >= height) 0 else y
            return fields[checkY][checkX]
        }

        fun hasEmptyTarget(pos: Position, type: Char): Boolean {
            return when (type) {
                EAST -> field(pos.x + 1, pos.y) == EMPTY
                SOUTH -> field(pos.x, pos.y + 1) == EMPTY
                else -> false
            }
        }

        fun move(pos: Position, type: Char) {
            val (x, y) = pos
            fields[y][x] = EMPTY

            when (type) {
                EAST -> {
                    val newX = if (x + 1 >= width) 0 else x + 1
                    fields[y][newX] = EAST
                }
                SOUTH -> {
                    val newY = if (y + 1 >= height) 0 else y + 1
                    fields[newY][x] = SOUTH
                }
            }
        }

        fun step(): Int {
            val order = listOf(EAST, SOUTH)
            var moved = 0

            order.forEach { cuType ->
                cucumbersOfType(cuType)
                    .filter { hasEmptyTarget(it, cuType) }
                    .forEach {
                        ++moved
                        move(it, cuType)
                    }
            }

            return moved
        }

        override fun toString(): String {
            return buildString {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        append(fields[y][x])
                    }
                    append("\n")
                }
            }
        }
    }

    val lines = File("inputs/day25.txt").readLines().filterNot { it.isBlank() }

    fun part1() {
        val floor = Seafloor(lines)
        var step = 1
        while (floor.step() > 0) {
            ++step
        }
        println(step)
    }

    fun part2() {
        // only 42 stars :( need 50 (=all) to get puzzle ?!?!
    }

    part1()
    part2()
}

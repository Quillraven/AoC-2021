import java.io.File

fun main() {
    data class Position(val x: Int, val y: Int)

    data class HeightMap(val width: Int, val height: Int) {
        private val map = Array(height) { IntArray(width) }
        private val lowPoints = mutableMapOf<Position, Int>()
        private val basins = mutableMapOf<Position, List<Position>>()

        val riskLevel: Int
            get() = lowPoints.values.sumOf { it + 1 }

        val largestBasins: List<Int>
            get() = basins.values.sortedByDescending { it.size }.take(3).map { it.size }

        fun addLine(y: Int, line: String) {
            line.forEachIndexed { x, c ->
                map[y][x] = c.digitToInt()
            }
        }

        private fun heightValue(x: Int, y: Int): Int {
            return if (x in 0 until width && y in 0 until height) {
                map[y][x]
            } else {
                Int.MAX_VALUE
            }
        }

        private fun isLowPoint(x: Int, y: Int): Boolean {
            val value = heightValue(x, y)
            val l = heightValue(x - 1, y)
            val u = heightValue(x, y + 1)
            val r = heightValue(x + 1, y)
            val d = heightValue(x, y - 1)

            return value < l && value < u && value < r && value < d
        }

        fun calculateLowPoints() {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (isLowPoint(x, y)) {
                        lowPoints[Position(x, y)] = map[y][x]
                    }
                }
            }
        }

        fun calculateBasins() {
            lowPoints.keys.forEach { position ->
                val pointsToCheck = mutableListOf<Position>()
                pointsToCheck.add(position)

                var idx = 0
                while (idx < pointsToCheck.size) {
                    val point = pointsToCheck[idx]
                    ++idx

                    val adjacentPoints = listOf(
                        Position(point.x, point.y - 1),
                        Position(point.x - 1, point.y),
                        Position(point.x + 1, point.y),
                        Position(point.x, point.y + 1)
                    )

                    for (adjPos in adjacentPoints) {
                        if (heightValue(adjPos.x, adjPos.y) < 9 && adjPos !in pointsToCheck) {
                            pointsToCheck.add(adjPos)
                        }
                    }
                }

                basins[position] = pointsToCheck.toList()
            }
        }
    }

    val lines = File("inputs/day9.txt").readLines()
    val map = HeightMap(lines.first().length, lines.size)
    lines.forEachIndexed { idx, line -> map.addLine(idx, line) }

    fun part1() {
        map.calculateLowPoints()

        println(map.riskLevel)
    }

    fun part2() {
        map.calculateBasins()

        println(map.largestBasins.fold(1) { acc, value -> acc * value })
    }

    part1()
    part2()
}

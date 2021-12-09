import java.io.File

fun main() {
    data class Point(val x: Int, val y: Int)

    data class Line(val start: Point, val end: Point) {
        val isHorizontal: Boolean
            get() = start.x == end.x

        val isVertical: Boolean
            get() = start.y == end.y

        fun forEachPoint(action: (Point) -> Unit) {
            var x = start.x
            var y = start.y
            action(Point(x, y))

            while (x != end.x || y != end.y) {
                when {
                    x < end.x -> x++
                    x > end.x -> x--
                }
                when {
                    y < end.y -> y++
                    y > end.y -> y--
                }

                action(Point(x, y))
            }
        }
    }

    class Diagram {
        val diagram = mutableMapOf<Point, Int>()

        fun addLine(line: Line) {
            line.forEachPoint { point ->
                val value = diagram.getOrPut(point) { 0 }
                diagram[point] = value + 1
            }
        }

        fun overlaps(minValue: Int): Int {
            return diagram.values.count { it >= minValue }
        }
    }

    val lines = File("inputs/day5.txt").readLines()
    val segments = lines.filter { it.isNotBlank() }
        .map {
            val startEndStr = it.split("->")
            val startStr = startEndStr[0].trim().split(",")
            val endStr = startEndStr[1].trim().split(",")

            Line(
                Point(startStr[0].toInt(), startStr[1].toInt()),
                Point(endStr[0].toInt(), endStr[1].toInt())
            )
        }

    fun part1() {
        val diagram = Diagram()
        segments.filter { it.isHorizontal || it.isVertical }
            .forEach { diagram.addLine(it) }

        println(diagram.overlaps(2))
    }

    fun part2() {
        val diagram = Diagram()
        segments.forEach { diagram.addLine(it) }

        println(diagram.overlaps(2))
    }

    part1()
    part2()
}

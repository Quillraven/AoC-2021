import java.io.File

fun main() {
    data class Position(var x: Int, var y: Int)

    data class Fold(val axis: Char, val line: Int)

    val lines = File("inputs/day13.txt").readLines()
    val positions = lines.filter { !it.startsWith("fold") && it.isNotBlank() }
        .map { it.split(",") }
        .map { Position(it[0].toInt(), it[1].toInt()) }
    val folds = lines.filter { it.startsWith("fold along") }
        .map { it.substringAfter("fold along ").split("=") }
        .map { Fold(it.first()[0], it.last().toInt()) }

    fun List<Position>.fold(fold: Fold) {
        when (fold.axis) {
            'x' -> {
                this.filter { it.x > fold.line }.forEach { pos ->
                    pos.x = fold.line + (fold.line - pos.x)
                }
            }
            else -> {
                this.filter { it.y > fold.line }.forEach { pos ->
                    pos.y = fold.line + (fold.line - pos.y)
                }
            }
        }
    }

    fun part1() {
        val firstFold = folds.first()
        val posToFold = positions.toList()
        posToFold.fold(firstFold)
        val minX = if (firstFold.axis == 'x') firstFold.line else Int.MAX_VALUE
        val minY = if (firstFold.axis == 'y') firstFold.line else Int.MAX_VALUE

        println(posToFold.filter { it.y < minY && it.x < minX }.distinct().size)
    }

    fun part2() {
        val posToFold = positions.toList()
        folds.forEach { posToFold.fold(it) }
        val minX = folds.filter { it.axis == 'x' }.minOf { it.line }
        val minY = folds.filter { it.axis == 'y' }.minOf { it.line }
        val resultPositions = posToFold.filter { it.y < minY && it.x < minX }.distinct()

        repeat(minY) { y ->
            repeat(minX) { x ->
                if (Position(x, y) in resultPositions) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    part1()
    part2()
}

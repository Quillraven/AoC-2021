import java.io.File

fun main() {
    data class Cell(val nr: Int, var marked: Boolean)

    data class Board(
        val idx: Int,
        val cells: Array<Array<Cell>>,
        var complete: Boolean = false,
        var completionStep: Int = -1,
        var winningNumber: Int = -1
    ) {
        val sum: Int
            get() = cells.flatten()
                .filter { !it.marked }
                .fold(0) { acc, cell -> acc + cell.nr }

        fun index(number: Int): Pair<Int, Int>? {
            cells.forEachIndexed { y, rows ->
                rows.forEachIndexed { x, cell ->
                    if (cell.nr == number) {
                        return Pair(x, y)
                    }
                }
            }

            return null
        }

        fun mark(number: Int): Boolean {
            val (x, y) = index(number) ?: return false
            cells[y][x].marked = true

            for (i in 0 until 5) {
                if (!cells[y][i].marked) {
                    break
                } else if (i == 4) {
                    return true
                }
            }

            for (i in 0 until 5) {
                if (!cells[i][x].marked) {
                    break
                } else if (i == 4) {
                    return true
                }
            }

            return false
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Board

            if (idx != other.idx) return false

            return true
        }

        override fun hashCode(): Int {
            return idx
        }
    }

    val lines = File("inputs/day4.txt").readLines().filter { it.isNotBlank() }
    val nrsToDraw = lines.first().split(",").map { it.toInt() }
    val boards = mutableListOf<Board>()
    for (i in 1 until lines.size step 5) {
        val numbers = mutableListOf<List<Int>>()
        numbers.add(lines[i].split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() })
        numbers.add(lines[i + 1].split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() })
        numbers.add(lines[i + 2].split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() })
        numbers.add(lines[i + 3].split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() })
        numbers.add(lines[i + 4].split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() })

        boards.add(Board(boards.size, Array(5) { y ->
            Array(5) { x -> Cell(numbers[y][x], false) }
        }))
    }

    fun part1() {
        nrsToDraw.forEach { nr ->
            boards.forEach { board ->
                if (board.mark(nr)) {
                    println(board.sum * nr)
                    return
                }
            }
        }
    }

    fun part2() {
        nrsToDraw.forEachIndexed { step, nr ->
            boards.forEach { board ->
                if (!board.complete && board.mark(nr)) {
                    board.complete = true
                    board.completionStep = step
                    board.winningNumber = nr
                }
            }
        }

        val lastBoard = boards.filter { it.complete }.maxByOrNull { it.completionStep }!!
        println(lastBoard.winningNumber * lastBoard.sum)
    }

    part1()
    part2()
}

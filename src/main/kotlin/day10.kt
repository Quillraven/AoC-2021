import java.io.File

fun main() {
    val lines = File("inputs/day10.txt").readLines()

    fun Char.isOpeningBracket(): Boolean =
        this in listOf('(', '[', '{', '<')

    fun Char.openingBracket(): Char =
        when (this) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            else -> '<'
        }

    fun Char.closingBracket(): Char =
        when (this) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            else -> '>'
        }

    fun part1() {
        data class Score(val index: Int, val bracket: Char) {
            val points: Int =
                when (bracket) {
                    ')' -> 3
                    ']' -> 57
                    '}' -> 1197
                    else -> 25137
                }
        }

        val scores = lines.map { line ->
            buildString {
                line.forEachIndexed { idx, c ->
                    if (c.isOpeningBracket()) {
                        append(c)
                    } else {
                        if (this.last() == c.openingBracket()) {
                            // matching closing bracket
                            this.deleteCharAt(this.length - 1)
                        } else {
                            // corrupted
                            return@map Score(idx, c)
                        }
                    }
                }
            }

            return@map Score(-1, ' ')
        }.filter { it.index != -1 }

        println(scores.sumOf { it.points })
    }

    fun part2() {
        data class Score(val incomplete: String) {
            val points: Long
                get() {
                    var result = 0L

                    for (i in incomplete.length - 1 downTo 0) {
                        result *= 5

                        result += when (incomplete[i].closingBracket()) {
                            ')' -> 1
                            ']' -> 2
                            '}' -> 3
                            else -> 4
                        }
                    }

                    return result
                }
        }

        val scores = lines.map { line ->
            val remainingLine = buildString {
                line.forEach { c ->
                    if (c.isOpeningBracket()) {
                        append(c)
                    } else {
                        if (this.last() == c.openingBracket()) {
                            // matching closing bracket
                            this.deleteCharAt(this.length - 1)
                        } else {
                            // corrupted
                            return@map Score("")
                        }
                    }
                }
            }

            return@map Score(remainingLine)
        }.filter { it.incomplete.isNotBlank() }

        println(scores.sortedBy { it.points }[(scores.size * 0.5).toInt()].points)
    }

    part1()
    part2()
}

import java.io.File

fun main() {
    data class Digits(val input: List<String>, val output: List<String>)

    val digits = File("inputs/day8.txt").readLines()
        .map { it.split("|") }
        .map { Digits(it[0].trim().split(" "), it[1].trim().split(" ")) }

    fun part1() {
        val uniqueLengths = listOf(2, 3, 4, 7)

        println(digits.sumOf { digit -> digit.output.filter { it.length in uniqueLengths }.size })
    }

    fun part2() {
        val sortedDigits = digits.map { digit ->
            Digits(
                digit.input.map { it.asIterable().sorted().joinToString("") },
                digit.output.map { it.asIterable().sorted().joinToString("") }
            )
        }

        val outputs = sortedDigits.map { dig ->
            val code1 = dig.input.single { it.length == 2 }
            val code4 = dig.input.single { it.length == 4 }
            val code7 = dig.input.single { it.length == 3 }
            val code8 = dig.input.single { it.length == 7 }
            // 3 is 7 plus two additional segments
            val code3 = dig.input.single { inp -> inp.length == 5 && code7.all { c -> c in inp } }
            // 9 is 3 plus one additional segment
            val code9 = dig.input.single { inp -> inp.length == 6 && code3.all { c -> c in inp } }
            // 5 is 9 minus one segment and not 3
            val code5 = dig.input.single { inp -> inp.length == 5 && inp != code3 && inp.all { c -> c in code9 } }
            // 2 is the only digit left with a length of 5; 3 and 5 are the other digits with a length of 5
            val code2 = dig.input.single { inp -> inp.length == 5 && inp != code3 && inp != code5 }
            // 0 includes 7 and has length 6 and is not 9
            val code0 = dig.input.single { inp -> inp.length == 6 && inp != code9 && code7.all { c -> c in inp } }
            // 6 does not include 7 and has length 6 and is not 9
            val code6 = dig.input.single { inp -> inp.length == 6 && inp != code9 && !code7.all { c -> c in inp } }

            val mapping = mapOf(
                code0 to 0,
                code1 to 1,
                code2 to 2,
                code3 to 3,
                code4 to 4,
                code5 to 5,
                code6 to 6,
                code7 to 7,
                code8 to 8,
                code9 to 9,
            )

            dig.output.map { mapping[it] }.joinToString("").toInt()
        }

        println(outputs.sum())
    }

    part1()
    part2()
}

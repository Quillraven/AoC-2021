import java.io.File

fun main() {
    val lines = File("inputs/day14.txt").readLines()
    val template = lines.first()
    val rules = lines.filter { "->" in it }.map { it.split("->") }
        .associate { it.first().trim() to it.last().trim() }

    fun StringBuilder.step(rules: Map<String, String>) {
        var i = 0
        val lookup = StringBuilder()
        while (i < this.length - 1) {
            lookup.clear()
            lookup.append(this[i]).append(this[i + 1])
            val rule = rules[lookup.toString()]
            if (rule != null) {
                this.insert(i + 1, rule)
                i += 2
            } else {
                ++i
            }
        }
    }

    fun part1() {
        val strBuilder = StringBuilder(template)
        repeat(10) {
            strBuilder.step(rules)
        }

        val dict = strBuilder.asIterable().distinct().associateWith { c ->
            strBuilder.count { it == c }
        }

        println(dict.maxOf { it.value } - dict.minOf { it.value })
    }

    // solution of part 1 doesn't work because of out of memory exception when increasing the step count to 40
    // --> completely new approach by just keeping track of pairs instead of the complete string
    fun part2() {
        val segments = template.windowed(2).groupingBy { it }.eachCount().mapValues { it.value.toLong() }.toMutableMap()

        repeat(40) {
            val toUpdate = mutableMapOf<String, Long>()
            segments.forEach { entry ->
                val rule = rules[entry.key]
                if (rule != null) {
                    val first = entry.key[0] + rule
                    val firstVal = toUpdate.getOrPut(first) { 0L }
                    toUpdate[first] = firstVal + entry.value
                    val second = rule + entry.key[1]
                    val secondVal = toUpdate.getOrPut(second) { 0L }
                    toUpdate[second] = secondVal + entry.value
                } else {
                    val secondVal = toUpdate.getOrPut(entry.key) { 0L }
                    toUpdate[entry.key] = secondVal + entry.value
                }
            }

            segments.clear()
            segments.putAll(toUpdate)
        }

        val dict = mutableMapOf<Char, Long>()
        segments.forEach { entry ->
            val secondC = entry.key[1]
            val secondVal = dict.getOrPut(secondC) { 0L }
            dict[secondC] = secondVal + entry.value
        }
        val firstC = template[0]
        val firstVal = dict.getOrPut(firstC) { 0L }
        dict[firstC] = firstVal + 1L

        println(dict.maxOf { it.value } - dict.minOf { it.value })
    }

    part1()
    part2()
}

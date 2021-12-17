import java.io.File

enum class OperatorType {
    SUM, PRODUCT, MIN, MAX, LITERAL, GREATER, LESS, EQUAL;

    companion object {
        fun byPacketType(packetType: Int): OperatorType {
            return when (packetType) {
                0 -> SUM
                1 -> PRODUCT
                2 -> MIN
                3 -> MAX
                4 -> LITERAL
                5 -> GREATER
                6 -> LESS
                else -> EQUAL
            }
        }
    }
}

data class Operator(val type: OperatorType, val literal: Long? = null) {
    var parent: Operator? = null
        private set
    private val children = mutableListOf<Operator>()

    val result: Long
        get() {
            return when (type) {
                OperatorType.SUM -> children.sumOf { it.result }
                OperatorType.PRODUCT -> children.fold(1L) { acc, op ->
                    acc * op.result
                }
                OperatorType.MIN -> children.minOf { it.result }
                OperatorType.MAX -> children.maxOf { it.result }
                OperatorType.GREATER -> if (children[0].result > children[1].result) 1 else 0
                OperatorType.LESS -> if (children[0].result < children[1].result) 1 else 0
                OperatorType.EQUAL -> if (children[0].result == children[1].result) 1 else 0
                else -> literal!!
            }
        }

    fun addChild(operator: Operator) {
        children.add(operator)
        operator.parent = this
    }
}

data class BITS(val input: String) {
    private val versions = mutableListOf<Int>()
    private var rootOperator: Operator? = null
    private var currentOperator: Operator? = null
    private var position = 0

    val totalVersions: Int
        get() = versions.sum()

    val result: Long
        get() = rootOperator?.result ?: 0L

    private val String.packetVersion: Int
        get() = this.substring(0, 3).toInt(2)

    private val String.packetType: Int
        get() = this.substring(3, 6).toInt(2)

    private val String.lengthID: Int
        get() = this[0].digitToInt()

    private fun String.isLiteral(): Boolean = packetType == 4

    private fun parseLiteral() {
        val toParse = input.substring(position)
        versions.add(toParse.packetVersion)
        position += 6

        var number = ""
        toParse.substring(6).chunked(5).forEach { bits ->
            position += 5
            number += bits.substring(1)
            if (bits[0] == '0') {
                // last group
                currentOperator?.addChild(
                    Operator(OperatorType.LITERAL, number.toLong(2))
                )
                return
            }
        }

        throw RuntimeException("wrong literal format")
    }

    private fun parseByNumSubPackets() {
        ++position
        var toParse = input.substring(position)
        val numPackets = toParse.substring(0, 11).toInt(2)
        position += 11

        repeat(numPackets) {
            toParse = input.substring(position)

            when {
                toParse.isLiteral() -> parseLiteral()
                else -> parseOperator()
            }
        }
    }

    private fun parseByLength() {
        ++position
        var toParse = input.substring(position)
        val length = toParse.substring(0, 15).toInt(2)
        position += 15
        val total = position + length

        while (position < total) {
            toParse = input.substring(position)

            when {
                toParse.isLiteral() -> parseLiteral()
                else -> parseOperator()
            }
        }
    }

    private fun parseOperator() {
        val toParse = input.substring(position)
        versions.add(toParse.packetVersion)
        position += 6

        val newOp = Operator(OperatorType.byPacketType(toParse.packetType))
        currentOperator?.addChild(newOp)
        currentOperator = newOp
        if (rootOperator == null) {
            rootOperator = currentOperator
        }

        when (toParse.substring(6).lengthID) {
            1 -> parseByNumSubPackets()
            else -> parseByLength()
        }

        currentOperator = currentOperator?.parent
    }

    fun parse() {
        when {
            input.isLiteral() -> parseLiteral()
            else -> parseOperator()
        }
    }
}

fun main() {
    val binaryInput = File("inputs/day16.txt").readLines()
        .filterNot { it.isBlank() }
        .map { line ->
            line.map {
                it.digitToInt(16).toString(2).padStart(4, '0')
            }
        }

    fun part1() {
        binaryInput.forEach { inp ->
            BITS(inp.joinToString("")).apply {
                parse()
                println(totalVersions)
            }
        }
    }

    fun part2() {
        binaryInput.forEach { inp ->
            BITS(inp.joinToString("")).apply {
                parse()
                println(result)
            }
        }
    }

    part1()
    part2()
}

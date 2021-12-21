import java.io.File
import kotlin.math.ceil
import kotlin.math.floor

sealed class SnailFishNumber {
    var parent: SnailFishNumber? = null

    private val root: SnailFishNumber
        get() = parent?.root ?: this

    abstract val magnitude: Int
    abstract fun split(): Boolean
    abstract fun orderedDigits(): List<SnailFishDigit>
    abstract fun orderedPairsByDepth(depth: Int = 0): List<SnailFishPairWithDepth>

    operator fun plus(other: SnailFishNumber): SnailFishNumber {
        return SnailFishPair(this, other).apply {
            @Suppress("ControlFlowWithEmptyBody")
            while (explode() || split()) {
            }
        }
    }

    private fun explode(): Boolean {
        val pairs = root.orderedPairsByDepth()
        val explodingPair = pairs.firstOrNull { it.depth == 4 }?.pair
        if (explodingPair != null) {
            val digits = root.orderedDigits()
            digits.elementAtOrNull(digits.indexOfFirst { it === explodingPair.left } - 1)
                ?.addValue(explodingPair.left as SnailFishDigit)
            digits.elementAtOrNull(digits.indexOfFirst { it === explodingPair.right } + 1)
                ?.addValue(explodingPair.right as SnailFishDigit)
            (explodingPair.parent as SnailFishPair).childHasExploded(explodingPair)
            return true
        }
        return false
    }
}

data class SnailFishPairWithDepth(val depth: Int, val pair: SnailFishPair)

data class SnailFishDigit(var value: Int) : SnailFishNumber() {
    override val magnitude: Int
        get() = value

    override fun split(): Boolean = false
    override fun orderedDigits(): List<SnailFishDigit> = listOf(this)
    override fun orderedPairsByDepth(depth: Int): List<SnailFishPairWithDepth> = emptyList()

    fun addValue(amount: SnailFishDigit) {
        this.value += amount.value
    }

    fun splitToPair(parent: SnailFishNumber): SnailFishPair =
        SnailFishPair(
            SnailFishDigit(floor(value.toDouble() * 0.5).toInt()),
            SnailFishDigit(ceil(value.toDouble() * 0.5).toInt())
        ).apply { this.parent = parent }
}

data class SnailFishPair(var left: SnailFishNumber, var right: SnailFishNumber) : SnailFishNumber() {
    init {
        left.parent = this
        right.parent = this
    }

    override val magnitude: Int
        get() = 3 * left.magnitude + 2 * right.magnitude

    fun childHasExploded(child: SnailFishPair) {
        val replacement = SnailFishDigit(0).apply { parent = this@SnailFishPair.parent }
        when {
            left === child -> left = replacement
            else -> right = replacement
        }
    }

    override fun orderedDigits(): List<SnailFishDigit> =
        this.left.orderedDigits() + this.right.orderedDigits()

    override fun orderedPairsByDepth(depth: Int): List<SnailFishPairWithDepth> =
        this.left.orderedPairsByDepth(depth + 1) +
                listOf(SnailFishPairWithDepth(depth, this)) +
                this.right.orderedPairsByDepth(depth + 1)

    override fun split(): Boolean {
        if (left is SnailFishDigit) {
            val actualLeft = left as SnailFishDigit
            if (actualLeft.value >= 10) {
                left = actualLeft.splitToPair(this)
                return true
            }
        }
        val didSplit = left.split()
        if (didSplit) return true
        if (right is SnailFishDigit) {
            val actualRight = right as SnailFishDigit
            if (actualRight.value >= 10) {
                right = actualRight.splitToPair(this)
                return true
            }
        }
        return right.split()
    }
}

fun main() {
    val input = File("inputs/day18.txt").readLines().filterNot { it.isBlank() }

    fun parseInput(line: String): SnailFishNumber {
        val numbers = mutableListOf<SnailFishNumber>()
        line.forEach { char ->
            when {
                char.isDigit() -> numbers.add(SnailFishDigit(char.digitToInt()))
                char == ']' -> {
                    val right = numbers.removeLast()
                    val left = numbers.removeLast()
                    numbers.add(SnailFishPair(left, right))
                }
            }
        }
        return numbers.removeFirst()
    }

    fun part1() {
        println(
            input.map { parseInput(it) }.reduce { a, b -> a + b }.magnitude
        )
    }

    fun part2() {
        println(
            input.mapIndexed { index, left ->
                input.drop(index + 1).map { right ->
                    listOf(
                        parseInput(left) to parseInput(right),
                        parseInput(right) to parseInput(left)
                    )
                }.flatten()
            }.flatten()
                .maxOf { (it.first + it.second).magnitude }
        )
    }

    part1()
    part2()
}

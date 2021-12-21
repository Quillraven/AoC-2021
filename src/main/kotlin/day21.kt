import java.io.File
import kotlin.math.max

fun main() {
    abstract class Die(var value: Int) {
        abstract fun roll(): Int
    }

    class DeterministicDie : Die(0) {
        override fun roll(): Int {
            value++
            if (value > 100) {
                value = 1
            }
            return value
        }
    }

    class FixedDie(private val values: Array<Int>) : Die(0) {
        private var currentIdx = 0

        override fun roll(): Int {
            val result = values[currentIdx]
            currentIdx = (currentIdx + 1) % values.size
            return result
        }
    }

    data class Game(val players: Array<Int>, val die: Die, val goal: Int) {
        var numDiceThrows = 0
            private set
        private val scores = IntArray(players.size)
        private var currentPlayer = 0

        fun copyWithDie(die: Die): Game {
            return Game(this.players, die, this.goal).apply {
                this.numDiceThrows = this@Game.numDiceThrows
                this@Game.scores.forEachIndexed { idx, score ->
                    this.scores[idx] = score
                }
                this.currentPlayer = this@Game.currentPlayer
            }
        }

        val losingPlayer: Int
            get() = if (scores[0] > scores[1]) 1 else 0

        val losingScore: Int
            get() = scores[losingPlayer] * numDiceThrows

        fun isFinished(): Boolean =
            scores.any { it >= goal }

        fun turn() {
            var totalAmount = 0
            repeat(3) {
                totalAmount += die.roll()
                ++numDiceThrows
            }

            var playerLoc = players[currentPlayer] + totalAmount
            while (playerLoc > 10) {
                playerLoc -= 10
            }
            players[currentPlayer] = playerLoc

            scores[currentPlayer] += players[currentPlayer]
            currentPlayer = (currentPlayer + 1) % players.size
        }
    }

    val lines = File("inputs/day21.txt").readLines()
    val p1 = lines[0].substringAfter(":").trim().toInt()
    val p2 = lines[1].substringAfter(":").trim().toInt()

    fun part1() {
        val game = Game(arrayOf(p1, p2), DeterministicDie(), 1000)
        while (!game.isFinished()) {
            game.turn()
        }
        println(game.losingScore)
    }

    /**
     * Solution of Part 2 is not by me
     * It is from here: https://github.com/Kroppeb/AdventOfCodeSolutions/blob/a0b0ad018b07325bf499103b3de9e46048f864fa/src/day%2021.kt
     *
     * Added it for completion of this day.
     *
     * I couldn't find a proper way to use my Game class to simulate all possible outcomes.
     * Tried to find a way doing it one by one for every possible universe combination but in the end I gave up because
     * I couldn't come up with a nice solution.
     */
    data class State(val p1: Int, val p2: Int, val scoreP1: Int, val scoreP2: Int)

    fun part2() {
        var counter = mutableMapOf(State(p1, p2, 0, 0) to 1L)

        var p1Wins = 0L
        var p2Wins = 0L

        while (!counter.isEmpty()) {
            var iterator = counter.iterator()

            val (state, l) = iterator.next()
            iterator.remove()

            val (p1, p2, scoreP1, scoreP2) = state

            repeat(3) { d1 ->
                repeat(3) { d2 ->
                    repeat(3) { d3 ->
                        var px1 = p1
                        px1 += d1 + d2 + d3 + 3
                        px1--
                        px1 %= 10
                        px1++

                        var scorePx1 = scoreP1

                        scorePx1 += px1
                        if (scorePx1 >= 21) {
                            p1Wins += l
                        } else {
                            repeat(3) { dx1 ->
                                repeat(3) { dx2 ->
                                    repeat(3) { dx3 ->
                                        var px2 = p2
                                        px2 += dx1 + dx2 + dx3 + 3
                                        px2--
                                        px2 %= 10
                                        px2++

                                        var scorePx2 = scoreP2
                                        scorePx2 += px2
                                        if (scorePx2 >= 21) {
                                            p2Wins += l
                                        } else {
                                            val state1 = State(px1, px2, scorePx1, scorePx2)
                                            if (state1 in counter) {
                                                counter[state1] = l + counter[state1]!!
                                            } else {
                                                counter[state1] = l
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        println(max(p1Wins, p2Wins))
    }

    part1()
    part2()
}

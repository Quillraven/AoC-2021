import java.io.File
import kotlin.math.max

interface Die {
    fun roll(): Int
}

class DeterministicDie : Die {
    private var value = 0

    override fun roll(): Int {
        value++
        if (value > 100) {
            value = 1
        }
        return value
    }
}

class FixedDie(private val values: IntArray) : Die {
    private var currentIdx = 0

    override fun roll(): Int {
        val result = values[currentIdx]
        currentIdx = (currentIdx + 1) % values.size
        return result
    }
}

data class Game(val players: Array<Int>, val die: Die, val goal: Int) {
    private var numDiceThrows = 0
    val scores = IntArray(players.size)
    var currentPlayer = 0

    val winningPlayer: Int
        get() = if (scores[0] > scores[1]) 0 else 1

    private val losingPlayer: Int
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

    companion object {
        fun ofState(state: State, rolls: IntArray, goal: Int): Game {
            return Game(state.players.clone(), FixedDie(rolls), goal).apply {
                state.scores.forEachIndexed { idx, value ->
                    this.scores[idx] = value
                }
                this.currentPlayer = state.currentPlayer
            }
        }
    }
}

data class State(
    val players: Array<Int>,
    val scores: IntArray,
    val currentPlayer: Int,
    val winningPlayer: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (!players.contentEquals(other.players)) return false
        if (!scores.contentEquals(other.scores)) return false
        if (currentPlayer != other.currentPlayer) return false
        if (winningPlayer != other.winningPlayer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = players.contentHashCode()
        result = 31 * result + scores.contentHashCode()
        result = 31 * result + currentPlayer
        result = 31 * result + winningPlayer
        return result
    }

    companion object {
        fun ofGame(game: Game): State {
            return if (game.isFinished()) {
                State(game.players.clone(), game.scores.clone(), game.currentPlayer, game.winningPlayer)
            } else {
                State(game.players.clone(), game.scores.clone(), game.currentPlayer, -1)
            }
        }
    }
}

fun main() {
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

    fun part2() {
        val states = mutableMapOf(State(arrayOf(p1, p2), IntArray(2), 0, -1) to 1L)
        while (states.any { it.key.winningPlayer == -1 }) {
            val iter = states.filter { it.key.winningPlayer == -1 }.iterator()
            while (iter.hasNext()) {
                val (state, amount) = iter.next()

                // simulate all possible 27 outcomes of a single turn of one specific universe
                repeat(3) { roll1 ->
                    repeat(3) { roll2 ->
                        repeat(3) { roll3 ->
                            val game = Game.ofState(state, intArrayOf(roll1 + 1, roll2 + 1, roll3 + 1), 21)
                            game.turn()
                            val newState = State.ofGame(game)
                            val currAmount = states.getOrPut(newState) { 0L }
                            states[newState] = currAmount + amount
                        }
                    }
                }

                states.remove(state)
            }
        }

        val p1Wins = states.asIterable().filter { it.key.winningPlayer == 0 }.sumOf { it.value }
        val p2Wins = states.asIterable().filter { it.key.winningPlayer == 1 }.sumOf { it.value }
        println(max(p1Wins, p2Wins))
    }

    part1()
    part2()
}

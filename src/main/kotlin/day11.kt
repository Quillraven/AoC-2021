import java.io.File

fun main() {
    data class Position(val x: Int, val y: Int)

    data class Cavern(val octopus: Array<IntArray>) {
        val width = octopus.size
        val height = octopus[0].size
        var numFlashes: Int = 0
        val toFlash = mutableListOf<Position>()
        val fullFlash = mutableListOf<Int>()
        var step = 0

        fun incEnergy(x: Int, y: Int): Boolean {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return false
            }

            val energy = octopus[x][y] + 1
            octopus[x][y] = energy
            return energy == 10
        }

        fun flash(x: Int, y: Int) {
            val posToFlash = Position(x, y)
            if (posToFlash in toFlash) {
                return
            }

            var i = toFlash.size
            toFlash.add(posToFlash)
            while (i < toFlash.size) {
                numFlashes++
                val (flashPosX, flashPosY) = toFlash[i]
                for (octX in flashPosX - 1..flashPosX + 1) {
                    for (octY in flashPosY - 1..flashPosY + 1) {
                        val octPos = Position(octX, octY)
                        if (incEnergy(octPos.x, octPos.y) && octPos !in toFlash) {
                            toFlash.add(octPos)
                        }
                    }
                }

                ++i
            }
        }

        fun debug() {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    print(octopus[x][y])
                }
                println()
            }
            println()
        }

        fun step(numSteps: Int) {
            //debug()
            repeat(numSteps) {
                step++

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        if (incEnergy(x, y)) {
                            flash(x, y)
                        }
                    }
                }

                toFlash.forEach { octopus[it.x][it.y] = 0 }
                toFlash.clear()

                if (octopus.sumOf { it.sum() } == 0) {
                    fullFlash.add(step)
                }
                //  debug()
            }
        }
    }

    val lines = File("inputs/day11.txt").readLines()
    val cavern = Cavern(Array(lines[0].length) { x ->
        IntArray(lines.size) { y ->
            lines[y].get(x).digitToInt()
        }
    })

    fun part1() {
        cavern.step(100)
        println(cavern.numFlashes)
    }

    fun part2() {
        while (cavern.fullFlash.isEmpty()) {
            cavern.step(1)
        }
        println(cavern.fullFlash.first())
    }

    part1()
    part2()
}

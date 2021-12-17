import java.io.File
import kotlin.math.sign

/**
 * pretty sure there is a better solution than just brute forcing a solution with hardcoded values ;)
 * I assume we could get out the formulas for fx(t) and fy(t) and use math to get our results in a cleaner way.
 * However, I am no math expert and couldn't figure out the formulas within 10 minutes, and I wasn't in the mood
 * to spend more time on that, sorry!
 */
fun main() {
    data class Vector2D(var x: Int, var y: Int)

    val ranges = File("inputs/day17.txt").readLines()
        .first().drop(13)
        .split(",")
        .map {
            val line = it.trim()
            val fromTo = line.drop(2).split("..")
            IntRange(fromTo[0].toInt(), fromTo[1].toInt())
        }
    val rangeX = ranges.first()
    val rangeY = ranges.last()

    fun potentialVelocitiesX(rangeX: IntRange): List<Int> {
        val possibleVelocities = mutableListOf<Int>()

        for (vel in 0..rangeX.last) {
            var currentX = 0
            var velocity = vel
            for (i in 0..rangeX.first) {
                if (currentX in rangeX) {
                    possibleVelocities.add(vel)
                    break
                }

                currentX += velocity
                if (velocity != 0) {
                    velocity -= sign(velocity.toFloat()).toInt()
                }
            }
        }

        return possibleVelocities
    }

    fun highestY(velocitiesX: List<Int>, rangeX: IntRange, rangeY: IntRange): Int {
        var highestY = 0

        for (y in 0..1000) {
            for (velX in velocitiesX) {
                val currPos = Vector2D(0, 0)
                val velocity = Vector2D(velX, y)
                var maxHeight = 0

                while (currPos.y >= rangeY.minOf { it }) {
                    if (currPos.x in rangeX && currPos.y in rangeY && highestY < maxHeight) {
                        highestY = maxHeight
                        break
                    }

                    currPos.x += velocity.x
                    currPos.y += velocity.y
                    if (velocity.x != 0) {
                        velocity.x -= sign(velocity.x.toFloat()).toInt()
                    }
                    velocity.y -= 1

                    if (currPos.y > maxHeight) {
                        maxHeight = currPos.y
                    }
                }
            }
        }

        return highestY
    }

    fun allVelocities(velocitiesX: List<Int>, rangeX: IntRange, rangeY: IntRange): List<Vector2D> {
        val velocities = mutableListOf<Vector2D>()

        for (y in -1000..1000) {
            for (velX in velocitiesX) {
                val currPos = Vector2D(0, 0)
                val velocity = Vector2D(velX, y)

                while (currPos.y >= rangeY.minOf { it }) {
                    if (currPos.x in rangeX && currPos.y in rangeY) {
                        velocities.add(Vector2D(velX, y))
                        break
                    }

                    currPos.x += velocity.x
                    currPos.y += velocity.y
                    if (velocity.x != 0) {
                        velocity.x -= sign(velocity.x.toFloat()).toInt()
                    }
                    velocity.y -= 1
                }
            }
        }

        return velocities
    }

    fun part1() {
        val possibleVelocities = potentialVelocitiesX(rangeX)
        println(highestY(possibleVelocities, rangeX, rangeY))
    }

    fun part2() {
        val possibleVelocities = potentialVelocitiesX(rangeX)
        println(allVelocities(possibleVelocities, rangeX, rangeY).size)
    }

    part1()
    part2()
}

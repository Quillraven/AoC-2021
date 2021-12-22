import java.io.File

data class Cube(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    fun inRange(range: IntRange): Boolean {
        val min = range.first
        val max = range.last

        return xRange.first >= min && xRange.last <= max
                && yRange.first >= min && yRange.last <= max
                && zRange.first >= min && zRange.last <= max
    }
}

data class Position(val x: Int, val y: Int, val z: Int)

fun main() {
    fun String.toIntRange(): IntRange {
        val splits = this.trim().split("..")
        return IntRange(splits[0].toInt(), splits[1].toInt())
    }

    val steps = File("inputs/day22.txt").readLines().filterNot { it.isBlank() }
        .associate { line ->
            val on = line.startsWith("on")
            val input = if (on) {
                line.drop(3)
            } else {
                line.drop(4)
            }

            val xyz = input.split(",")
            val cube = Cube(
                xyz[0].drop(2).toIntRange(),
                xyz[1].drop(2).toIntRange(),
                xyz[2].drop(2).toIntRange()
            )
            cube to on
        }

    fun part1() {
        val engine = mutableMapOf<Position, Boolean>()

        steps.filterKeys { it.inRange(-50..50) }
            .forEach { (cube, on) ->
                cube.xRange.forEach { x ->
                    cube.yRange.forEach { y ->
                        cube.zRange.forEach { z ->
                            engine[Position(x, y, z)] = on
                        }
                    }
                }
            }

        println(engine.count { it.value })
    }

    fun part2() {
        val engine = mutableMapOf<Position, Boolean>()

        steps.forEach { (cube, on) ->
            cube.xRange.forEach { x ->
                cube.yRange.forEach { y ->
                    cube.zRange.forEach { z ->
                        engine[Position(x, y, z)] = on
                    }
                }
            }
        }

        println(engine.count { it.value })
    }

    part1()
    part2()
}

import java.io.File

fun main() {
    data class Image(val data: List<String>, val enhancement: String) {
        val width = data[0].length
        val height = data.size
        val isBlinking = enhancement[0] == '#'
        var infinitePixel = '.'

        val numLit: Int
            get() = data.sumOf { it.count { c -> c == '#' } }

        fun pixel(x: Int, y: Int): Char {
            return if (x < 0 || x >= width || y < 0 || y >= height) {
                infinitePixel
            } else {
                data[y][x]
            }
        }

        fun outputPixel(origX: Int, origY: Int, enhancement: String): Char {
            val idx = buildString {
                for (y in origY - 1..origY + 1) {
                    for (x in origX - 1..origX + 1) {
                        when (pixel(x, y)) {
                            '#' -> append(1)
                            else -> append(0)
                        }
                    }
                }
            }.toInt(2)

            return enhancement[idx]
        }

        fun output(enhancement: String, step: Int): Image {
            if (isBlinking) {
                infinitePixel = if (step % 2 == 1) '#' else '.'
            }

            val newData = mutableListOf<String>()
            for (y in -1..height) {
                newData.add(buildString {
                    for (x in -1..width) {
                        append(outputPixel(x, y, enhancement))
                    }
                })
            }

            return Image(newData, enhancement)
        }

        override fun toString(): String {
            return data.joinToString("\n")
        }
    }

    val lines = File("inputs/day20.txt").readLines().filterNot { it.isBlank() }

    fun part1() {
        val imgEnhancement = lines[0]
        val inpImage = Image(lines.drop(1), imgEnhancement)

        var img = inpImage
        repeat(2) {
            img = img.output(imgEnhancement, it)
        }

        println(img.numLit)
    }

    fun part2() {
        val imgEnhancement = lines[0]
        val inpImage = Image(lines.drop(1), imgEnhancement)

        var img = inpImage
        repeat(50) {
            img = img.output(imgEnhancement, it)
        }

        println(img.numLit)
    }

    part1()
    part2()
}

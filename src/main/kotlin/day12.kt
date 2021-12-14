import java.io.File

fun main() {
    val pairs = File("inputs/day12.txt").readLines()
        .filter { it.isNotBlank() }
        .map {
            val fromTo = it.split("-")
            Pair(fromTo.first(), fromTo.last())
        }
    val paths = mutableMapOf<String, MutableList<String>>()
    pairs.forEach {
        paths.getOrPut(it.first) { mutableListOf() }.add(it.second)
        paths.getOrPut(it.second) { mutableListOf() }.add(it.first)
    }

    fun String.isSmallCave(): Boolean = this[0].isLowerCase()

    fun List<String>.endPaths(allRoutes: Map<String, List<String>>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val toCheck = this.toMutableList()
        val allPaths = mutableListOf<MutableList<String>>()
        toCheck.forEach { allPaths.add(mutableListOf("start", it)) }

        while (toCheck.isNotEmpty()) {
            val cave = toCheck.removeFirst()
            val caveDestinations = allRoutes[cave]
            val currCavePaths = allPaths.filter { it.last() == cave }

            caveDestinations?.forEach { dest ->
                currCavePaths.forEach { currPath ->
                    if (dest == "start") {
                        // don't go back to start again
                    } else if (dest.isSmallCave() && dest in currPath) {
                        // small cave already visited
                    } else if (dest == "end") {
                        // path reaches end -> add to result
                        val resultPath = currPath.toMutableList().apply { add("end") }
                        if (resultPath !in result) {
                            result.add(resultPath)
                        }
                    } else {
                        // new valid path -> add to allPaths for further analysis
                        val newPath = currPath.toMutableList().apply { add(dest) }
                        if (newPath !in allPaths) {
                            allPaths.add(newPath)
                            toCheck.add(dest)
                        }
                    }
                }
            }

            allPaths.removeAll(currCavePaths)
        }


        return result
    }

    data class Route(val path: MutableList<String>, val hasTwoSmallVisits: Boolean) {
        val last: String
            get() = path.last()

        operator fun contains(cave: String) = cave in path

        fun newRoute(destCave: String, hasTwoVisits: Boolean): Route {
            return this.copy(path = this.path.toMutableList().apply { add(destCave) }, hasTwoSmallVisits = hasTwoVisits)
        }
    }

    fun List<String>.endPaths2(allRoutes: Map<String, List<String>>): MutableSet<Route> {
        val result = mutableSetOf<Route>()
        val allPaths = mutableSetOf<Route>()
        this.forEach { allPaths.add(Route(mutableListOf("start", it), false)) }

        var unfinishedPaths = allPaths.filter { it.last != "end" }
        while (unfinishedPaths.isNotEmpty()) {
            unfinishedPaths.forEach { currPath ->
                allPaths.remove(currPath)
                val cave = currPath.last
                val caveDestinations = allRoutes[cave]

                caveDestinations?.forEach { dest ->
                    if (dest == "start") {
                        // don't go back to start again
                    } else if (dest.isSmallCave() && dest in currPath) {
                        // small cave already visited
                        if (currPath.hasTwoSmallVisits) {
                            // there was already a small cave visited twice
                        } else {
                            val newPath = currPath.newRoute(dest, true)
                            if (newPath !in allPaths) {
                                allPaths.add(newPath)
                            }
                        }
                    } else if (dest == "end") {
                        // path reaches end -> add to result
                        val resultPath = currPath.newRoute("end", currPath.hasTwoSmallVisits)
                        if (resultPath !in result) {
                            result.add(resultPath)
                        }
                    } else {
                        // new valid path -> add to allPaths for further analysis
                        val newPath = currPath.newRoute(dest, currPath.hasTwoSmallVisits)
                        if (newPath !in allPaths) {
                            allPaths.add(newPath)
                        }
                    }
                }
            }

            unfinishedPaths = allPaths.filter { it.last != "end" }
        }

        return result
    }

    fun part1() {
        val resultPaths = paths["start"]?.endPaths(paths)

        println(resultPaths?.size)
    }

    fun part2() {
        val resultPaths = paths["start"]?.endPaths2(paths)

        println(resultPaths?.size)
    }

    part1()
    part2()
}

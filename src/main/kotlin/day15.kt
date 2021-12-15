import java.io.File
import java.util.*

/**
 * Tried to implement A* first as described at: https://stackabuse.com/graphs-in-java-a-star-algorithm/
 * But the heuristic didn't work out for me no matter if I use Manhattan, Chebyshev or Euclidian.
 * That's why I removed the heuristic part and use the risk/move cost instead.
 * Move cost is simply the sum of all risks to reach to the node.
 */

fun main() {
    data class Position(val x: Int, val y: Int)

    data class Node(val position: Position, val risk: Int) : Comparable<Node> {
        lateinit var parent: Node
        var move: Int = risk

        override fun compareTo(other: Node): Int {
            return move.compareTo(other.move)
        }
    }

    data class Path(val nodes: List<Node>) {
        val cost: Int
            get() = nodes.sumOf { it.risk }
    }

    fun neighbours(node: Node, nodes: Map<Position, Node>): List<Node> {
        val (nodeX, nodeY) = node.position
        val result = mutableListOf<Node>()
        val neighbourPositions = listOf(
            Position(nodeX - 1, nodeY),
            Position(nodeX + 1, nodeY),
            Position(nodeX, nodeY - 1),
            Position(nodeX, nodeY + 1),
        )

        for (neighbourPos in neighbourPositions) {
            nodes[neighbourPos]?.let { result.add(it) }
        }

        return result
    }

    fun shortestPath(start: Node, target: Node, graph: Map<Position, Node>): Path {
        val openList = PriorityQueue<Node>().apply { add(start) }
        val closedList = mutableSetOf<Node>()

        while (openList.isNotEmpty()) {
            val node = openList.peek()
            if (node == target) {
                val resultNodes = mutableListOf<Node>()
                var currNode = target
                while (currNode != start) {
                    resultNodes.add(currNode)
                    currNode = currNode.parent
                }
                return Path(resultNodes.reversed())
            }

            for (neighbour in neighbours(node, graph)) {
                val totalMove = node.move + neighbour.risk

                if (neighbour !in closedList && neighbour !in openList) {
                    openList.add(neighbour)
                    neighbour.parent = node
                    neighbour.move = totalMove
                } else {
                    if (totalMove < neighbour.move) {
                        neighbour.parent = node
                        neighbour.move = totalMove
                        if (neighbour in closedList) {
                            closedList.remove(neighbour)
                            openList.add(neighbour)
                        }
                    }
                }
            }

            openList.remove(node)
            closedList.add(node)
        }

        return Path(emptyList())
    }

    fun part1() {
        var lineNum = -1
        val nodes = File("inputs/day15.txt").readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                ++lineNum
                List(line.length) { x ->
                    Node(Position(x, lineNum), line[x].digitToInt())
                }
            }
            .flatten()
            .associateBy { it.position }
        val width = nodes.values.maxOf { it.position.x }
        val height = nodes.values.maxOf { it.position.y }

        val start = nodes[Position(0, 0)]!!
        val end = nodes[Position(width, height)]!!
        println(shortestPath(start, end, nodes).cost)
    }

    fun part2() {
        var lineNum = -1
        val nodes = File("inputs/day15.txt").readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                ++lineNum
                List(line.length) { x ->
                    Node(Position(x, lineNum), line[x].digitToInt())
                }
            }
            .flatten()
            .associateBy { it.position }
        val width = nodes.values.maxOf { it.position.x } + 1
        val height = nodes.values.maxOf { it.position.y } + 1

        val realCave = mutableMapOf<Position, Node>()
        for (y in 0 until 5) {
            for (x in 0 until 5) {
                val toAdd = x + y

                nodes.forEach { entry ->
                    val newRisk = entry.value.risk + toAdd
                    val newNode = Node(
                        Position(entry.key.x + width * x, entry.key.y + height * y),
                        if (newRisk >= 10) newRisk - 9 else newRisk
                    )
                    realCave[newNode.position] = newNode
                }
            }
        }

        val realCaveWidth = realCave.values.maxOf { it.position.x }
        val realCaveHeight = realCave.values.maxOf { it.position.y }
        val start = realCave[Position(0, 0)]!!
        val end = realCave[Position(realCaveWidth, realCaveHeight)]!!
        println(shortestPath(start, end, realCave).cost)
    }

    part1()
    part2()
}

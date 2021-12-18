package day15

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day15) {
    testPart1(checkResult = 40)
    testPart2(checkResult = 315)
    runPart1()
    runPart2()
}

object Day15 : Aoc2021(debug = false) {

    override fun solutionPart1(inputReader: InputStreamReader): Any = with(SquareBoard(inputReader)) {
        solve()
    }

    override fun solutionPart2(inputReader: InputStreamReader): Any = with(SquareBoard(inputReader, true)) {
        solve()
    }

    class SquareBoard(inputReader: InputStreamReader, isPart2: Boolean = false) {
        private val board: Array<Array<Int>>
        private val boardSize: Int

        init {
            val initBoard = inputReader.readLines().map { line ->
                line.map { char -> char.digitToInt() }.toTypedArray()
            }.toTypedArray()
            board = if (!isPart2) {
                initBoard
            } else {
                val sSize = initBoard.size
                val bSize = sSize * 5
                Array(bSize) { y ->
                    Array(bSize) { x ->
                        val ix = x.rem(sSize)
                        val iy = y.rem(sSize)
                        val add = x.div(sSize) + y.div(sSize) // brdX + brdY
                        val risk = (initBoard[ix, iy] + add - 1).mod(9) + 1
                        risk
                    }
                }
            }
            boardSize = board.size
            check(board[0].size == boardSize)  // square
        }

        data class Position(val x: Int, val y: Int) {
            private val toLeft get() = Position(x - 1, y)
            private val toUp get() = Position(x, y - 1)
            private val toRight get() = Position(x + 1, y)
            private val toDown get() = Position(x, y + 1)
            private fun crossNeighbours(width: Int, height: Int) = listOf(toLeft, toRight, toUp, toDown).filter {
                it.x in 0 until width && it.y in 0 until height
            }

            fun crossNeighbours(squareSize: Int) = crossNeighbours(squareSize, squareSize)
        }

        private val nextMap = mutableSetOf<Pair<Position, Int>>()
        private val riskFromStart = mutableMapOf<Position, Int>()
        private val cameFrom = mutableMapOf<Position, Position>()

        operator fun <T> Map<Position, T>.get(x: Int, y: Int) = this[Position(y, x)]
        operator fun <T> Array<Array<T>>.get(pos: Position) = this[pos.y][pos.x] ?: error("No position: $pos")
        operator fun <T> Array<Array<T>>.get(x: Int, y: Int) = this[y][x]

        fun solve(): Int {
            val start = Position(0, 0)
            val end = Position(boardSize - 1, boardSize - 1)
            nextMap.add(start to 0)
            riskFromStart[start] = 0
            cameFrom[start] = start
            while (true) {
                val currentCandidate = nextMap.minByOrNull { it.second } ?: break
                nextMap.remove(currentCandidate)
                val current = currentCandidate.first
                debugPrint(current)
                if (current == end)
                    break
                val currentRisk = riskFromStart[current] ?: error("No current risk")
                val candidates = current.crossNeighbours(boardSize)
                debugToConsole { "Current: $current (${candidates.joinToString()})" }
                candidates.forEach { next ->
                    val nextRisk = board[next]
                    val newRisk = currentRisk + nextRisk
                    riskFromStart[next].let { nextRiskFromStart ->
                        if (nextRiskFromStart == null || newRisk < nextRiskFromStart) {
                            riskFromStart[next] = newRisk
                            cameFrom[next] = current
                            check(nextMap.add(next to newRisk))
                            debugToConsole { " -> Next: $next " }
                        }
                    }
                }
                debugToConsole { "Candidates: " + nextMap.joinToString() }
            }
            return riskFromStart[end] ?: error("No result")
        }

        // Debug print functions
        private fun debugPrint(cur: Position? = null) = debugToConsole {
            board.mapIndexed { y, line ->
                line.mapIndexed { x, risk ->
                    Position(x, y).let { pos ->
                        val mark = if (pos == cur) "*" else if (nextMap.any { it.first == pos }) "-" else " "
                        val rfs = (riskFromStart[pos]?.toString() ?: "--").padStart(2)
                        val cf = (cameFrom[pos]?.let { "${it.x},${it.y}" } ?: "--").padStart(3)
                        "$mark $risk $rfs $cf $mark"
                    }
                }.joinToString(" | ", "| ", " |")
            }.joinToString("\n", "\n")
        }
    }

}

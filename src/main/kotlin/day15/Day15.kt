package day15

import shared.Aoc2021

fun main() {
    Day15.solve(bothParts = false, checkPart1 = 40, checkPart2 = 315)
}

object Day15 : Aoc2021(production = false, debug = true) {

    override fun solvePart1(debug: Boolean) = with(SquareBoard()) {
        solve()
    }

    override fun solvePart2(debug: Boolean) = with(SquareBoard(true)) {
        solve()
    }

    class SquareBoard(isPart2: Boolean = false) {
        private val board: Array<Array<Int>>
        private val boardSize: Int

        init {
            val initBoard = inputReader().readLines().map { line ->
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
            debugPrintBoard()
        }

        private fun debugPrintBoard() = debugToConsole {
            board.toList().chunked(10).joinToString("\n\n", "\n", "\n") { vList ->
                vList.joinToString("\n") { lines ->
                    lines.toList().chunked(10).joinToString(" ") { hList ->
                        hList.joinToString("") { it.toString() }
                    }
                }
            }
        }

        data class Position(val x: Int, val y: Int) {
            private val toLeft get() = Position(x - 1, y)
            private val toUp get() = Position(x, y - 1)
            private val toRight get() = Position(x + 1, y)
            private val toDown get() = Position(x, y + 1)
            fun crossNeighbours(squareSize: Int) = crossNeighbours(squareSize, squareSize)
            private fun crossNeighbours(width: Int, height: Int) = listOf(toLeft, toRight, toUp, toDown).filter {
                it.x in 0 until width && it.y in 0 until height
            }
        }

        private val nextMap = sortedSetOf<Pair<Position, Int>>(compareBy { it.second })
        private val riskFromStart = mutableMapOf<Position, Int>()
        private val cameFrom = mutableMapOf<Position, Position>()

        operator fun <T> Map<Position, T>.get(x: Int, y: Int) = this.get(Position(y, x))
        operator fun <T> Array<Array<T>>.get(x: Int, y: Int) = this[y][x]
        operator fun <T> Array<Array<T>>.get(pos: Position) = this[pos.y][pos.x] ?: error("No position: $pos")

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

        fun solve() {
            val start = Position(0, 0)
            val end = Position(boardSize - 1, boardSize - 1)
            nextMap.add(start to 0)
            riskFromStart[start] = 0
            cameFrom[start] = start
            while (true) {
                val current = nextMap.pollFirst()?.first ?: break
                debugPrint(current); readln()
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
                            nextMap.add(next to newRisk)
                            debugToConsole { " -> Next: $next " }
                        }
                    }
                }
                debugToConsole { "Candidates: " + nextMap.joinToString() }
            }

            println(
                "Came from: " + cameFrom.filterKeys { it.x > 7 || it.y > 7 }.toList().joinToString("\n", "\n", "\n")
            )
            println("Risk: " + riskFromStart[end])
        }
    }
}


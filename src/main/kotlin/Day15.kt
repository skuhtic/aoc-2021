import shared.Aoc2021

fun main() {
    Day15.solve(bothParts = true, checkPart1 = 40, checkPart2 = 315)
}

object Day15 : Aoc2021(
    production = false,
    debug = true
) {

    override fun solvePart1(debug: Boolean) = with(SquareBoard()) {
        weightHalf()
        findBestAmountFormDiagonal()
    }

    override fun solvePart2(debug: Boolean) = with(SquareBoard(true)) {
        weightHalf()
        findBestAmountFormDiagonal()
    }

    class SquareBoard(isPart2: Boolean = false) {
        private val brd: Array<Array<Field>>
        private val size get() = brd.size

        init {
            val initBoard = inputReader().readLines().map { line ->
                line.map { char -> Field(char.digitToInt()) }.toTypedArray()
            }.toTypedArray()
            brd = if (!isPart2) {
                initBoard
            } else {
                val sSize = initBoard.size
                val bSize = sSize * 5
                Array(bSize) { y ->
                    Array(bSize) { x ->
                        val ix = x.rem(sSize)
                        val iy = y.rem(sSize)
                        val add = x.div(sSize) + y.div(sSize) // brdX + brdY
                        val risk = (initBoard[ix, iy].risk + add - 1).mod(9) + 1
                        Field(risk)
                    }
                }
            }
            check(brd[0].size == size)  // square
            debugPrintBoard()
        }

        private fun debugPrintBoard() = debugToConsole {
            brd.toList().chunked(10).joinToString("\n\n", "\n", "\n") { vList ->
                vList.joinToString("\n") { lines ->
                    lines.toList().chunked(10).joinToString(" ") { hList ->
                        hList.joinToString("") { it.risk.toString() }
                    }
                }
            }
        }

        private fun debugPrintFields() = debugToConsole {
            brd.joinToString("\n", "\n", "\n") { fields ->
                fields.joinToString("|") { it.toString() }
            }
        }

        fun weightHalf() {
            repeat(size) { i ->
                for (y in 0..i) {
                    // from start
                    val x = i - y
                    try {
                        if (x == 0 && y == 0) brd[x, y].setStart(x, y)
                        if (x > 0) brd[x, y].considerNewFieldToStart(x - 1, y)
                        if (y > 0) brd[x, y].considerNewFieldToStart(x, y - 1)
                    } catch (e: Throwable) {
                        error("$x,$y considering up, left (for start) " + debugPrintFields())
                    }
                    // from end
                    val limit = size - 1
                    val yToEnd = limit - y
                    val xToEnd = limit - x
                    try {
                        if (xToEnd == limit && yToEnd == limit) brd[xToEnd, yToEnd].setEnd(xToEnd, yToEnd)
                        if (xToEnd < limit) brd[xToEnd, yToEnd].considerNewFieldToEnd(xToEnd + 1, yToEnd)
                        if (yToEnd < limit) brd[xToEnd, yToEnd].considerNewFieldToEnd(xToEnd, yToEnd + 1)
                    } catch (e: Throwable) {
                        error("$xToEnd,$yToEnd considering down, right (for end) " + debugPrintFields())
                    }
                }
            }
//            debugPrintFields()
        }

        fun findBestAmountFormDiagonal() =
            brd.flatten().filter { it.bestPathOnField != null }.minOf { it.bestPathOnField!! }


        inner class Field(val risk: Int) {
            private var bestToStart: Link? = null
            private var bestToEnd: Link? = null

            val bestPathOnField
                get() = if (bestToStart == null || bestToEnd == null) null
                else bestToStart!!.riskSum + bestToEnd!!.riskSum

            fun setStart(x: Int, y: Int) {
                bestToStart = Link(0, x, y)
            }

            fun setEnd(x: Int, y: Int) {
                bestToEnd = Link(0, x, y)
            }

            fun considerNewFieldToStart(x: Int, y: Int) {
                brd[x, y].let { fieldToConsider ->
                    fieldToConsider.bestToStart!!.riskSum.let { consideredRiskSum ->
                        Link(consideredRiskSum + risk, x, y).let { newLink ->
                            bestToStart = if (bestToStart == null) newLink else
                                minOf(bestToStart!!, newLink) { o1, o2 ->
                                    o1.riskSum - o2.riskSum
                                }
                        }
                    }
                }
            }

            fun considerNewFieldToEnd(x: Int, y: Int) {
                brd[x, y].let { fieldToConsider ->
                    fieldToConsider.bestToEnd!!.riskSum.let { riskSum ->
                        Link(riskSum + fieldToConsider.risk, x, y).let { newLink ->
                            bestToEnd = if (bestToEnd == null) newLink else minOf(bestToEnd!!, newLink) { o1, o2 ->
                                o1.riskSum - o2.riskSum
                            }
                        }
                    }
                }
            }

            override fun toString() = "$risk ${(bestToStart ?: "-/-").toString().padStart(4)} ${
                (bestToEnd ?: "-/-").toString().padStart(4)
            }".padEnd(12)
        }

        class Link(val riskSum: Int, val x: Int, val y: Int) {
            override fun toString() = "$riskSum"
        }

    }
}

operator fun <T> Array<Array<T>>.get(x: Int, y: Int) = this[y][x]

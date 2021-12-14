import shared.Aoc2021
import kotlin.math.abs
import kotlin.math.max

fun main() {
    Day05.solve(bothParts = true, checkPart1 = 5, checkPart2 = 12)
}

object Day05 : Aoc2021(production = true, debug = false) {
    private val cloudsHorizontalAndVertical = mutableMapOf<Position, Int>()
    private val cloudsAll = mutableMapOf<Position, Int>()

    init {
        inputReader().readLines().forEach { input ->
            Line.parse(input).let { line ->
                line.positions.forEach { pos ->
                    cloudsAll.getOrPut(pos) { 0 }.let { old ->
                        cloudsAll.replace(pos, old, old + 1)
                    }
                    if (line.isHorizontalOrVertical) {
                        cloudsHorizontalAndVertical.getOrPut(pos) { 0 }.let { old ->
                            cloudsHorizontalAndVertical.replace(pos, old, old + 1)
                        }
                    }
                }
            }
        }
    }

    override fun solvePart1(debug: Boolean) = cloudsHorizontalAndVertical.filterValues { it >= 2 }.count()
    override fun solvePart2(debug: Boolean) = cloudsAll.filterValues { it >= 2 }.count()
}

data class Position(val x: Int, val y: Int)

data class Line(val start: Position, val end: Position) {
    val positions: Set<Position>
    val isHorizontalOrVertical: Boolean
    private val isDiagonal: Boolean

    init {
        val dw = end.x - start.x
        val dh = end.y - start.y
        isHorizontalOrVertical = dw == 0 || dh == 0
        isDiagonal = abs(dw) == abs(dh)
        if (!isDiagonal && !isHorizontalOrVertical) error("Not supported, only horizontal, vertical ot diagonal lines supported")
        val tmp = mutableSetOf<Position>()
        val dx = if (dw == 0) 0 else dw / abs(dw)
        val dy = if (dh == 0) 0 else dh / abs(dh)
        repeat(max(abs(dw), abs(dh))) { f ->
            tmp.add(Position(start.x + dx * f, start.y + dy * f))
        }
        tmp.add(end)
        positions = tmp.toSet()
    }

    companion object {
        fun parse(line: String) = line.split(" -> ").let { positions ->
            positions[0].split(',').let { Position(it[0].toInt(), it[1].toInt()) } to
                    positions[1].split(',').let { Position(it[0].toInt(), it[1].toInt()) }
        }.let { (s, e) -> Line(s, e) }
    }
}
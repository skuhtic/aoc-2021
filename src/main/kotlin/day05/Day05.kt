package day05

import shared.Aoc2021
import shared.Position
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.math.max

fun main() = with(Day05) {
    testPart1(checkResult = 5)
    testPart2(checkResult = 12)
    runPart1()
    runPart2()
}

object Day05 : Aoc2021() {

    private fun initClouds(inputReader: InputStreamReader, alsoUseDiagonal:Boolean = false): MutableMap<Position, Int> {
        val clouds = mutableMapOf<Position, Int>()
        inputReader.readLines().forEach { input ->
            Line.parse(input).let { line ->
                if (alsoUseDiagonal || line.isHorizontalOrVertical) {
                    line.positions.forEach { pos ->
                        clouds.getOrPut(pos) { 0 }.let { old ->
                            clouds.replace(pos, old, old + 1)
                        }
                    }
                }
            }
        }
        return clouds
    }

    override fun solutionPart1(inputReader: InputStreamReader): Any {
        val clouds = initClouds(inputReader)
        return clouds.filterValues { it >= 2 }.count()
    }
    override fun solutionPart2(inputReader: InputStreamReader): Any {
        val clouds = initClouds(inputReader, true)
        return clouds.filterValues { it >= 2 }.count()
    }
}

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

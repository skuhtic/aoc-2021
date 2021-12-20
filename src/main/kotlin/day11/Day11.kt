package day11

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day11) {
    testPart1(checkResult = 1656)
    testPart2(checkResult = 195)
    runPart1()
    runPart2()
}

object Day11 : Aoc2021() {
    private fun inputData(inputReader: InputStreamReader) = inputReader.readLines()
    override fun solutionPart1(inputReader: InputStreamReader) = inputData(inputReader).let {
        Board(it).run { solve(false) }
    }

    override fun solutionPart2(inputReader: InputStreamReader) = inputData(inputReader).let {
        Board(it).run { solve(true) }
    }
}


class Board private constructor(private val data: CharArray, private val columns: Int, private val rows: Int) {
    companion object {
        const val EXPLOSION = 'X'
        const val EXPLODED = '+'
        const val FLASHING = '0'
    }

    private var step = 0
    private var explosions = 0
    private val isAllFlashing get() = data.all { it == FLASHING }

    constructor(input: List<String>) : this(
        input.joinToString("").toCharArray(),
        input.firstOrNull()?.length ?: 0,
        input.size
    ) {
        check(data.size == columns * rows)
    }

    private fun explode(index: Int) {
        check(data[index] == EXPLOSION)
        explosions++
        data[index] = EXPLODED
        neighbours(index).forEach {
            data[it] = data[it].levelUp()
            if (data[it] == EXPLOSION) explode(it)
        }
    }

    private fun step() {
        data.indices.forEach { i ->
            data[i] = data[i].levelUp()
            if (data[i] == EXPLOSION) explode(i)
        }
        data.indices.forEach { i -> if (data[i] == EXPLODED) data[i] = FLASHING }
        step++
    }

    fun solve(finalPart: Boolean) =
        if (!finalPart) {
            repeat(100) { step() }
            explosions
        } else {
            do {
                step()
            } while (!isAllFlashing)
            step
        }

    private fun neighbours(index: Int): List<Int> {
        val ret = mutableListOf<Int>()
        val x = index.rem(columns)
        val y = index.div(columns)
        if (x > 0) { // Not 1st column
            ret.add(index - 1) // left
            if (y > 0) ret.add(index - columns - 1) // left-up
            if (y < rows - 1) ret.add(index + columns - 1) // left-down
        }
        if (x < columns - 1) { // Not last column
            ret.add(index + 1) // right
            if (y > 0) ret.add(index + 1 - columns) // right-up
            if (y < rows - 1) ret.add(index + 1 + columns) // right-down
        }
        if (y > 0) ret.add(index - columns) // up
        if (y < rows - 1) ret.add(index + columns) // down
        return ret
    }

    private fun Char.levelUp(): Char = when (this) {
        '0' -> '1'
        '1' -> '2'
        '2' -> '3'
        '3' -> '4'
        '4' -> '5'
        '5' -> '6'
        '6' -> '7'
        '7' -> '8'
        '8' -> '9'
        '9' -> EXPLOSION
        else -> this
    }

    // Debug functions
    override fun toString() = data.let {
        "Board after step $step ($explosions explosions)" +
                it.joinToString("").chunked(columns).joinToString("\n", "\n", "\n")
    }
}

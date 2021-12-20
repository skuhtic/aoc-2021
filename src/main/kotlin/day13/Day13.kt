package day13

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day13) {
    testPart1(checkResult = 17)
    testPart2(checkResult = 16)
    runPart1()
    runPart2()
}

object Day13 : Aoc2021() {
    private fun inputData(inputReader: InputStreamReader) = inputReader.readLines()
    override fun solutionPart1(inputReader: InputStreamReader) = inputData(inputReader).let {
        PaperFolder(it).run {
            processInstruction()
            result
        }
    }

    override fun solutionPart2(inputReader: InputStreamReader) = inputData(inputReader).let {
        PaperFolder(it).run {
            while (true) {
                if (!processInstruction()) break
            }
            debug { this }
            result
        }
    }


    class PaperFolder(input: List<String>) {
        private val paperDots = mutableSetOf<PaperDot>()
        private val instructions = mutableListOf<PaperInstruction>()
        private var dotJoinCount = 0

        init {
            var dotsInput = true
            input.forEach { line ->
                if (line.isEmpty()) {
                    dotsInput = false
                    return@forEach
                }
                if (dotsInput) {
                    val (x, y) = line.split(',').let { it[0].toInt() to it[1].toInt() }
                    check(paperDots.add(PaperDot(x, y)))
                } else {
                    val (dir, pos) = line.substringAfterLast(' ').split('=').let { it[0][0] to it[1].toInt() }
                    check(instructions.add(PaperInstruction(dir, pos)))
                }
            }
        }

        val result get() = paperDots.size

        fun processInstruction(): Boolean {
            val ins = instructions.firstOrNull() ?: return false
            val dots = paperDots.filter { if (ins.dir == 'y') it.y > ins.pos else it.x > ins.pos }.toSet()
            check(paperDots.removeAll(dots))
            dots.map { if (ins.dir == 'y') it.x to (ins.pos * 2 - it.y) else (ins.pos * 2 - it.x) to it.y }
                .forEach { (x, y) ->
                    if (!paperDots.add(PaperDot(x, y))) dotJoinCount++
                }
            check(instructions.remove(ins))
            return true
        }

        override fun toString(): String {
            val width = paperDots.maxOf { it.x } + 1
            val height = paperDots.maxOf { it.y } + 1
            val board = List(height) { " ".repeat(width).toCharArray() }
            paperDots.forEach { board[it.y][it.x] = '*' }
            return board.joinToString("\n", "\n", "\n") { it.joinToString("") }
        }

    }

    data class PaperDot(val x: Int, val y: Int)
    data class PaperInstruction(val dir: Char, val pos: Int)
}

package day01

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day01) {
    testPart1(checkResult = 7)
    testPart2(checkResult = 5)
    runPart1()
    runPart2()
}

object Day01 : Aoc2021() {

    override fun solutionPart1(inputReader: InputStreamReader): Any = inputReader
        .readLines()
        .map { it.toInt() }
        .zipWithNext { a, b -> a < b }
        .count { it }

    override fun solutionPart2(inputReader: InputStreamReader): Any = inputReader
        .readLines()
        .map { it.toInt() }
        .windowed(3) { it.sum() }
        .zipWithNext { a, b -> a < b }
        .count { it }

}

package day02

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day02) {
    testPart1(checkResult = 150)
    testPart2(checkResult = 900)
    runPart1()
    runPart2()
}

object Day02 : Aoc2021(debug = false) {

    override fun solutionPart1(inputReader: InputStreamReader): Any = inputReader.readLines()
        .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
        .fold(0 to 0) { depthDistance, instruction ->
            when (instruction.first) {
                "forward" -> depthDistance.first to depthDistance.second + instruction.second
                "down" -> depthDistance.first + instruction.second to depthDistance.second
                "up" -> depthDistance.first - instruction.second to depthDistance.second
                else -> throw IllegalArgumentException("Unknown instruction")
            }
        }.let { it.first * it.second }

    override fun solutionPart2(inputReader: InputStreamReader): Any = inputReader.readLines()
        .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
        .fold(Triple(0, 0, 0)) { aimDepthDistance, instruction ->
            when (instruction.first) {
                "forward" -> Triple(
                    aimDepthDistance.first,
                    aimDepthDistance.second + aimDepthDistance.first * instruction.second,
                    aimDepthDistance.third + instruction.second
                )
                "down" -> Triple(
                    aimDepthDistance.first + instruction.second,
                    aimDepthDistance.second,
                    aimDepthDistance.third
                )
                "up" -> Triple(
                    aimDepthDistance.first - instruction.second,
                    aimDepthDistance.second,
                    aimDepthDistance.third
                )
                else -> throw IllegalArgumentException("Unknown instruction")
            }.also { debugToConsole { it } }
        }.let { it.second * it.third }

}

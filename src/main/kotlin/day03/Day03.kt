package day03

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day03) {
    testPart1(checkResult = 198)
    testPart2(checkResult = 230)
    runPart1()
    runPart2()
}

object Day03 : Aoc2021() {

    override fun solutionPart1(inputReader: InputStreamReader): Any {
        val chars = inputReader.readLines().map { it.toCharArray() }
        var gamma = 0
        var epsilon = 0
        val half = chars.size / 2
        repeat(chars[0].size) { index ->
            gamma *= 2
            epsilon *= 2
            if (chars.count { it[index] == '1' } > half) gamma++ else epsilon++
        }
        return gamma * epsilon
    }

    override fun solutionPart2(inputReader: InputStreamReader): Any {
        val chars = inputReader.readLines().map { it.toCharArray() }
        var oxygenList = chars //input.map { it.toCharArray() }
        repeat(oxygenList[0].size) { index ->
            oxygenList = oxygenList.filterOxygen(index)
        }
        check(oxygenList.size == 1)
        val oxygen = oxygenList[0].joinToString("").toInt(2)

        var co2List = chars
        repeat(co2List[0].size) { index ->
            co2List = co2List.filterCO2(index)
        }
        check(co2List.size == 1)
        val co2 = co2List[0].joinToString("").toInt(2)

        return oxygen * co2
    }
}

fun List<CharArray>.filterOxygen(index: Int) = if (this.size == 1) this else
    partition { it[index] == '1' }.let {
        when {
            it.first.size > it.second.size -> it.first
            it.first.size < it.second.size -> it.second
            else -> it.first
        }
    }

fun List<CharArray>.filterCO2(index: Int) = if (this.size == 1) this else
    partition { it[index] == '0' }.let {
        when {
            it.first.size < it.second.size -> it.first
            it.first.size > it.second.size -> it.second
            else -> it.first
        }
    }

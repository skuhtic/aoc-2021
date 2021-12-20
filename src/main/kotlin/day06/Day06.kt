package day06

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day06) {
    testPart1(checkResult = 5934L)
    testPart2(checkResult = 26984457539L)
//    runPart1()
//    runPart2()
}

object Day06 : Aoc2021(debug = false) {

    private fun initData(inputReader: InputStreamReader) = inputReader
        .readLines()[0].split(',').map { it.toInt() }.let { input ->
        (0..8).map { it }.associateWith { i ->
            input.count { it == i }.toLong()
        }
    }

    override fun solutionPart1(inputReader: InputStreamReader) = LanternFish(initData(inputReader)).apply {
        solve(80)
    }.count

    override fun solutionPart2(inputReader: InputStreamReader) = LanternFish(initData(inputReader)).apply {
        solve(256)
    }.count


    class LanternFish(private var fishStats: Map<Int, Long>) {
        val count get() = fishStats.values.sum()

        private fun day() {
            fishStats = MutableList(9) { 0L }.also { newFishStats ->
                fishStats.forEach { level ->
                    when (level.key) {
                        0 -> {
                            newFishStats[8] += level.value
                            newFishStats[6] += level.value
                        }
                        else -> newFishStats[level.key - 1] += level.value
                    }
                }
            }.mapIndexed { i, c -> i to c }.toMap()
        }

        fun solve(days: Int) = repeat(days) {
            debug { "Day: ${it + 1} ($count)" }
            day()
        }
    }

}



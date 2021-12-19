package day07

import shared.Aoc2021
import java.io.InputStreamReader
import kotlin.math.abs

fun main() = with(Day07) {
    testPart1(checkResult = 37)
    testPart2(checkResult = 168)
    runPart1()
    runPart2()
}

object Day07 : Aoc2021() {

    private fun initData(inputReader: InputStreamReader) =
        inputReader.buffered().readLine().split(',').map { it.toInt() }

    override fun solutionPart1(inputReader: InputStreamReader) =
        TheTreacheryOfWhales(initData(inputReader)).apply {
            solve()
        }.minFuelCost

    override fun solutionPart2(inputReader: InputStreamReader) =
        TheTreacheryOfWhales(initData(inputReader), true).apply {
            solve()
        }.minFuelCost

    class TheTreacheryOfWhales(private val crabs: List<Int>, private val nonLinearFuelCost: Boolean = false) {
        private val tries = mutableMapOf<Int, Int>()
        private val nextTryPosition
            get() = tries.nextPosition {
                if (nonLinearFuelCost) crabs.average().toInt() else crabs.sorted()[crabs.size / 2]
            }

        private val travelCosts = (0..crabs.maxByOrNull { it }!! - crabs.minByOrNull { it }!!)
            .runningReduce { a, x ->
                x + if (nonLinearFuelCost) a else 0
            }

        val minFuelCost get() = tries.minByOrNull { it.value }?.value ?: error("Not solved")

        fun solve() {
            while (true) {
                val next = nextTryPosition ?: break
                val res = crabs.tryPosition(next)
                tries[next] = res
                debugToConsole { "Trying @ $next: $res" }
            }
            debugToConsole { "Solution found" }
        }

        private fun List<Int>.tryPosition(pos: Int): Int = sumOf { travelCosts[abs(it - pos)] }

        private fun MutableMap<Int, Int>.nextPosition(initValue: () -> Int): Int? =
            when (val bestValue = minByOrNull { it.value }) {
                null -> initValue()
                maxByOrNull { it.key } -> bestValue.key + 1
                minByOrNull { it.key } -> bestValue.key - 1
                else -> null
            }
    }
}

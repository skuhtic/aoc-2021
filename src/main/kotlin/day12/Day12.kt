package day12

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day12) {
//    testPart1(checkResult = 10, inputFileNameSuffix = "s")
//    testPart1(checkResult = 19, inputFileNameSuffix = "m")
    testPart1(checkResult = 226, inputFileNameSuffix = "b")
//    testPart2(checkResult = 36, inputFileNameSuffix = "s")
//    testPart2(checkResult = 103, inputFileNameSuffix = "m")
    testPart2(checkResult = 3509, inputFileNameSuffix = "b")
//    runPart1()
//    runPart2()
}

object Day12 : Aoc2021(debug = true) {
    private fun inputData(inputReader: InputStreamReader) = inputReader.readLines()
    override fun solutionPart1(inputReader: InputStreamReader) = inputData(inputReader).let {
        Maze(it).run { solve() }
    }

    override fun solutionPart2(inputReader: InputStreamReader) = inputData(inputReader).let {
        Maze(it).run { solve(smallCaveVisitTwiceLimit = 1) }
    }


    class Maze(inputData: List<String>) {
        companion object {
            const val START = "start"
            const val END = "end"
        }

        private val caves = mutableMapOf<String, Cave>()
        private var possibleSolutions = listOf(Path())
        private var step = 0
        private val solutions = mutableListOf<Path>()

        init {
            parseInputData(inputData)
        }

        private fun parseInputData(inputData: List<String>) = inputData.map { line ->
            line.split('-').let { it[0] to it[1] }
        }.forEach { (first, second) ->
            caves.getOrPut(first) { Cave(first) }.let { cave1 ->
                caves.getOrPut(second) { Cave(second) }.let { cave2 ->
                    cave1.addConnectedCave(second)
                    cave2.addConnectedCave(first)
                }
            }
        }

        private fun getCave(name: String) = caves[name] ?: throw Exception("No reference to cave")
        private fun getCaveConnections(name: String) = caves[name]?.connected ?: throw Exception("No reference to cave")

        private fun step(smallCaveVisitTwiceLimit: Int) {
            possibleSolutions = mutableListOf<Path>().apply {
                possibleSolutions.forEach { oldPossibleSolution ->
                    oldPossibleSolution.pathList.let { oldPossiblePathList ->
                        getCaveConnections(oldPossiblePathList.last()).forEach insert@{ nextCaveName ->
                            val nextCave = getCave(nextCaveName)
                            // Check if valid
                            if (nextCave.isStart) return@insert
                            if (nextCave.isSmall) {
                                if (smallCaveVisitTwiceLimit <= 0) { // This is for part 1
                                    if (oldPossiblePathList.contains(nextCaveName)) return@insert
                                } else { // This is for part 2
                                    val newPossiblePathList = oldPossiblePathList + nextCaveName
                                    val newSmall = newPossiblePathList.filter { getCave(it).isSmall }
                                    val newSmallGroupedCount = newSmall.groupingBy { it }.eachCount()
                                    val filtered = newSmallGroupedCount.filter { it.value > smallCaveVisitTwiceLimit }
                                    if (filtered.size > 1) return@insert
                                    if ((filtered.maxOfOrNull { it.value } ?: 0) > 2) return@insert
                                }
                            }
                            // Moved if done
                            if (nextCave.isEnd) {
                                solutions.add(Path(oldPossiblePathList, nextCaveName))
                                return@insert
                            }
                            // Add rest to possible solutions
                            add(Path(oldPossiblePathList, nextCaveName))
                        }
                    }
                }
            }
            step++
        }

        fun solve(smallCaveVisitTwiceLimit: Int = 0): Int {
//            printCurrentPossibleSolutions()
            do {
                step(smallCaveVisitTwiceLimit)
//                printCurrentPossibleSolutions()
            } while (possibleSolutions.isNotEmpty())
//            printSolutions()
            return solutions.size
        }

        inner class Cave(name: String) {
            private val connections = mutableListOf<String>()
            private val isBig = name.uppercase() == name
            val isSmall = !isBig
            val isStart = name == START
            val isEnd = name == END
            val connected get() = connections.toList()

            fun addConnectedCave(caveName: String) = connections.add(caveName)
        }


        inner class Path private constructor(val pathList: List<String>) {
            constructor() : this(listOf(START))
            constructor(pathList: List<String>, additionalCaveName: String) : this(pathList.plus(additionalCaveName))

            override fun toString(): String = pathList.joinToString("-")
        }

//        private fun printCurrentPossibleSolutions() = debug { "Step: $step $possibleSolutions" }
//        private fun printSolutions() = debug { "Solutions: ${solutions.joinToString("\n", "\n", "\n")}" }
    }

}
const val START = "start"
const val END = "end"

class Maze(inputData: List<String>) {

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
//                                println("   ---- newPossiblePathList: $newPossiblePathList")
                                val newSmall = newPossiblePathList.filter { getCave(it).isSmall }
//                                println("   ---- newSmall: $newSmall")
                                val newSmallGroupedCount = newSmall.groupingBy { it }.eachCount()
//                                println("   ---- newSmallGroupedCount: $newSmallGroupedCount")
                                val filtered = newSmallGroupedCount.filter { it.value > smallCaveVisitTwiceLimit }
//                                println("   ---- Filtered: $filtered")
//                                println("   ---- Filtered.size: ${filtered.size}")
//                                println("   ---- Filtered.maxOfOrNull: ${filtered.maxOfOrNull { it.value } ?: 0}")
                                if (filtered.size > 1) return@insert
                                if ((filtered.maxOfOrNull { it.value } ?: 0) > 2) return@insert
//                                println("   ---- Inserted: $newPossiblePathList")
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
//        printCurrentPossibleSolutions()
        do {
            step(smallCaveVisitTwiceLimit)
//            printCurrentPossibleSolutions()
        } while (possibleSolutions.isNotEmpty())
//        printSolutions()
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

    private fun printCurrentPossibleSolutions() = println("Step: $step $possibleSolutions")
    private fun printSolutions() = println("Solutions: ${solutions.joinToString("\n", "\n", "\n")}")
}


fun main() {

    fun part1(input: List<String>): Int {
        return Maze(input).solve()
    }

    fun part2(input: List<String>): Int {
        return Maze(input).solve(1)
    }

    // test if implementation meets criteria from the description, like:
    val testInputSmall = readInput("Day12_test_s")
    check(part1(testInputSmall) == 10)
    check(part2(testInputSmall) == 36)

    val testInputMedium = readInput("Day12_test_m")
    check(part1(testInputMedium) == 19)
    check(part2(testInputMedium) == 103)

    val testInputBig = readInput("Day12_test_b")
    check(part1(testInputBig) == 226)
    check(part2(testInputBig) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))

}
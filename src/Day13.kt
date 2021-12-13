data class PaperDot(val x: Int, val y: Int)
data class PaperInstruction(val dir: Char, val pos: Int)

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

fun main() {

    fun part1(input: List<String>) = with(PaperFolder(input)) {
        processInstruction()
        result
    }

    fun part2(input: List<String>) = with(PaperFolder(input)) {
        @Suppress("ControlFlowWithEmptyBody")
        while (processInstruction()) {
//            println(this)
        }
            println(this)
        result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    val testPart1 = part1(testInput)
    println("Test part 1: $testPart1")
    check(testPart1 == 17)

    val testPart2 = part2(testInput)
    println("Test part 2: $testPart2")
    check(testPart2 == 16)

    val input = readInput("Day13")
//    println(part1(input))
    println(part2(input))

    // JGAJEFKU
}
package shared

import java.io.File

abstract class Aoc2021(val production: Boolean, private val debug:Boolean = true) {

    abstract fun solvePart1(debug: Boolean): Any
    abstract fun solvePart2(debug: Boolean): Any

    private val identifier: String by lazy { getClassName() }

    fun solve(bothParts: Boolean, checkPart1: Any? = null, checkPart2: Any? = null) {
        solvePart(false, if (production) null else checkPart1)
        if (bothParts) solvePart(true, if (production) null else checkPart2)
    }

    private fun solvePart(finalPart: Boolean, checkResult: Any?) {
        val part = if (finalPart) 2 else 1
        println("Running for part $part ($identifier)...")
        val result = if (finalPart) solvePart2(debug) else solvePart1(debug)
        println("Result for part $part: $result")
        if (checkResult != null) check(checkResult == result) { "Result for part $part should be $checkResult (it is $result)" }
    }

    fun inputReader(suffix: String = "") =
        File("inputs", "$identifier${if (production) "" else "_test"}$suffix.txt")
            .inputStream()
            .reader()

    fun debugToConsole(forceDebug:Boolean = false, message: () -> Any) {
        if (debug || forceDebug) println(message.invoke())
    }

    private fun getClassName(): String = this::class.simpleName.toString()

}
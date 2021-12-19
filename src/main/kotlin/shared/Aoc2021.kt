package shared

import java.io.File
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

abstract class Aoc2021(private val debug: Boolean = true) {

    abstract fun solutionPart1(inputReader: InputStreamReader): Any
    abstract fun solutionPart2(inputReader: InputStreamReader): Any

    private val identifier: String by lazy { getClassName() }

    fun testPart1(checkResult: Any? = null, inputFileNameSuffix: String = "") = runPart(
        finalPart = false,
        production = false,
        checkResult = checkResult,
        suffix = inputFileNameSuffix
    )

    fun testPart2(checkResult: Any? = null, inputFileNameSuffix: String = "") = runPart(
        finalPart = true,
        production = false,
        checkResult = checkResult,
        suffix = inputFileNameSuffix
    )

    fun runPart1(checkResult: Any? = null) = runPart(
        finalPart = false,
        production = true,
        checkResult = checkResult,
        suffix = ""
    )

    fun runPart2(checkResult: Any? = null) = runPart(
        finalPart = true,
        production = true,
        checkResult = checkResult,
        suffix = ""
    )

    private fun runPart(
        finalPart: Boolean,
        production: Boolean,
        checkResult: Any?,
        suffix: String
    ) {
        measureTimeMillis {
            val partNo = if (finalPart) 2 else 1
            val runOrTest = if (production) "Running" else "Testing"
            val inputFileName = inputFileName(production, suffix)
            println("\n$runOrTest $identifier part $partNo using $inputFileName ...")
            val inputReader = inputReader(inputFileName)
            val result = if (finalPart) solutionPart2(inputReader) else solutionPart1(inputReader)
            val status =
                if (checkResult != null) if (checkResult == result) "(ok)" else "(should be $checkResult)" else ""
            println("$identifier part $partNo result: $result $status")
            if (checkResult != null) check(checkResult == result)
        }.let { println("Done in: ${it.toDouble() / 1000} seconds...\n") }
    }

    private fun inputFileName(production: Boolean, suffix: String) =
        "$identifier${if (production) "" else "_test"}${if (suffix.isNotBlank()) "_$suffix" else ""}.txt"

    private fun inputReader(fileName: String) = File("inputs", fileName).inputStream().reader()

    fun debugToConsole(forceDebug: Boolean = false, message: () -> Any) {
        if (debug || forceDebug) println(message.invoke())
    }

    private fun getClassName(): String = this::class.simpleName.toString()
}
package wordsvirtuoso

import java.io.File
import kotlin.system.exitProcess

val baseDir = ""//"C:/Users/L-L/Desktop/"
var countInvalid = 0
val allClues = mutableListOf<String>()
val wrongChards = mutableSetOf<Char>()

fun main(args: Array<String>) {

    if(args.size != 2) {
        println("Error: Wrong number of arguments.")
        exitProcess(0)
    }
    val files = arrayOf(File(baseDir + args[0]), File(baseDir + args[1]))

    if (!files[0].exists()) {
        println("Error: The words file ${files[0]} doesn't exist.")
        exitProcess(0)
    }
    if (!files[1].exists()) {
        println("Error: The candidate words file ${files[1]} doesn't exist.")
        exitProcess(0)
    }
    val words = files[0].readLines().map { it.uppercase() }
    countInvalid = 0
    words.forEach { isWordValid(it) }
    if (countInvalid > 0) {
        println("Error: $countInvalid invalid words were found in the ${files[0]} file.")
        exitProcess(0)
    }
    val candidates = files[1].readLines().map { it.uppercase() }
    countInvalid = 0
    candidates.forEach { isWordValid(it) }
    if (countInvalid > 0) {
        println("Error: $countInvalid invalid words were found in the ${files[1]} file.")
        exitProcess(0)
    }
    val notContained = candidates.count { !words.contains(it) }
    if (notContained > 0) {
        println("Error: $notContained candidate words are not included in the ${files[0]} file.")
        exitProcess(0)
    }
    println("Words Virtuoso")
    var word = candidates.random().uppercase()
    var turns = 0
    allClues.clear()
    wrongChards.clear()
    var start = System.currentTimeMillis()

    while(true) {
        turns++
        println("Input a 5-letter word:")
        val guess = readln().trim().uppercase()
        if (guess == "EXIT") {
            println("The game is over.")
            exitProcess(0)
        } else if (guess == word) {
            giveClue(guess, word)
            allClues.forEach { println(it) }
            println()
            println("Correct!")
            if (turns == 1) {
                println("Amazing luck! The solution was found at once.")
            }
            else {
                var end = System.currentTimeMillis()
                val duration = (end - start) / 1000
                println("The solution was found after $turns tries in $duration seconds.")
            }
            exitProcess(0)
        } else if (!isWordValid(guess, printOrNot = true)) {
            word = candidates.random().uppercase()
            continue
        }
        else if (!words.contains(guess)) {
            println("The input word isn't included in my words list.")
            word = candidates.random().uppercase()
            continue
        }
        else {
            giveClue(guess, word)
            allClues.forEach { println(it) }
            println()
            println("\u001B[48:5:14m${wrongChards.sorted().joinToString("")}\u001B[0m")
            println()
        }
    }
}

fun giveClue(guess: String, word: String){
    var clue = ""
    for (i in guess.indices) {
        if (guess[i] == word[i]) clue += "\u001B[48:5:10m${guess[i]}\u001B[0m"
        else if (word.contains(guess[i])) clue += "\u001B[48:5:11m${guess[i]}\u001B[0m"
        else {
            wrongChards.add(guess[i])
            clue += "\u001B[48:5:7m${guess[i]}\u001B[0m"
        }
    }
    allClues.add(clue)
}

fun isWordValid(word: String, printOrNot: Boolean = false): Boolean {
    var isValid = false

    if (word.length != 5) {
        if (printOrNot) println("The input isn't a 5-letter word.")
    }
    else if(!word.matches("[A-Z]+".toRegex())){
        if (printOrNot) println("One or more letters of the input aren't valid.")
    }
    else if(word.any{ letter -> word.count{ it == letter } > 1 }) {
        if (printOrNot) println("The input has duplicate letters.")
    }
    else isValid =  true //println("The input is a valid string.")
    if (!isValid) {
        countInvalid++
    }
    return isValid
}

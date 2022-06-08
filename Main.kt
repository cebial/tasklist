package tasklist

import kotlinx.datetime.*
import java.time.LocalTime
import java.time.format.DateTimeParseException

data class Task(val date: String, val time: String, val priority: String, val description: List<String>)

class TaskList {
    private val tasks = mutableListOf<Task>()

    private fun String.fixSingleDigits() = "\\b(\\d)\\b".toRegex().replace(this, "0$1")

    fun add() {
        var priority = ""
        do println("Input the task priority (C, H, N, L):")
        while (readln().uppercase().takeIf { it in "CHNL" }?.let { priority = it } == null)

        var date = ""
        while (date.isEmpty()) {
            println("Input the date (yyyy-mm-dd):")
            try {
                readln().fixSingleDigits().toLocalDate().also { date = it.toString() }
            } catch (e: IllegalArgumentException) {
                println("The input date is invalid")
            }
        }

        var time = ""
        while (time.isEmpty()) {
            println("Input the time (hh:mm):")
            try {
                LocalTime.parse(readln().fixSingleDigits()).also { time = it.toString() }
            } catch (e: DateTimeParseException) {
                println("The input time is invalid")
            }
        }

        println("Input a new task (enter a blank line to end):")
        val desc = mutableListOf<String>()
        while (readln().trim().takeIf { it.isNotBlank() }?.let { desc.add(it) } == true);

        if (desc.isEmpty()) println("The task is blank")
        else tasks.add(Task(date, time, priority, desc))
    }

    fun print() {
        if (tasks.isEmpty()) {
            println("No tasks have been input"); return
        }

        tasks.forEachIndexed { i, t ->
            println("%-2s".format(i + 1) + " ${t.date} ${t.time} ${t.priority}")
            t.description.forEach { println("   $it") }
            println()
        }
    }
}

fun main() {
    val tasklist = TaskList()

    while (true) {
        println("Input an action (add, print, end):")
        when (readln().lowercase()) {
            "add" -> tasklist.add()
            "print" -> tasklist.print()
            "end" -> break
            else -> println("The input action is invalid")
        }
    }
    println("Tasklist exiting!")
}
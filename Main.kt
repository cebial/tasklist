package tasklist

import kotlinx.datetime.*
import java.time.LocalTime
import java.time.format.DateTimeParseException

enum class Ansi(val color: String) {
    RED("\u001B[101m \u001B[0m"),
    YELLOW("\u001B[103m \u001B[0m"),
    GREEN("\u001B[102m \u001B[0m"),
    BLUE("\u001B[104m \u001B[0m"),
}

val prioToAnsi = mapOf("C" to Ansi.RED.color, "H" to Ansi.YELLOW.color, "N" to Ansi.GREEN.color, "L" to Ansi.BLUE.color)
val dueToAnsi = mapOf("I" to Ansi.GREEN.color, "T" to Ansi.YELLOW.color, "O" to Ansi.RED.color)

data class Task(var date: String, var time: String, var prio: String, var text: List<String>) {
    val due: String
        get() {
            val t = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0"))
                .date.daysUntil(date.toLocalDate())
            return if (t > 0) "I" else if (t < 0) "O" else "T"
        }
}

class TaskList {
    private val tasks = mutableListOf<Task>()

    private fun String.fixSingleDigits() = "\\b(\\d)\\b".toRegex().replace(this, "0$1")

    private fun requestPriority(): String {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().takeIf { it in "CHNL" }?.also { return it }
        }
    }

    private fun requestDate(): String {
        while (true) {
            println("Input the date (yyyy-mm-dd):")
            try { // beware: using exceptions for flow, .also is only executed for a valid date
                readln().fixSingleDigits().toLocalDate().also { return it.toString() }
            } catch (ignored: IllegalArgumentException) {}
            println("The input date is invalid")
        }
    }

    private fun requestTime(): String {
        while (true) {
            println("Input the time (hh:mm):")
            try { // beware: using exceptions for flow, .also is only executed for a valid time
                LocalTime.parse(readln().fixSingleDigits()).also { return it.toString() }
            } catch (ignored: DateTimeParseException) {}
            println("The input time is invalid")
        }
    }

    private fun requestText(): List<String> {
        println("Input a new task (enter a blank line to end):")
        val text = mutableListOf<String>()
        while (readln().trim().takeIf { it.isNotBlank() }?.let { text.add(it) } == true);
        return text
    }

    private fun addTask() {
        val prio = requestPriority()
        val date = requestDate()
        val time = requestTime()
        val text = requestText()

        if (text.isEmpty()) println("The task is blank")
        else tasks.add(Task(date, time, prio, text))
    }

    private fun printList(): Boolean {
        if (tasks.isEmpty()) {
            println("No tasks have been input"); return false
        }

        val bar = "+----+------------+-------+---+---+--------------------------------------------+\n"
        println("$bar| N  |    Date    | Time  | P | D |                   Task                     |$bar")

        tasks.forEachIndexed { i, t ->
            print("| ${"%-2s".format(i + 1)} | ${t.date} | ${t.time} | ${prioToAnsi[t.prio]} | ${dueToAnsi[t.due]} |")

            t.text.forEachIndexed { j, line ->
                var n = 0
                while (n < line.length) {
                    if (j + n > 0) print("|    |            |       |   |   |")
                    println("%-44s|".format(line.substring(n, minOf(n + 44, line.length))))
                    n += 44
                }
            }
            print(bar)
        }
        return true
    }

    private fun getTaskIndex(): Int {
        while (true) {
            println("Input the task number (1-${tasks.size}):")
            readln().toIntOrNull()?.takeIf { it in 1..tasks.size }?.let { return it - 1 }
            println("Invalid task number")
        }
    }

    private fun inputField(): String {
        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            readln().takeIf { it in listOf("priority", "date", "time", "task") }
                ?.let { return it } ?: println("Invalid field")
        }
    }

    private fun editTask() {
        printList() || return // is this bad? kinda like it...

        val idx = getTaskIndex()
        when (inputField()) {
            "priority" -> tasks[idx].prio = requestPriority()
            "date" -> tasks[idx].date = requestDate()
            "time" -> tasks[idx].time = requestTime()
            "task" -> requestText().takeIf { it.isNotEmpty() }?.let { tasks[idx].text = it }
        }

        println("The task is changed")
    }

    private fun deleteTask() {
        printList() || return

        tasks.removeAt(getTaskIndex())
        println("The task is deleted")
    }

    fun run() {
        while (true) {
            println("Input an action (add, print, edit, delete, end):")
            when (readln().lowercase()) {
                "add" -> addTask()
                "print" -> printList()
                "edit" -> editTask()
                "delete" -> deleteTask()
                "end" -> break
                else -> println("The input action is invalid")
            }
        }
        println("Tasklist exiting!")
    }
}

fun main() = TaskList().run()

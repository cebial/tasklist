package tasklist

import kotlinx.datetime.*
import java.time.LocalTime
import java.time.format.DateTimeParseException

data class Task(var date: String, var time: String, var priority: String, var description: List<String>) {
    val dueTag: String
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

    private fun requestDescription(): List<String> {
        println("Input a new task (enter a blank line to end):")
        val desc = mutableListOf<String>()
        while (readln().trim().takeIf { it.isNotBlank() }?.let { desc.add(it) } == true);
        return desc
    }

    private fun addTask() {
        val prio = requestPriority()
        val date = requestDate()
        val time = requestTime()
        val desc = requestDescription()

        if (desc.isEmpty()) println("The task is blank")
        else tasks.add(Task(date, time, prio, desc))
    }

    private fun printList(): Boolean {
        if (tasks.isEmpty()) {
            println("No tasks have been input"); return false
        }

        tasks.forEachIndexed { i, t ->
            println("%-2s".format(i + 1) + " ${t.date} ${t.time} ${t.priority} ${t.dueTag}")
            t.description.forEach { println("   $it") }
            println()
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
            "priority" -> tasks[idx].priority = requestPriority()
            "date" -> tasks[idx].date = requestDate()
            "time" -> tasks[idx].time = requestTime()
            "task" -> requestDescription().takeIf { it.isNotEmpty() }?.let { tasks[idx].description = it }
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

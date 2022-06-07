package tasklist

fun main() {
    val tasks = mutableListOf<String>()
    var input : String

    println("Input the tasks (enter a blank line to end):")
    while (readln().trim().also { input = it }.isNotEmpty()) tasks.add(input)

    if (tasks.isEmpty()) println("No tasks have been input")
    else tasks.forEachIndexed { i, s -> println("%-2d $s".format(i + 1)) }
}
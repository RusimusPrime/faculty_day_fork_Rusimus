import java.time.Month
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime


fun main() {
    final_task()
}

/*
1) Строки + регулярные выражения
["Name: Ivan, score=17", ...]
Извлечь имя и score, собрать пары, вывести победителя.
*/
fun task1() {
    val lines = listOf(
        "Name: Ivan, score=17",
        "Name: Olga, score=23",
        "Name: Max, score=5"
    )

    val re = Regex("""^Name:\s*([A-Za-z]+)\s*,\s*score=(\d+)\s*$""")

    val pairs: List<Pair<String, Int>> = lines.mapNotNull { s ->
        val m = re.find(s) ?: return@mapNotNull null
        val name = m.groupValues[1]
        val score = m.groupValues[2].toInt()
        name to score
    }

    println("Task 1 pairs: $pairs")

    val best = pairs.maxByOrNull { it.second }
    if (best != null) {
        println("Task 1 best: ${best.first} (${best.second})")
    } else {
        println("Task 1: no valid lines")
    }
}

/*
2) Даты + коллекции
["2026-01-22", ...]
Преобразовать в даты, отсортировать, посчитать сколько в январе 2026.
*/
fun task2() {
    val dateStrings = listOf(
        "2026-01-22",
        "2026-02-01",
        "2025-12-31",
        "2026-01-05"
    )

    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val dates = dateStrings.map { LocalDate.parse(it, fmt) }.sorted()

    println("Task 2 sorted dates: ${dates.joinToString { it.format(fmt) }}")

    val countJan2026 = dates.count { it.year == 2026 && it.month == Month.JANUARY }
    println("Task 2 count in Jan 2026: $countJan2026")
}

/*
3) Коллекции + строки
"apple orange apple banana orange apple"
Частоты слов, вывести слова с частотой > 1 по алфавиту.
*/
fun task3() {
    val text = "apple orange apple banana orange apple"

    val words = text.trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }

    val freq = mutableMapOf<String, Int>()
    for (w in words) {
        freq[w] = (freq[w] ?: 0) + 1
    }

    println("Task 3 freq: $freq")

    val repeated = freq
        .filter { (_, c) -> c > 1 }
        .keys
        .sorted()

    println("Task 3 repeated words: ${repeated.joinToString(", ")}")
}


// 4 Задание: Регулярные выражения — фильтрация по формату
fun tusk4(): List<String> {
    val list = listOf("A-123", "B-7", "AA-12", "C-001", "D-99x")
    val regex = Regex("^[A-Z]-\\d{1,3}\$")
    return list.filter { regex.matches(it) }
}

// 5 Задание: Строки — нормализация пробелов
fun tusk5(): List<String> {
    val list = listOf(" Hello world ", "A B C", " one")
    return list.map {
        it.trim()
            .replace("\\s+".toRegex(), " ")
    }
}

// 6 Задание: Даты — разница в днях между парой дат
fun tusk6(): List<Long> {
    val pairs = listOf(
        "2026-01-01" to "2026-01-10",
        "2025-12-31" to "2026-01-01",
        "2026-02-01" to "2026-01-22"
    )
    return pairs.map { (d1, d2) ->
        val date1 = LocalDate.parse(d1, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val date2 = LocalDate.parse(d2, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ChronoUnit.DAYS.between(date1, date2)
    }
}

// 7 Задание: Коллекции — группировка по ключу с сохранением порядка
fun tusk7(): Map<String, List<String>> {
    val list = listOf("math:Ivan", "bio:Olga", "math:Max", "bio:Ivan", "cs:Olga")
    val map = linkedMapOf<String, MutableList<String>>()
    for (entry in list) {
        val (subject, student) = entry.split(":")
        val students = map.getOrPut(subject) { mutableListOf() }
        if (student !in students) {
            students.add(student)
        }
    }

    return map
}

// 8 Задание: Регулярные выражения + даты — извлечение времени из текста
fun tusk8(): List<String> {
    val list = listOf(
        "Start at 2026/01/22 09:14",
        "No time here",
        "End: 22-01-2026 18:05"
    )


    return list.mapNotNull { text ->
        var match = Regex("""(\d{4})/(\d{2})/(\d{2}) (\d{2}):(\d{2})""").find(text)
        if (match != null) {
            val (y, m, d, h, min) = match.destructured
            "$y-$m-$d $h:$min"
        } else {
            match = Regex("""(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2})""").find(text)
            if (match != null) {
                val (d, m, y, h, min) = match.destructured
                "$y-$m-$d $h:$min"
            } else null
        }
    }
}


data class LogEntry(val dt: String, val id: Int, val status: String)

fun normalize(line: String): LogEntry? {
    val trimmed = line.trim()

    val regexA = Regex(
        """(\d{4}-\d{2}-\d{2} \d{2}:\d{2})\s*\|\s*ID\s*:\s*(\d+)\s*\|\s*STATUS\s*:\s*(\w+)""",
        RegexOption.IGNORE_CASE
    )
    val regexB = Regex(
        """TS\s*=\s*(\d{2}/\d{2}/\d{4}-\d{2}:\d{2})\s*;\s*status\s*=\s*(\w+)\s*;\s*#(\d+)""",
        RegexOption.IGNORE_CASE
    )
    val regexC =
        Regex("""\[(\d{2}\.\d{2}\.\d{4} \d{2}:\d{2})\]\s*(\w+)\s*\(id\s*:\s*(\d+)\)""", RegexOption.IGNORE_CASE)

    fun parseDateTime(input: String, inputFormat: DateTimeFormatter, outputFormat: DateTimeFormatter): String? {
        return try {
            val dt = LocalDateTime.parse(input, inputFormat)
            outputFormat.format(dt)
        } catch (e: Exception) {
            null
        }
    }

    val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    regexA.matchEntire(trimmed)?.let {
        val (dtRaw, idRaw, statusRaw) = it.destructured
        val dt = parseDateTime(dtRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), outputFormat) ?: return null
        return LogEntry(dt, idRaw.toInt(), statusRaw.toLowerCase())
    }

    regexB.matchEntire(trimmed)?.let {
        val (dtRaw, statusRaw, idRaw) = it.destructured
        val dt = parseDateTime(dtRaw, DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"), outputFormat) ?: return null
        return LogEntry(dt, idRaw.toInt(), statusRaw.toLowerCase())
    }

    regexC.matchEntire(trimmed)?.let {
        val (dtRaw, statusRaw, idRaw) = it.destructured
        val dt = parseDateTime(dtRaw, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"), outputFormat) ?: return null
        return LogEntry(dt, idRaw.toInt(), statusRaw.toLowerCase())
    }

    return null
}

fun final_task() {
    val logs = listOf(
        "2026-01-22 09:14 | ID:042 | STATUS:sent",
        "TS=22/01/2026-09:27; status=delivered; #042",
        "2026-01-22 09:10 | ID:043 | STATUS:sent",
        "2026-01-22 09:18 | ID:043 | STATUS:delivered",
        "TS=22/01/2026-09:05; status=sent; #044",
        "[22.01.2026 09:40] delivered (id:044)",
        "2026-01-22 09:20 | ID:045 | STATUS:sent",
        "[22.01.2026 09:33] delivered (id:045)",
        "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
        " [22.01.2026 10:05]   DELIVERED   (ID:046) "
    )

    val normalizedLogs = mutableListOf<LogEntry>()
    val brokenLogs = mutableListOf<String>()
    for (line in logs) {
        val normalized = normalize(line)
        if (normalized == null) brokenLogs.add(line)
        else normalizedLogs.add(normalized)
    }
    println("Нормализованные логи:")
    normalizedLogs.forEach { println(it) }
    println("Битые логи")
    brokenLogs.forEach { println(it) }

    val logsGrouped = normalizedLogs.groupBy { it.id }

    val incomplete = mutableListOf<Int>()
    val timeErrors = mutableListOf<Int>()
    val deliveryTimes = mutableListOf<Pair<Int, Long>>()

    for ((id, events) in logsGrouped) {
        val sentTimes = events.filter { it.status == "sent" }
            .map { LocalDateTime.parse(it.dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) }
        val deliveredTimes = events.filter { it.status == "delivered" }
            .map { LocalDateTime.parse(it.dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) }

        if (sentTimes.isEmpty() || deliveredTimes.isEmpty()) {
            incomplete.add(id)
            continue
        }

        val sentTime = sentTimes.minOrNull()!!
        val deliveredTime = deliveredTimes.minOrNull()!!

        if (deliveredTime.isBefore(sentTime)) {
            timeErrors.add(id)
            continue
        }

        val duration = ChronoUnit.MINUTES.between(sentTime, deliveredTime)
        deliveryTimes.add(id to duration)
    }

    val sortedByDuration = deliveryTimes.sortedByDescending { it.second }
    println("Длительности доставки (id, минуты):")
    sortedByDuration.forEach { println(it) }

    val longestOrder = sortedByDuration.maxByOrNull { it.second }
    println("Самый долгий заказ:")
    println(longestOrder)

    val violators = sortedByDuration.filter { it.second > 20 }
    println("Нарушители (доставка > 20 минут):")
    violators.forEach { println(it) }

    println("Неполные id (нет sent или delivered): $incomplete")
    println("Ошибки времени (delivered раньше sent): $timeErrors")


    val deliveredHoursCount = normalizedLogs
        .filter { it.status == "delivered" }
        .groupingBy { LocalDateTime.parse(it.dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).hour }
        .eachCount()

    val maxHour = deliveredHoursCount.maxByOrNull { it.value }
    println("Час с наибольшим количеством delivered: ${maxHour?.key} (событий: ${maxHour?.value})")

    val duplicates = normalizedLogs.groupBy { it.id }.mapValues { entry ->
        val sentCount = entry.value.count { it.status == "sent" }
        val deliveredCount = entry.value.count { it.status == "delivered" }
        Pair(sentCount, deliveredCount)
    }.filter { it.value.first > 1 || it.value.second > 1 }

    println("Дубли по id (id -> (sentCount, deliveredCount)):")
    duplicates.forEach { (id, counts) ->
        println("id=$id, sent=${counts.first}, delivered=${counts.second}")
    }
}
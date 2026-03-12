import java.net.HttpURLConnection
import java.net.URL

// ===========================================
// Задача 6. Клиент для сервера заметок
// ===========================================
// Цель: написать клиент, который тестирует все эндпоинты сервера.
// Перед запуском: запустить Task6_Server.kt
//
// TODO 1: Реализовать request() — универсальную функцию отправки запросов
// TODO 2: В main() выполнить 8 шагов (ниже), вывести код и тело каждого ответа

val BASE = "http://localhost:8080/api/notes"

/** Отправить HTTP-запрос.
 *  @param url    — полный URL
 *  @param method — HTTP-метод
 *  @param body   — JSON-тело (null для GET/DELETE)
 *  @return Pair(statusCode, responseBody)
 */
fun request(url: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")
    connection.doOutput = (body != null)  // разрешаем запись тела, если оно есть

    return try {
        // Если есть тело, записываем его в output stream
        if (body != null) {
            connection.outputStream.use { os ->
                os.write(body.toByteArray(Charsets.UTF_8))
            }
        }

        val responseCode = connection.responseCode
        val responseBody = if (responseCode in 200..299) {
            connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
        } else {
            connection.errorStream?.bufferedReader(Charsets.UTF_8)?.readText() ?: ""
        }
        responseCode to responseBody
    } finally {
        connection.disconnect()
    }
}

fun main() {
    // TODO 2: выполнить 8 шагов, каждый раз вызывая request() и выводя результат

    // Шаг 1: получить все заметки
    println("=== 1. GET /api/notes — все заметки ===")
    val (code1, body1) = request(BASE, "GET")
    println("Код: $code1\nТело: $body1\n")

    // Шаг 2: создать новую заметку
    println("=== 2. POST /api/notes — создать заметку ===")
    val newNote = """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}"""
    val (code2, body2) = request(BASE, "POST", newNote)
    println("Код: $code2\nТело: $body2\n")

    // Шаг 3: получить заметку по id
    println("=== 3. GET /api/notes/1 — одна заметка ===")
    val (code3, body3) = request("$BASE/1", "GET")
    println("Код: $code3\nТело: $body3\n")

    // Шаг 4: обновить заметку
    println("=== 4. PUT /api/notes/1 — обновить заметку ===")
    val updatedNote = """{"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}"""
    val (code4, body4) = request("$BASE/1", "PUT", updatedNote)
    println("Код: $code4\nТело: $body4\n")

    // Шаг 5: фильтр по тегу
    println("=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
    val (code5, body5) = request("$BASE?tag=учёба", "GET")
    println("Код: $code5\nТело: $body5\n")

    // Шаг 6: удалить заметку
    println("=== 6. DELETE /api/notes/1 — удалить заметку ===")
    val (code6, body6) = request("$BASE/1", "DELETE")
    println("Код: $code6\nТело: $body6\n")

    // Шаг 7: запросить несуществующую заметку (ожидаем 404)
    println("=== 7. GET /api/notes/999 — несуществующая заметка ===")
    val (code7, body7) = request("$BASE/999", "GET")
    println("Код: $code7\nТело: $body7\n")

    // Шаг 8: финальное состояние
    println("=== 8. GET /api/notes — финальное состояние ===")
    val (code8, body8) = request(BASE, "GET")
    println("Код: $code8\nТело: $body8")
}
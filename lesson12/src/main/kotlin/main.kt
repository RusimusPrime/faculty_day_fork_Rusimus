import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

// ===========================================
// Задача 1. HTTP-запросы через HttpURLConnection
// ===========================================
// Цель: научиться отправлять GET и POST запросы, читать ответ и статус-код.
// API: https://jsonplaceholder.typicode.com
//
// TODO 1: Отправить GET /posts/1, вывести статус-код и тело ответа
// TODO 2: Отправить POST /posts с JSON-телом, вывести статус-код и тело
// TODO 3: Отправить GET /posts/9999, обработать ошибку (код != 2xx)
//
// Подсказки:
//   val connection = URL("...").openConnection() as HttpURLConnection
//   connection.requestMethod = "GET"             — задать метод
//   connection.doOutput = true                   — разрешить отправку тела
//   connection.setRequestProperty("Content-Type", "application/json") — заголовок
//   connection.outputStream.write(json.toByteArray())                 — записать тело
//   connection.responseCode                      — получить статус-код
//   connection.inputStream.bufferedReader().readText()  — прочитать тело ответа
//   connection.errorStream                       — поток ошибок (при коде 4xx/5xx)
//   connection.disconnect()                      — закрыть соединение


fun main() {
    disableSslVerification()

    // TODO 1: GET /posts/1
    println("=== GET /posts/1 ===")

    val getUrl = URL("https://jsonplaceholder.typicode.com/posts/1")
    val getConn = getUrl.openConnection() as HttpURLConnection
    getConn.requestMethod = "GET"
    println("Код: ${getConn.responseCode}")
    val getBody = getConn.inputStream.bufferedReader().readText()
    println("Тело: $getBody")
    getConn.disconnect()

    // TODO 2: POST /posts
    println("\n=== POST /posts ===")
    val postUrl = URL("https://jsonplaceholder.typicode.com/posts")
    val postConn = postUrl.openConnection() as HttpURLConnection
    postConn.requestMethod = "POST"
    postConn.doOutput = true
    postConn.setRequestProperty("Content-Type", "application/json; utf-8")
    postConn.setRequestProperty("Accept", "application/json")

    val jsonInputString = """
        {
            "title": "sadkhgkhasdg",
            "body": "barrrrr",
            "userId": 1
        }
    """.trimIndent()

    postConn.outputStream.use { os ->
        val input = jsonInputString.toByteArray(Charsets.UTF_8)
        os.write(input, 0, input.size)
    }

    println("Код: ${postConn.responseCode}")
    val responseBody = postConn.inputStream.bufferedReader().readText()
    println("Тело: $responseBody")

    postConn.disconnect()


    // TODO 3: GET /posts/9999 (несуществующий ресурс)
    println("\n=== GET /posts/9999 ===")
    val getUrl2 = URL("https://jsonplaceholder.typicode.com/posts/9999")
    val getConn2 = getUrl2.openConnection() as HttpURLConnection
    getConn2.requestMethod = "GET"
    println("Код: ${getConn2.responseCode}")
    if (getConn2.responseCode == 200) {
        val getBody2 = getConn2.inputStream.bufferedReader().readText()
        println("Тело: $getBody2")
    } else {
        println(getConn2.responseCode)
    }

}
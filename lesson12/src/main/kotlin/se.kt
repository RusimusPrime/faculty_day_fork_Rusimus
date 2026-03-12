import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import java.util.Base64
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

// ===========================================
// Задача 3. JWT — авторизация
// ===========================================
// Цель: понять структуру JWT, собрать и декодировать токен, отправить запрос с Bearer-авторизацией.
// API: https://httpbin.org/bearer (возвращает 200 если есть Bearer, 401 если нет)
//
// TODO 1: Собрать JWT из трёх частей (header, payload, signature) в Base64URL
// TODO 2: Декодировать JWT обратно — вывести header и payload как JSON
// TODO 3: Отправить GET https://httpbin.org/bearer с заголовком Authorization: Bearer <token>
// TODO 4: Отправить тот же запрос БЕЗ токена — убедиться, что вернулся 401
// TODO 5: Подменить payload (role: student → admin), объяснить почему сервер отвергнет
//
// Подсказки:
//   Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) — кодирование
//   Base64.getUrlDecoder().decode(string)                        — декодирование
//   JWT = base64(header) + "." + base64(payload) + "." + base64(signature)
//
// Вопросы после выполнения:
//   - Из каких 3 частей состоит JWT?
//   - Можно ли подменить payload и использовать токен? Почему нет?
//   - Что такое access token и refresh token?
//

/**
 * Отключает проверку SSL-сертификатов (для учебных целей).
 * В реальных проектах так делать нельзя!
 */
fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}

fun main() {
    disableSslVerification()

    val encoder = Base64.getUrlEncoder().withoutPadding()
    val decoder = Base64.getUrlDecoder()

    //  TODO 1: Сборка JWT
    println("Сборка JWT")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "IamOptimusPrime"
    val encodedHeader = encoder.encodeToString(header.toByteArray(Charsets.UTF_8))
    val encodedPayload = encoder.encodeToString(payload.toByteArray(Charsets.UTF_8))
    val encodedSignature = encoder.encodeToString(fakeSignature.toByteArray(Charsets.UTF_8))

    val token = "$encodedHeader.$encodedPayload.$encodedSignature"
    println("JWT Token: $token\n")

    // TODO 2: Декодирование JWT
    println("Декодирование JWT")
    val parts = token.split(".")
    if (parts.size == 3) {
        val decodedHeader = String(decoder.decode(parts[0]), Charsets.UTF_8)
        val decodedPayload = String(decoder.decode(parts[1]), Charsets.UTF_8)
        println("Header: $decodedHeader")
        println("Payload: $decodedPayload\n")
    } else {
        println("Неверный формат JWT\n")
    }

    // TODO 3: GET /bearer с токеном
    println("GET /bearer (с токеном)")
    val url = URL("https://httpbin.org/bearer")
    var connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Authorization", "Bearer $token")
    val codeWithToken = connection.responseCode
    val bodyWithToken = if (codeWithToken in 200..299) {
        connection.inputStream.bufferedReader().readText()
    } else {
        connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
    }
    connection.disconnect()
    println("Код: $codeWithToken")
    println("Тело:\n$bodyWithToken\n")

    //  TODO 4: GET /bearer без токена
    println("GET /bearer (без токена)")
    connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    val codeNoToken = connection.responseCode
    val bodyNoToken = if (codeNoToken in 200..299) {
        connection.inputStream.bufferedReader().readText()
    } else {
        connection.errorStream?.bufferedReader()?.readText() ?: "No body"
    }
    connection.disconnect()
    println("Код: $codeNoToken")
    if (codeNoToken == 401) {
        println("Ожидаемый 401, тело ошибки:\n$bodyNoToken\n")
    } else {
        println("Неожиданный код ответа: $codeNoToken, тело:\n$bodyNoToken\n")
    }

    //  TODO 5: Подмена payload
    println("Подмена payload (role: student → admin)")
    val modifiedPayload = """{"sub":"1","name":"Ivan Petrov","role":"admin","iat":1234567890}"""
    val encodedModifiedPayload = encoder.encodeToString(modifiedPayload.toByteArray(Charsets.UTF_8))
    val modifiedToken = "$encodedHeader.$encodedModifiedPayload.$encodedSignature"
    println("Подменённый JWT:\n$modifiedToken")

    connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Authorization", "Bearer $modifiedToken")
    val codeModified = connection.responseCode
    val bodyModified = if (codeModified in 200..299) {
        connection.inputStream.bufferedReader().readText()
    } else {
        connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
    }
    connection.disconnect()
    println("Код: $codeModified")
    if (codeModified == 401) {
        println("Тело ошибки:\n$bodyModified")
    } else {
        println("Тело:\n$bodyModified")
    }

    println("\nОбъяснение:")
    println(
        "Подмена payload нарушает целостность токена, так как подпись не совпадает с изменённым содержимым. " +
                "Сервер проверяет подпись и отклоняет токены с некорректной подписью, поэтому подменённый токен недействителен."
    )
}
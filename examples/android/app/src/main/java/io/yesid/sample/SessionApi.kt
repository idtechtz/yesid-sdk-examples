package io.yesid.sample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Asks YOUR backend for a session. The backend calls the Yesid API with the secret API key and returns {"sessionId": "..."}.
 * The secret key never belongs in the app. See ../backend/server.js for a minimal example.
 */
object SessionApi {
    sealed class Result {
        data class Ok(val sessionId: String) : Result()
        data class Failed(val message: String) : Result()
    }

    suspend fun create(): Result = withContext(Dispatchers.IO) {
        try {
            val conn = (URL(Config.sessionUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 30_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                outputStream.use { it.write("{}".toByteArray()) }
            }
            val code = conn.responseCode
            val body = (if (code in 200..299) conn.inputStream else conn.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) return@withContext Result.Failed("Your session endpoint answered HTTP $code")
            val id = JSONObject(body).optString("sessionId")
            if (id.isBlank()) Result.Failed("Your session endpoint did not return a sessionId") else Result.Ok(id)
        } catch (e: Exception) {
            Result.Failed("Could not reach your session endpoint: ${e.javaClass.simpleName}")
        }
    }
}

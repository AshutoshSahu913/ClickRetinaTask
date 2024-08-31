package com.example.myapplication.repo

import com.example.myapplication.Model.Choice
import com.example.myapplication.Model.Message
import com.example.myapplication.Model.TaskResponse
import com.example.myapplication.Model.Usage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class Repository {

    suspend fun fetchData(): TaskResponse? {
        return withContext(Dispatchers.IO) {
            val url = URL("https://www.jsonkeeper.com/b/6HBE")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val inputStream = urlConnection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Parse JSON response
                val jsonObject = JSONObject(response)

                val id = jsonObject.getString("id")
                val objectType = jsonObject.getString("object")
                val created = jsonObject.getLong("created")
                val model = jsonObject.getString("model")

                val choicesJsonArray = jsonObject.getJSONArray("choices")
                val choices = mutableListOf<Choice>()

                for (i in 0 until choicesJsonArray.length()) {
                    val choiceJsonObject = choicesJsonArray.getJSONObject(i)
                    val index = choiceJsonObject.getInt("index")

                    val messageJsonObject = choiceJsonObject.getJSONObject("message")
                    val role = messageJsonObject.getString("role")

                    val content = messageJsonObject.getString("content")

                    val refusal = messageJsonObject.optString("refusal", null)
                    val message = Message(role=role, content =  content, refusal =  refusal)

                    val logprobs = choiceJsonObject.optString("logprobs", null)
                    val finishReason = choiceJsonObject.optString("finish_reason", null)
                    choices.add(
                        Choice(
                            index = index,
                            message = message,
                            logprobs = logprobs,
                            finish_reason = finishReason
                        )
                    )
                }

                val usageObject = jsonObject.optJSONObject("usage")
                val usage = usageObject?.let {
                    Usage(
                        it.getInt("prompt_tokens"),
                        it.getInt("completion_tokens"),
                        it.getInt("total_tokens")
                    )
                }
                val systemFingerprint = jsonObject.optString("system_fingerprint", null)

                TaskResponse(
                    id = id,
                    `object` = objectType,
                    created = created,
                    model = model,
                    choices = choices,
                    usage = usage!!,
                    system_fingerprint = systemFingerprint
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                urlConnection.disconnect()
            }
        }
    }
}
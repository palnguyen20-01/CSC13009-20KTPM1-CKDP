package com.example.csc13009_android_ckdp.HealthAdvice

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class GPTAPI {
    val client = OkHttpClient()
    fun sendMessage(queries:ArrayList<MessageModel>,callback: Callback){

        val url = "https://api.openai.com/v1/chat/completions"
        var queriesJSON= JSONArray()
        queries.forEach {
            //statement(s)
            queriesJSON=queriesJSON.put(
            JSONObject().put("role", it.sentBy).put("content", it.message)
            )
        }

        val json = JSONObject()
            .put("model","gpt-3.5-turbo")
            .put("messages",queriesJSON)
            .put("max_tokens", 120)
            .put("n", 1)
            .put("temperature", 0)


        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(callback)

    }
}
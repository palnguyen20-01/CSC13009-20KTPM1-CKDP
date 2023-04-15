package com.example.csc13009_android_ckdp.HealthAdvice

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
        queriesJSON.remove(queriesJSON.length() - 1)

        val lastElement = queriesJSON.getJSONObject(queriesJSON.length() - 1)

        val  addSuffix:String=lastElement.getString("content")+".Hãy trả lời ngắn gọn."
        // Update the content
        lastElement.put("content", addSuffix)


        val json = JSONObject()
            .put("model","text-davinci-003")
            .put("messages",queriesJSON)
            .put("max_tokens", 150)
            .put("n", 1)
            .put("temperature", 0.3)

        Log.d("SENDTOAPI", json.toString())

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(callback)

    }
    fun sendMessageByRapidAPI(queries:ArrayList<MessageModel>, callback: Callback) {

        var queriesJSON= JSONArray()
        queries.forEach {
            queriesJSON=queriesJSON.put(
                JSONObject().put("role", it.sentBy).put("content", it.message)
            )
        }
        queriesJSON.remove(queriesJSON.length() - 1)

//        val lastElement = queriesJSON.getJSONObject(queriesJSON.length() - 1)

//        val  addSuffix:String=lastElement.getString("content")+".Hãy trả lời ngắn gọn."
        // Update the content
//        lastElement.put("content", addSuffix)

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", queriesJSON)
            put("max_tokens", 220)
            put("n", 1)
            put("temperature", 0.3)
        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://openai80.p.rapidapi.com/chat/completions")
            .post(body)
            .addHeader("X-RapidAPI-Key", "b71d1f41camsh51cbf0a2df5facap1b9e25jsn2ce94a61f252")
            .addHeader("X-RapidAPI-Host", "openai80.p.rapidapi.com")
            .build()


        client.newCall(request).enqueue(callback)
    }
}
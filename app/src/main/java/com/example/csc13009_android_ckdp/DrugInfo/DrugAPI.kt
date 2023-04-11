package com.example.csc13009_android_ckdp.DrugInfo

import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
class DrugAPI {

    val client = OkHttpClient()
    fun searchDrug(drugName: String, callback: Callback) {
        val client = OkHttpClient()
        val url = "https://api.fda.gov/drug/label.json?search=brand_name=$drugName"+" OR "+"generic_name=$drugName"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(callback)
    }
    fun processJsonResponse(jsonResponse: JSONObject):  Triple<String, String, String> {

        Log.d("errrorr",jsonResponse.toString())
        if (jsonResponse.has("error")){
            val responeSTR:String =jsonResponse.getJSONObject("error").getString("message")
            return Triple(responeSTR, responeSTR, responeSTR)
        }
        val results = jsonResponse.getJSONArray("results")

        if (results.length() == 0) {
            val responeSTR : String ="Not Found Drug"
            return Triple(responeSTR, responeSTR, responeSTR)

        }

        var indications: String ="Not Found"
        var dosage: String ="Not Found"
        var interactions: String ="Not Found"


        val drug = results.getJSONObject(0)
        if (drug.has("indications_and_usage")){
            indications = drug.getJSONArray("indications_and_usage")[0] as String
        }
        if (drug.has("dosage_and_administration")){
            dosage = drug.getJSONArray("indications_and_usage")[0] as String
        }
        if (drug.has("drug_interactions")){
            interactions = drug.getJSONArray("indications_and_usage")[0] as String
        }


        return Triple(interactions, indications, dosage)
    }


}
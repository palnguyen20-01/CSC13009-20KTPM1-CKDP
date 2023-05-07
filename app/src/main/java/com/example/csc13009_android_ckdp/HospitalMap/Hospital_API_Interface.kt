package com.example.csc13009_android_ckdp.HospitalMap

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Hospital_API_Interface {
    @GET("nearby-search")
    fun getResouces(@Query(value = "key", encoded = true) key: String,
                    @Query(value = "location", encoded = true) location: String,
                    @Query(value = "radius", encoded = true) radius: String,
                    @Query(value = "text", encoded = true) text: String,
                    @Query(value = "types", encoded = true) types: String,
                    @Query(value = "tags", encoded = true) tags: String,
                    @Query(value = "datetime", encoded = true) datetime: String
    ): Call<ResponseBody>

    companion object{
        var BASE_URL = "https://api.map4d.vn/sdk/place/"

        fun create():Hospital_API_Interface{
            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()
            return retrofit.create(Hospital_API_Interface::class.java)
        }
    }
}
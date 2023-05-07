package com.example.csc13009_android_ckdp.HospitalMap

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Distance_API_Interface {
    @GET("matrix")
    fun getResouces(@Query(value = "key", encoded = true) key: String,
                    @Query(value = "origins", encoded = true) origins: String,
                    @Query(value = "destinations", encoded = true) destinations: String,
                    @Query(value = "mode", encoded = true) mode: String
    ): Call<ResponseBody>

    companion object{
        var BASE_URL = "https://api.map4d.vn/sdk/route/"

        fun create():Distance_API_Interface{
            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()
            return retrofit.create(Distance_API_Interface::class.java)
        }
    }
}
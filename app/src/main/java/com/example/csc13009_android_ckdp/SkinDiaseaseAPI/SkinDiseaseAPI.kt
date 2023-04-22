package com.example.csc13009_android_ckdp.SkinDiaseaseAPI

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.csc13009_android_ckdp.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File


class SkinDiseaseAPI {
    val client = OkHttpClient()
    fun SendtoAPI(image:ByteArray, callback: Callback) {
        // Đường dẫn tới file ảnh cần gửi

//        val file = File(imagePath)
//        val inputStream = file.inputStream()
//        val bytes = inputStream.readBytes()
//        Log.d("ResponeFromAPIDiseaseSkin", bytes.toString())


        Log.d("ResponeFromAPIDiseaseSkin", image.toString())

        val mediaType = "multipart/form-data; boundary=---011000010111000001101001".toMediaTypeOrNull()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", RequestBody.create("image/*".toMediaTypeOrNull(), image))
            .build()


        val request = Request.Builder()
            .url("https://detect-skin-disease.p.rapidapi.com/facebody/analysis/detect-skin-disease")
            .post(requestBody)
            .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
            .addHeader("X-RapidAPI-Key", "8a673e4186msh12af7b4e676a669p10f791jsndfd5ec06c895")

            .addHeader("X-RapidAPI-Host", "detect-skin-disease.p.rapidapi.com")
            .build()


        client.newCall(request).enqueue(callback)
    }

}
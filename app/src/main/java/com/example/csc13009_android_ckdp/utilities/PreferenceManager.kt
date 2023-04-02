package com.example.csc13009_android_ckdp.utilities

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {
    final lateinit var sharedPreferences: SharedPreferences
    constructor(context: Context){
        sharedPreferences = context.getSharedPreferences("CKDP_APP", Context.MODE_PRIVATE)
    }

    fun putBoolean(key: String, value: Boolean){
        var editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean{
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String, value: String){
        var editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun clear(){
        var editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
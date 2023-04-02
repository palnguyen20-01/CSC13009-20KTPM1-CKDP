package com.example.csc13009_android_ckdp.Alarm.Application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class App : Application(){
    companion object{
        val ID="com.example.alarm.ckdp"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createChanel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChanel() {
        val channel = NotificationChannel(ID,"Alarm Service",NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager=getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }
}
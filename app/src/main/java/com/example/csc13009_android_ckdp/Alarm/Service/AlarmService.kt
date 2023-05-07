package com.example.csc13009_android_ckdp.Alarm.Service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.csc13009_android_ckdp.Alarm.Application.App
import com.example.csc13009_android_ckdp.Alarm.RingAlarmActivity
import com.example.csc13009_android_ckdp.R

class AlarmService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val CONTENT=intent!!.getStringExtra("CONTENT")

        val intent=Intent(this, RingAlarmActivity::class.java)

intent.putExtra("CONTENT",CONTENT)
        intent.setAction("MESSAGE");
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val notification=NotificationCompat.Builder(this, App.ID)
            .setSmallIcon(R.drawable.ic_time)
            .setContentTitle("Time to take medicine")
            .setContentText("Take medicine")
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(CONTENT))
            .build()


        startForeground(1,notification)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
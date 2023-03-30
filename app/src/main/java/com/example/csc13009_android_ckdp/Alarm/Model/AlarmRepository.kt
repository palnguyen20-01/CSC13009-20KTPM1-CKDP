package com.example.csc13009_android_ckdp.Alarm.Model

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.Alarm.Model.AlarmDao
import com.example.csc13009_android_ckdp.Alarm.Model.AlarmDatabase

class AlarmRepository {
    private var alarmDao : AlarmDao
    var list : LiveData<List<Alarm>>

    constructor(application: Application) {
        this.alarmDao = AlarmDatabase.getInstance(application).alarmDao()
        this.list = alarmDao.getAll()
    }

    suspend fun insert(alarm: Alarm){
        alarmDao.insert(alarm)
    }

    suspend fun delete(alarm: Alarm){
        alarmDao.delete(alarm)
    }

    suspend fun update(alarm: Alarm){
        alarmDao.update(alarm)
    }
}
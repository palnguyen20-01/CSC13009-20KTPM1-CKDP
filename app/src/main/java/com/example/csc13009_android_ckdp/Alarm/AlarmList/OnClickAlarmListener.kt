package com.example.csc13009_android_ckdp.Alarm.AlarmList

import com.example.csc13009_android_ckdp.Alarm.Model.Alarm

interface OnClickAlarmListener {
    fun onClick(alarm: Alarm)

    fun onLongClick(alarm: Alarm)
}
package com.example.csc13009_android_ckdp.Alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.csc13009_android_ckdp.Alarm.Service.AlarmService
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.ActivityRingAlarmBinding

class RingAlarmActivity : AppCompatActivity() {
    lateinit var binding:ActivityRingAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRingAlarmBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.closeAlarmlBTN.setOnClickListener{
            val intent= Intent(this, AlarmService::class.java)
            applicationContext.stopService(intent)
            finish()
        }
    }
}
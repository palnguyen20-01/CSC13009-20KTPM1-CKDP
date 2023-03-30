package com.example.csc13009_android_ckdp.Profile

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.R

class InfoChange : AppCompatActivity() {
    lateinit var btnCancel : Button
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changeinfo)

        btnCancel = findViewById(R.id.btnCancelChangeProfile)
        btnCancel.setOnClickListener {
            finish()
        }
    }
}
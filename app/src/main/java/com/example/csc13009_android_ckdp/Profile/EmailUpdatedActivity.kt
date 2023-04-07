package com.example.csc13009_android_ckdp.Profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.LoginActivity
import com.example.csc13009_android_ckdp.R


class EmailUpdatedActivity : AppCompatActivity() {
    lateinit var btnNext : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_updated)

        btnNext = findViewById(R.id.btnLoginEmailUpdated)

        btnNext.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }
}
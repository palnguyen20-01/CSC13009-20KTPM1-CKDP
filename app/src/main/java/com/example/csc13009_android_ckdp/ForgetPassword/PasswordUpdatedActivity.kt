package com.example.csc13009_android_ckdp.ForgetPassword

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.LoginActivity
import com.example.csc13009_android_ckdp.R
import com.google.firebase.auth.FirebaseAuth

class PasswordUpdatedActivity : AppCompatActivity() {
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_updated)
        btnNext = findViewById(R.id.btnLoginPassUpdated)
        btnNext.setOnClickListener {

            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

    }
}
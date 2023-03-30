package com.example.csc13009_android_ckdp.Profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.csc13009_android_ckdp.HomeFragment
import com.example.csc13009_android_ckdp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class PasswordChange : AppCompatActivity() {
    lateinit var btnCancel : Button
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        btnCancel = findViewById(R.id.btnCancelChangePass)
        btnCancel.setOnClickListener {
            finish()
        }
    }
}
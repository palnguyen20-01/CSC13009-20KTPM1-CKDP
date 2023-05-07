package com.example.csc13009_android_ckdp.HospitalMap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.csc13009_android_ckdp.R

class HospitalMapMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_map_menu)

        val hospitalMapButton : Button = findViewById(R.id.hospitalMapButton)
        val hospitalListButton : Button = findViewById(R.id.hospitalListButton)

        hospitalMapButton.setOnClickListener {
            val intent = Intent(this, HospitalMapActivity::class.java)
            intent.putExtra("type","none")
            this.startActivity(intent);
        }

        hospitalListButton.setOnClickListener {
            val intent = Intent(this, HospitalListActivity::class.java)
            this.startActivity(intent);
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.title_activity_hospital_map)
    }

}
package com.example.csc13009_android_ckdp.FirstAid

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.R

class FirstAidInfo: AppCompatActivity() {
    lateinit var iconImageView : ImageView
    lateinit var titleTextView: TextView
    lateinit var contentTextView: TextView

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        androidx.appcompat.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid_info)
        val intent = intent
        val name = intent.getStringExtra("name")!!.toInt()
        val image = intent.getStringExtra("image")!!.toInt()
        val code = intent.getStringExtra("code")!!.toInt()

        iconImageView = findViewById(R.id.iconImageView)
        iconImageView.setImageResource(image)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.setText(getString(name))

        contentTextView = findViewById(R.id.contentTextView)
        contentTextView.setText(code)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
package com.example.csc13009_android_ckdp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.csc13009_android_ckdp.Profile.InfoChange
import com.example.csc13009_android_ckdp.Profile.PasswordChange
import java.util.*

class SettingFragment : Fragment() {

    private var main: MainActivity? = null

    lateinit var imageProfile : ImageView
    lateinit var card_pass : CardView
    lateinit var card_info : CardView
    lateinit var card_email : CardView
    lateinit var card_phone : CardView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = try {
            activity as MainActivity?
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MainActivity must implement callbacks")
        }
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        (activity as MainActivity).supportActionBar!!.setDisplayUseLogoEnabled(true)
        (activity as MainActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TEST", "onActivityResult: $requestCode");
        if(requestCode == 1){
            imageProfile = main!!.findViewById(R.id.imageProfile)

            var encodedImage = data?.getStringExtra("image")
            if (encodedImage != null) {
                Log.d("TEST", encodedImage)
            };
            if(encodedImage != null) {
                var bytes = Base64.getDecoder().decode(encodedImage)
                var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageProfile?.setImageBitmap(bitmap)

            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingsFragment: View = inflater.inflate(R.layout.fragment_setting, container, false)
        var lin =  settingsFragment.findViewById<ScrollView>(R.id.profileScrollView);

        card_pass = settingsFragment.findViewById<CardView>(R.id.card_password);
        card_info = settingsFragment.findViewById<CardView>(R.id.card_info);
        card_email = settingsFragment.findViewById<CardView>(R.id.card_email);
        card_phone = settingsFragment.findViewById<CardView>(R.id.card_phone);


        card_pass.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 0)
        }
        card_info.setOnClickListener {
            val intent = Intent(context, InfoChange::class.java)
            startActivityForResult(intent, 1)
        }
        card_email.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 2)
        }
        card_phone.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 3)
        }

        return settingsFragment
    }


}
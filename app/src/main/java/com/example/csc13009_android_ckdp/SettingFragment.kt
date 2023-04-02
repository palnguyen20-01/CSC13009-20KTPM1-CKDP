package com.example.csc13009_android_ckdp

import android.content.Intent
import android.content.SharedPreferences
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
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.firebase.ui.auth.AuthUI
import java.util.*

class SettingFragment : Fragment() {

    private var main: MainActivity? = null

    lateinit var imageProfile : ImageView
    lateinit var cardPass : CardView
    lateinit var cardInfo : CardView
    lateinit var cardEmail : CardView
    lateinit var cardPhone : CardView
    lateinit var cardLogout : CardView
    lateinit var preferenceManager: PreferenceManager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = try {
            activity as MainActivity?
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MainActivity must implement callbacks")
        }

        preferenceManager = PreferenceManager(requireContext())

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

        cardPass = settingsFragment.findViewById<CardView>(R.id.card_password);
        cardInfo = settingsFragment.findViewById<CardView>(R.id.card_info);
        cardEmail = settingsFragment.findViewById<CardView>(R.id.card_email);
        cardPhone = settingsFragment.findViewById<CardView>(R.id.card_phone);
        cardLogout = settingsFragment.findViewById<CardView>(R.id.card_logout);


        cardPass.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 0)
        }
        cardInfo.setOnClickListener {
            val intent = Intent(context, InfoChange::class.java)
            startActivityForResult(intent, 1)
        }
        cardEmail.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 2)
        }
        cardPhone.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 3)
        }

        cardLogout.setOnClickListener {
            preferenceManager.clear()
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    print("addOnCompleteListener")
                }
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return settingsFragment
    }


}
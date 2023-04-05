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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.csc13009_android_ckdp.Profile.InfoChange
import com.example.csc13009_android_ckdp.Profile.PasswordChange
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class SettingFragment : Fragment {

    private var main: MainActivity? = null

    lateinit var imageProfile : ImageView
    lateinit var txtProfileName: TextView
    lateinit var txtProfileEmail: TextView
    lateinit var cardPass : CardView
    lateinit var cardInfo : CardView
    lateinit var cardEmail : CardView
    lateinit var cardPhone : CardView
    lateinit var cardLogout : CardView
    lateinit var preferenceManager: PreferenceManager

    var user: FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    constructor(main: MainActivity?) : super() {
        this.main = main
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = try {
            activity as MainActivity?
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MainActivity must implement callbacks")
        }

        user = FirebaseAuth.getInstance().currentUser
        auth = FirebaseAuth.getInstance()
        preferenceManager = PreferenceManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TEST", "onActivityResult: $requestCode");
        if(requestCode == 5201){
            txtProfileName.text = data?.getStringExtra("name")
            txtProfileEmail.text = data?.getStringExtra("email")
            var encodedImage = data?.getStringExtra("image")
            Log.d("Image profiel", encodedImage.toString())
            if(encodedImage != null) {
                var bytes = Base64.getDecoder().decode(encodedImage)
                var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageProfile.setImageBitmap(bitmap)
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val settingsFragment: View = inflater.inflate(R.layout.fragment_setting, container, false)

        cardPass = settingsFragment.findViewById<CardView>(R.id.card_password)
        cardInfo = settingsFragment.findViewById<CardView>(R.id.card_info)
        cardEmail = settingsFragment.findViewById<CardView>(R.id.card_email)
        cardPhone = settingsFragment.findViewById<CardView>(R.id.card_phone)
        cardLogout = settingsFragment.findViewById<CardView>(R.id.card_logout)
        txtProfileEmail = settingsFragment.findViewById<TextView>(R.id.txtProfileEmail)
        txtProfileName = settingsFragment.findViewById<TextView>(R.id.txtProfileName)
        imageProfile = settingsFragment.findViewById<ImageView>(R.id.imageProfile)
        val authUser = auth.currentUser

        if(authUser == null){
            showToast("No profile found")
        }
        else{
            val name = authUser.displayName
            val email = authUser.email
            val photoUrl = authUser.photoUrl
            txtProfileEmail.text = email
            txtProfileName.text = name

//            Glide
//                .with(this)
//                .load(photoUrl)
//                .placeholder(R.drawable.user_avatar)
//                .into(imageProfile)

        }

// Name, email address, and profile photo Url


        cardPass.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, RequestCodeResult.CHANGE_PASSWORD)
        }
        cardInfo.setOnClickListener {
            val intent = Intent(context, InfoChange::class.java)
            startActivityForResult(intent, RequestCodeResult.CHANGE_INFORMATION)
        }
        cardEmail.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, RequestCodeResult.CHANGE_EMAIL)
        }
        cardPhone.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivityForResult(intent, 5203)
        }

        cardLogout.setOnClickListener {
            preferenceManager.clear()
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return settingsFragment
    }

    private fun showToast(message: String){
        Toast.makeText(main, message, Toast.LENGTH_SHORT).show()
    }
}
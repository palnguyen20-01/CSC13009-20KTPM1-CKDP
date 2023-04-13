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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.csc13009_android_ckdp.Profile.EmailChange
import com.example.csc13009_android_ckdp.Profile.InfoChange
import com.example.csc13009_android_ckdp.Profile.PasswordChange
import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class SettingFragment : Fragment() {

    public var main: MainActivity? = null

    lateinit var imageProfile : ImageView
    lateinit var txtProfileName: TextView
    lateinit var txtProfileEmail: TextView
    lateinit var cardPass : CardView
    lateinit var cardInfo : CardView
    lateinit var cardEmail : CardView
    //lateinit var cardPhone : CardView
    lateinit var cardLogout : CardView
    var isChange : Boolean = false
    var profileName: String? = null
    var profileEmail: String? = null
    var profileImage: ByteArray? = null
    var user: FirebaseUser? = null
    lateinit var auth: FirebaseAuth
//    constructor(main: MainActivity?) : super() {
//        this.main = main
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        main = try {
            activity as MainActivity?
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MainActivity must implement callbacks")
        }

        auth = FirebaseAuth.getInstance()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TEST", "onActivityResult: $requestCode");
        if(requestCode == 5201){
            imageProfile = main!!.findViewById(R.id.imageProfile)

            isChange = true
            profileName = data?.getStringExtra("name")

            var encodedImage = data?.getStringExtra("image")
            if(encodedImage != null) {
                var bytes = Base64.getDecoder().decode(encodedImage)
                var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageProfile.setImageBitmap(bitmap)

            }
        }
        else if(requestCode == 5200){

        }
        initData()
    }

    private fun initData(){
        val authUser = auth.currentUser

        if(authUser == null){
            showToast("No profile found")
        }
        else{
            if(isChange) {
                txtProfileName.text = profileName

            }
            else{
                profileName = authUser.displayName
                profileEmail = authUser.email
                val photoUrl = authUser.photoUrl
                txtProfileEmail.text = profileEmail
                txtProfileName.text = profileName

                GlideApp.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.user_avatar)
                    .into(imageProfile)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val settingsFragment: View = inflater.inflate(R.layout.fragment_setting, container, false)

        cardPass = settingsFragment.findViewById<CardView>(R.id.card_password)
        cardInfo = settingsFragment.findViewById<CardView>(R.id.card_info)
        cardEmail = settingsFragment.findViewById<CardView>(R.id.card_email)
        //cardPhone = settingsFragment.findViewById<CardView>(R.id.card_phone)
        cardLogout = settingsFragment.findViewById<CardView>(R.id.card_logout)
        txtProfileEmail = settingsFragment.findViewById<TextView>(R.id.txtProfileEmail)
        txtProfileName = settingsFragment.findViewById<TextView>(R.id.txtProfileName)
        imageProfile = settingsFragment.findViewById<ImageView>(R.id.imageProfile)

        initData()

        cardPass.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivity(intent)
        }
        cardInfo.setOnClickListener {
            val intent = Intent(context, InfoChange::class.java)
            startActivityForResult(intent, RequestCodeResult.CHANGE_INFORMATION)
        }
        cardEmail.setOnClickListener {
            val intent = Intent(context, EmailChange::class.java)
            startActivity(intent)
        }

        cardLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
        return settingsFragment
    }

    private fun showToast(message: String){
        Toast.makeText(main, message, Toast.LENGTH_SHORT).show()
    }
}
package com.example.csc13009_android_ckdp

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    lateinit var informationText: TextView
    lateinit var aboutUsText: TextView
    lateinit var txtPrivacyItem1: TextView
    lateinit var privacyText: TextView
    lateinit var phoneAboutUsText: TextView
    lateinit var emailAboutUsText: TextView
    lateinit var fbImg: ImageView
    lateinit var ytbImg: ImageView
    lateinit var cardPass : CardView
    lateinit var cardInfo : CardView
    lateinit var cardEmail : CardView
    lateinit var profileScrollView : ScrollView
    lateinit var privacyScrollView : ScrollView
    lateinit var aboutUsScrollView : ScrollView
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
        informationText = settingsFragment.findViewById<TextView>(R.id.informationTextProfile)
        aboutUsText = settingsFragment.findViewById<TextView>(R.id.aboutusTextProfile)
        privacyText = settingsFragment.findViewById<TextView>(R.id.privacyTextProfile)
        txtPrivacyItem1 = settingsFragment.findViewById<TextView>(R.id.txtPrivacyItem1)
        phoneAboutUsText = settingsFragment.findViewById<TextView>(R.id.phone_call)
        emailAboutUsText = settingsFragment.findViewById<TextView>(R.id.email_aboutus)
        //cardPhone = settingsFragment.findViewById<CardView>(R.id.card_phone)
        cardLogout = settingsFragment.findViewById<CardView>(R.id.card_logout)
        txtProfileEmail = settingsFragment.findViewById<TextView>(R.id.txtProfileEmail)
        txtProfileName = settingsFragment.findViewById<TextView>(R.id.txtProfileName)
        imageProfile = settingsFragment.findViewById<ImageView>(R.id.imageProfile)
        fbImg = settingsFragment.findViewById<ImageView>(R.id.facebook)
        ytbImg = settingsFragment.findViewById<ImageView>(R.id.youtube)
        profileScrollView = settingsFragment.findViewById<ScrollView>(R.id.profileScrollView)
        privacyScrollView = settingsFragment.findViewById<ScrollView>(R.id.privacyScrollView)
        aboutUsScrollView = settingsFragment.findViewById<ScrollView>(R.id.aboutUsScrollView)
        privacyScrollView.visibility = View.GONE
        aboutUsScrollView.visibility = View.GONE
        initData()

        informationText.setTypeface(null, Typeface.BOLD)
        privacyText.setTypeface(null, Typeface.NORMAL)
        aboutUsText.setTypeface(null, Typeface.NORMAL)

        informationText.setOnClickListener {
            profileScrollView.visibility = View.VISIBLE
            privacyScrollView.visibility = View.GONE
            aboutUsScrollView.visibility = View.GONE
            updateBoldText(1)
        }
        privacyText.setOnClickListener {
            profileScrollView.visibility = View.GONE
            privacyScrollView.visibility = View.VISIBLE
            aboutUsScrollView.visibility = View.GONE
            updateBoldText(2)
        }
        aboutUsText.setOnClickListener {
            profileScrollView.visibility = View.GONE
            privacyScrollView.visibility = View.GONE
            aboutUsScrollView.visibility = View.VISIBLE
            updateBoldText(3)
        }
        //txtPrivacyItem1.setMovementMethod(LinkMovementMethod.getInstance());
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
        phoneAboutUsText.setOnClickListener {

            val mIntent = Intent(Intent.ACTION_CALL)
            mIntent.data = Uri.parse("tel:+84 903612640")
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    main!!, arrayOf(CALL_PHONE),
                    RequestCodeResult.MY_PERMISSIONS_REQUEST_CALL_PHONE
                )

            } else {
                try {
                    startActivity(mIntent)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
        emailAboutUsText.setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:" + Uri.encode("ktphanmem20@gmail.com") + "?subject=" +
                    Uri.encode(getString(R.string.app_name)) + "&body=" + Uri.encode("")

            val uri = Uri.parse(uriText)
            intent.data = uri
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
        fbImg.setOnClickListener {
            var uri = Uri.parse("https://www.facebook.com/z.phuc4570");
            startActivity(Intent(Intent.ACTION_VIEW,uri))
        }
        ytbImg.setOnClickListener {
            var uri = Uri.parse("https://www.youtube.com/@PhucNguyen-jc3iw");
            startActivity(Intent(Intent.ACTION_VIEW,uri))
        }
        return settingsFragment
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestCodeResult.MY_PERMISSIONS_REQUEST_CALL_PHONE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the phone call
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }
    private fun updateBoldText(n: Int){
        when (n) {
            1 -> {
                informationText.setTypeface(null, Typeface.BOLD)
                privacyText.setTypeface(null, Typeface.NORMAL)
                aboutUsText.setTypeface(null, Typeface.NORMAL)
            }
            2 -> {
                informationText.setTypeface(null, Typeface.NORMAL)
                privacyText.setTypeface(null, Typeface.BOLD)
                aboutUsText.setTypeface(null, Typeface.NORMAL)
            }
            3 -> {
                informationText.setTypeface(null, Typeface.NORMAL)
                privacyText.setTypeface(null, Typeface.NORMAL)
                aboutUsText.setTypeface(null, Typeface.BOLD)
            }
        }
    }
    private fun showToast(message: String){
        Toast.makeText(main, message, Toast.LENGTH_SHORT).show()
    }
}
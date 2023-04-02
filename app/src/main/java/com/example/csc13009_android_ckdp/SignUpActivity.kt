package com.example.csc13009_android_ckdp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import java.io.ByteArrayOutputStream
import java.util.*


class SignUpActivity : AppCompatActivity() {
    lateinit var btnSignUp: Button
    lateinit var textEmail: EditText
    lateinit var textName: EditText
    lateinit var textPassword: EditText
    lateinit var textConfirm: EditText
    lateinit var progressBtn: ProgressBar
    lateinit var preferenceManager: PreferenceManager
    lateinit var auth : FirebaseAuth
    var encodedImage : String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignUp = findViewById(R.id.btnSignUp)
        textEmail = findViewById(R.id.textSignUpEmail)
        textName = findViewById(R.id.textSignUpName)
        textPassword = findViewById(R.id.textSignUpPassword)
        textConfirm = findViewById(R.id.textSignUpConfirmPassword)
        progressBtn = findViewById(R.id.progressSignUpButton)
        preferenceManager = PreferenceManager(applicationContext)
        auth = FirebaseAuth.getInstance()
        btnSignUp.setOnClickListener {
            if(isValidSignUp()){
                signUp()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bitmap : Bitmap) : String{
        var previewWidth = 150
        var previewHeight = bitmap.height * previewWidth / bitmap.width
        var previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        var byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        var bytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUp() {
        loading(true)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user)
        encodedImage = encodeImage(bitmap)

        var database = FirebaseFirestore.getInstance()
        var user = HashMap<String, String>()
        user["email"] = textEmail.text.toString()
        user["name"] = textName.text.toString()
        user["password"] = textPassword.text.toString()
        user["image"] = encodedImage


        auth.createUserWithEmailAndPassword(textEmail.text.toString(), textPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Sign up", "createUserWithEmail:success")
                    val userAuth = auth.currentUser
                    database.collection("users").add(user)
                        .addOnSuccessListener {documentReference ->
                            loading(false)
                            preferenceManager.putBoolean("isLogin", true)
                            preferenceManager.putString("id", documentReference.id)
                            preferenceManager.putString("email", textEmail.text.toString())
                            preferenceManager.putString("name", textName.text.toString())
                            preferenceManager.putString("image", encodedImage)
                            startActivity(Intent(applicationContext, MainActivity::class.java))

                        }
                        .addOnFailureListener{exception ->
                            loading(false)
                            exception.message?.let { showToast(it) }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Sign up", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }


    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignUp(): Boolean {
        if(textEmail.text.toString().trim().isEmpty())
        {
            showToast("Enter your Email")
            return false
        }
        else if(textName.text.toString().trim().isEmpty())
        {
            showToast("Enter your Email")
            return false
        }
        else if(textPassword.text.toString().trim().isEmpty())
        {
            showToast("Enter your Email")
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail.text.toString()).matches())
        {
            showToast("Email is invalid")
            return false
        }
        else if(textConfirm.text.toString().trim().isEmpty())
        {
            showToast("Confirm your password")
            return false
        }
        else if(textPassword.text.toString().equals(textConfirm.text.toString()))
        {
            showToast("Password & confirm password must be same")
            return false
        }
        else return true
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            btnSignUp.visibility = View.INVISIBLE
            progressBtn.visibility = View.VISIBLE
        }
        else{
            btnSignUp.visibility = View.VISIBLE
            progressBtn.visibility = View.INVISIBLE
        }
    }
}
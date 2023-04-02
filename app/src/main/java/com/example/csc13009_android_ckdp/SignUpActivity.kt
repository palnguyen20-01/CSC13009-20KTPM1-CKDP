package com.example.csc13009_android_ckdp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    lateinit var textEmail: EditText
    lateinit var textName: EditText
    lateinit var textPassword: EditText
    lateinit var textConfirm: EditText
    lateinit var progressBtn: ProgressBar
    lateinit var preferenceManager: PreferenceManager

    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignIn = findViewById(R.id.btnSignUp)
        textEmail = findViewById(R.id.textSignUpEmail)
        textName = findViewById(R.id.textSignUpName)
        textPassword = findViewById(R.id.textSignUpPassword)
        textConfirm = findViewById(R.id.textSignUpConfirmPassword)
        progressBtn = findViewById(R.id.progressSignUpButton)
        preferenceManager = PreferenceManager(applicationContext)

        btnSignIn.setOnClickListener {
            if(isValidSignUp()){
                signUp()
            }
        }
    }

    private fun signUp() {
        loading(true)
        var database = FirebaseFirestore.getInstance()
        var user = HashMap<String, String>()
        user["email"] = textEmail.text.toString()
        user["name"] = textName.text.toString()
        user["password"] = textPassword.text.toString()
        database.collection("users").add(user)
            .addOnSuccessListener {documentReference ->
                loading(false)
                preferenceManager.putBoolean("isLogin", true)
                preferenceManager.putString("id", documentReference.id)
                preferenceManager.putString("email", textEmail.text.toString())
                preferenceManager.putString("name", textName.text.toString())
                startActivity(Intent(applicationContext, MainActivity::class.java))

            }
            .addOnFailureListener{exception ->
                loading(false)
                exception.message?.let { showToast(it) }
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
            btnSignIn.visibility = View.INVISIBLE
            progressBtn.visibility = View.VISIBLE
        }
        else{
            btnSignIn.visibility = View.VISIBLE
            progressBtn.visibility = View.INVISIBLE
        }
    }
}
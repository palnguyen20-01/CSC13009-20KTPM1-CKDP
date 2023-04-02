package com.example.csc13009_android_ckdp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.FirstAid.FirstAidActivity
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    lateinit var progressBtn: ProgressBar
    lateinit var textEmail: TextView
    lateinit var textPass: TextView
    lateinit var textSignUp: TextView
    lateinit var preferenceManager: PreferenceManager
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnSignIn = findViewById(R.id.btnLogin)
        textEmail = findViewById(R.id.textLoginEmail)
        textPass = findViewById(R.id.textLoginPassword)
        textSignUp = findViewById(R.id.txtLoginSignUp)
        progressBtn = findViewById(R.id.progressSignInButton)
        preferenceManager = PreferenceManager(applicationContext)

        if(preferenceManager.getBoolean("isLogin")){
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        textSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        btnSignIn.setOnClickListener {
            if(isValidSignIn()) {
                login()
            }
        }
    }

    private fun login() {
        loading(true)
        var database = FirebaseFirestore.getInstance()
        database.collection("users")
            .whereEqualTo("email", textEmail.text.toString())
            .whereEqualTo("password", textPass.text.toString())
            .get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful && task.result != null && task.result.documents.size > 0){
                    val documentSnapshot = task.result.documents[0]
                    preferenceManager.putBoolean("isLogin", true)
                    preferenceManager.putString("id", documentSnapshot.id)
                    preferenceManager.putString("email", documentSnapshot.getString("email")!!)
                    preferenceManager.putString("name", documentSnapshot.getString("name")!!)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
                else
                {
                    loading(false)
                    showToast("Unable to log in")
                }
            }
            .addOnFailureListener{exception ->
                loading(false)
                exception.message?.let { showToast(it) }
            }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignIn(): Boolean {
        if(textEmail.text.toString().trim().isEmpty())
        {
            showToast("Enter your email")
            return false
        }
        else if(textPass.text.toString().trim().isEmpty())
        {
            showToast("Enter your password")
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
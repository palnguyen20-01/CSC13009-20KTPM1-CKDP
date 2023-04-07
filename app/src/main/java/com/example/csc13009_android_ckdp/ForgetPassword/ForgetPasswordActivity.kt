package com.example.csc13009_android_ckdp.ForgetPassword

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.R
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var textForgetPass: EditText
    lateinit var btnNext: Button
    lateinit var auth: FirebaseAuth
    lateinit var btnBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        textForgetPass = findViewById(R.id.textForgetPassword)
        btnNext = findViewById(R.id.btnNextForgetPass)
        btnBack = findViewById(R.id.btnBackForgetPassword)
        auth = FirebaseAuth.getInstance()


        btnNext.setOnClickListener {
            if(isValidForget()){
                sendResetPass()
            }
        }
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendResetPass() {
        auth.sendPasswordResetEmail(textForgetPass.text.toString()).addOnCompleteListener {
            task ->
                if(task.isSuccessful) {
                    showToast("Check your email")
                    startActivity(Intent(applicationContext, PasswordUpdatedActivity::class.java))
                }
            }
            .addOnFailureListener {
                showToast("Unable to send, failed!")
                finish()
            }
    }


    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidForget(): Boolean {
        if(textForgetPass.text.toString().trim().isEmpty())
        {
            showToast("Enter your Email")
            return false
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(textForgetPass.text.toString()).matches())
        {
            showToast("Email is invalid")
            return false
        }
        else return true
    }
}
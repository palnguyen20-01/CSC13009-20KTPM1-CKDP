package com.example.csc13009_android_ckdp.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.ForgetPassword.PasswordUpdatedActivity
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.SettingFragment
import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser

class EmailChange : AppCompatActivity() {
    lateinit var btnCancel : Button
    lateinit var btnSave : Button
    lateinit var btnAuth : Button
    lateinit var oldPass: EditText
    lateinit var newEmail: EditText

    lateinit var user: FirebaseUser
    var credential: AuthCredential? = null
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        btnCancel = findViewById(R.id.btnCancelChangeEmail)
        btnSave = findViewById(R.id.btnChangeEmail)
        btnAuth = findViewById(R.id.btnAuthenticateEmail)
        newEmail = findViewById(R.id.newEmailChangeEmail)
        oldPass = findViewById(R.id.currentPasswordChangeEmail)

        newEmail.isEnabled = false
        btnSave.isEnabled = false

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        if(user == null){
            showToast("User is not available")
            val intent = Intent(this, SettingFragment::class.java)
            setResult(RequestCodeResult.CHANGE_EMAIL, intent);
            finish()
        }
        else{
            reAuthenticateUser(user)
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun reAuthenticateUser(user: FirebaseUser) {
        btnAuth.setOnClickListener {
            var curPass = oldPass.text.toString()
            if(curPass.equals("")){
                showToast("Please enter your current password")
            }
            else{
                credential = EmailAuthProvider.getCredential(user.email!!,curPass)
                user.reauthenticate(credential!!)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful)
                        {
                            newEmail.isEnabled = true
                            btnSave.isEnabled = true
                            changeEmail()
                        }
                        else if(task.exception is FirebaseAuthInvalidCredentialsException){
                            oldPass.error = "Invalid password"
                            oldPass.requestFocus()
                        }

                    }
            }
        }
    }

    private fun changeEmail() {
        btnSave.setOnClickListener {
            if(!Patterns.EMAIL_ADDRESS.matcher(newEmail.text.toString()).matches()){
                showToast("Your new email is invalid")
            }
            else{
                user.updateEmail(newEmail.text.toString())
                    .addOnCompleteListener{
                        auth.signOut()
                        val intent = Intent(this, EmailUpdatedActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "Can not update Email");
                    }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
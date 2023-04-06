package com.example.csc13009_android_ckdp.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseUser

class EmailChange : AppCompatActivity() {
    lateinit var btnCancel : Button
    lateinit var btnSave : Button
    lateinit var btnAuth : Button
    lateinit var oldPass: EditText
    lateinit var newPass: EditText
    lateinit var confirmPass: EditText

    lateinit var user: FirebaseUser
    var credential: AuthCredential? = null
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        btnCancel = findViewById(R.id.btnCancelChangePass)
        btnSave = findViewById(R.id.btnChangePass)
        btnAuth = findViewById(R.id.btnAuthenticate)

        oldPass = findViewById(R.id.oldPasswordChangePassword)
        newPass = findViewById(R.id.newPasswordChangePassword)
        confirmPass = findViewById(R.id.confirmPasswordChangePassword)

        newPass.isEnabled = false
        confirmPass.isEnabled = false
        btnSave.isEnabled = false

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        if(user == null){
            showToast("User is not available")
            val intent = Intent(this, SettingFragment::class.java)
            setResult(RequestCodeResult.CHANGE_INFORMATION, intent);
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
                    .addOnCompleteListener {
                        newPass.isEnabled = true
                        confirmPass.isEnabled = true
                        btnSave.isEnabled = true
                        changePassword()

                    }
                    .addOnFailureListener{
                        Log.d("TAG", "Authenticate failed")
                    }
            }
        }
    }

    private fun changePassword() {
        btnSave.setOnClickListener {
            if(newPass.text.toString().length < 6){
                showToast("Your password must be longer than 6")
            }
            else{
                user.updateEmail(newPass.text.toString())
                    .addOnCompleteListener{
                        auth.signOut()
                        val intent = Intent(this, PasswordUpdatedActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "Can not update Password");
                    }
            }

        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
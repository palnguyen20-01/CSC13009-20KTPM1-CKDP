package com.example.csc13009_android_ckdp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.Models.Users
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import java.util.*

class FacebookAuthActivity: LoginActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile"))
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("FB", "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("FB", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("FB", "facebook:onError", error)
                }
            })

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    val userData = Users(user!!.email!!,user.displayName!!, user.photoUrl!!.toString(), user.uid)

                    database.reference.child("Users").child(user.uid).setValue(userData)
                        .addOnSuccessListener {
                            Log.d("firebase", "Login by Facebook success")
                        }.addOnFailureListener{
                            Log.d("firebase", "Error getting data")
                        }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null)
                }

            }
    }

    private fun updateUI(account: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
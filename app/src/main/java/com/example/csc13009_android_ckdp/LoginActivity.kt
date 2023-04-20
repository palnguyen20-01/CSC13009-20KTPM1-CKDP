package com.example.csc13009_android_ckdp

import android.content.Intent
import android.media.FaceDetector.Face
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.ForgetPassword.ForgetPasswordActivity
import com.example.csc13009_android_ckdp.Models.Users
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.util.*


open class LoginActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    lateinit var btnSignInGG: Button
    lateinit var btnSignInFb: Button
    lateinit var progressBtn: ProgressBar
    lateinit var textEmail: TextView
    lateinit var textPass: TextView
    lateinit var textSignUp: TextView
    lateinit var txtForgotPass: TextView

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var gsc : GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 3445
    private val REQ_FORGET_PASSWORD = 3440
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnSignIn = findViewById(R.id.btnLogin)
        btnSignInGG = findViewById(R.id.btnSignInGG)
        btnSignInFb = findViewById(R.id.btnSignInFb)
        textEmail = findViewById(R.id.textLoginEmail)
        textPass = findViewById(R.id.textLoginPassword)
        textSignUp = findViewById(R.id.txtLoginSignUp)
        progressBtn = findViewById(R.id.progressSignInButton)

        txtForgotPass = findViewById(R.id.txtLoginForgotPassword)
        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.my_web_client_id))
            .requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)

        textSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        btnSignIn.setOnClickListener {
            if(isValidSignIn()) {
                login()
            }
        }

        txtForgotPass.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))

        }

        btnSignInGG.setOnClickListener {
            loginGG()

        }
        btnSignInFb.setOnClickListener {
            startActivity(Intent(this, FacebookAuthActivity::class.java))

        }
        //FacebookSdk.sdkInitialize(applicationContext)

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

    private fun loginGG() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 1789)
//        signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.my_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(true)
//            .build()
//
//        oneTapClient = Identity.getSignInClient(this)
//        oneTapClient.beginSignIn(signInRequest)
//            .addOnSuccessListener { result ->
//                try {
//                    startIntentSenderForResult(
//                        result.pendingIntent.intentSender,
//                        REQ_ONE_TAP, null, 0, 0, 0)
//                } catch (e: IntentSender.SendIntentException) {
//                    showToast( e.localizedMessage)
//                }
//            }
//            .addOnFailureListener {
//                showToast( it.localizedMessage)
//            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                    // Use ID token from Google to authenticate with Firebase.
                            val fbCredential =
                                GoogleAuthProvider.getCredential(idToken, null)

                            signInWithCredential(fbCredential)
                        }
                        else -> {
                            showToast("\nNo ID token!") // Shouldn't happen.
                        }
                    }
                } catch (e: ApiException) {
                    showToast("\n${e.message.toString()}")
                }
            }
            1789 -> {
                val accountTask: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    var accountGG = accountTask.getResult(ApiException::class.java)
                    var credential = GoogleAuthProvider.getCredential(accountGG.idToken, null)
                    signInWithCredential(credential)
                } catch (e: ApiException) {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    private fun signInWithCredential(firebaseCredential: AuthCredential){
        loadingGG(true)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success
                    // update UI with the signed-in user's information
                    val user = auth.currentUser

                    val userData = Users(user!!.email!!,user.displayName!!, user.photoUrl!!.toString(), user.uid)

                    database.reference.child("Users").child(user.uid).setValue(userData)
                        .addOnSuccessListener {
                            Log.d("firebase", "Login by GG success")
                        }.addOnFailureListener{
                            Log.d("firebase", "Error getting data")
                        }
                    updateUI(user)
                } else {
                    loadingGG(false)
                    // If sign in fails, display a message to the user.
                    showToast("\n${task.exception.toString()}")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(account: FirebaseUser?) {
        if(account == null){
            showToast("Fail")
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        loading(true)

        auth.signInWithEmailAndPassword(textEmail.text.toString(),textPass.text.toString())
            .addOnCompleteListener {task ->
                if(task.isSuccessful)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                else{
                    loading(false)
                    showToast("Unable to log in")
                }
            }
            .addOnFailureListener {
                loading(false)
                showToast("Unable to log in")
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

    private fun loadingGG(isLoading: Boolean){
        if(isLoading){
            btnSignInGG.visibility = View.INVISIBLE
            progressBtn.visibility = View.VISIBLE
        }
        else{
            btnSignInGG.visibility = View.VISIBLE
            progressBtn.visibility = View.INVISIBLE
        }
    }
}
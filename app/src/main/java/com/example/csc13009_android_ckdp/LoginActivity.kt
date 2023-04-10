package com.example.csc13009_android_ckdp

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.ForgetPassword.ForgetPasswordActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    lateinit var btnSignInGG: Button
    lateinit var progressBtn: ProgressBar
    lateinit var textEmail: TextView
    lateinit var textPass: TextView
    lateinit var textSignUp: TextView
    lateinit var txtForgotPass: TextView


    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
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
        textEmail = findViewById(R.id.textLoginEmail)
        textPass = findViewById(R.id.textLoginPassword)
        textSignUp = findViewById(R.id.txtLoginSignUp)
        progressBtn = findViewById(R.id.progressSignInButton)

        txtForgotPass = findViewById(R.id.txtLoginForgotPassword)
        auth = FirebaseAuth.getInstance()



        textSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        btnSignIn.setOnClickListener {
            if(isValidSignIn()) {
                login()
            }
        }


4
        txtForgotPass.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))

        }

        btnSignInGG.setOnClickListener {
            loginGG()

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun loginGG() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.my_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient = Identity.getSignInClient(this)
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        REQ_ONE_TAP, null, 0, 0, 0)
                } catch (e: IntentSender.SendIntentException) {
                    showToast( e.localizedMessage)
                }
            }
            .addOnFailureListener {
                showToast( it.localizedMessage)
            }
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

//        var database = FirebaseFirestore.getInstance()
//        database.collection("users")
//            .whereEqualTo("email", textEmail.text.toString())
//            .whereEqualTo("password", textPass.text.toString())
//            .get()
//            .addOnCompleteListener {task ->
//                if(task.isSuccessful && task.result != null && task.result.documents.size > 0){
//                    val documentSnapshot = task.result.documents[0]
//                    preferenceManager.putBoolean("isLogin", true)
//                    preferenceManager.putString("id", documentSnapshot.id)
//                    preferenceManager.putString("email", documentSnapshot.getString("email")!!)
//                    preferenceManager.putString("name", documentSnapshot.getString("name")!!)
//                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                }
//                else
//                {
//                    loading(false)
//                    showToast("Unable to log in")
//                }
//            }
//            .addOnFailureListener{exception ->
//                loading(false)
//                exception.message?.let { showToast(it) }
//            }
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
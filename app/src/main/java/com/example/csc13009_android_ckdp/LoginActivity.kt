package com.example.csc13009_android_ckdp

import android.content.Intent
import android.content.IntentSender
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
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

    private lateinit var callbackManager: CallbackManager
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var gsc : GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 3763
    private val REQ_FORGET_PASSWORD = 3440
    private var emailFB: String = ""
    private var nameFB: String = ""
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

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        btnSignInGG.setOnClickListener {
            loginGG()

        }
        callbackManager = CallbackManager.Factory.create()


        btnSignInFb.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email","public_profile"))
            //FacebookSdk.sdkInitialize(applicationContext)
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        var mGraphRequest = GraphRequest.newMeRequest(
                            loginResult.accessToken,object : GraphRequest.GraphJSONObjectCallback {
                                override fun onCompleted(me: JSONObject?, response: GraphResponse?) {
                                    emailFB = me!!.optString("email")
                                    nameFB = me!!.optString("name")
                                }
                        })

                        var paramenters = Bundle()
                        paramenters.putString("fields", "email, name, birthday")
                        mGraphRequest.parameters = paramenters
                        mGraphRequest.executeAsync()

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
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                   Log.d("FACEBOOK",  emailFB)
                    user!!.updateEmail(emailFB)
                    val userData = Users(emailFB, nameFB, user!!.photoUrl!!.toString(), user.uid)
//
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

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

    private fun loginGG() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 9246)

        Log.d("GOOGLE", "DANG dang nhap")

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
        callbackManager.onActivityResult(requestCode,resultCode,data)
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
            9246 -> {
                val accountTask: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                Log.d("Login GG", "Toi dayy!!!")

                var accountGG = accountTask.getResult(ApiException::class.java)
                var credential = GoogleAuthProvider.getCredential(accountGG.idToken, null)
                signInWithCredential(credential)

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
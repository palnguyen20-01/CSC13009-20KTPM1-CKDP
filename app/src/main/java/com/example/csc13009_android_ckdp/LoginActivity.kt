package com.example.csc13009_android_ckdp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.FirstAid.FirstAidActivity
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    lateinit var btnSignInGG: Button
    lateinit var progressBtn: ProgressBar
    lateinit var textEmail: TextView
    lateinit var textPass: TextView
    lateinit var textSignUp: TextView
    lateinit var preferenceManager: PreferenceManager

    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient
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

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)


        btnSignInGG.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            Log.d("Login", "Login by GG successs")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
            Log.d("Login", "Login by GG successs")
        }else{
            Log.d("Login", "Login by GG fail")
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this , MainActivity::class.java)
                startActivity(intent)
            }else{
                Log.d("Login", "Login by GG fail")
                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_SHORT).show()

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
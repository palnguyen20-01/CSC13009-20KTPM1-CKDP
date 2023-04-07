package com.example.csc13009_android_ckdp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


import java.io.ByteArrayOutputStream
import java.util.*


class SignUpActivity : AppCompatActivity() {
    lateinit var btnSignUp: Button
    lateinit var textEmail: EditText
    lateinit var textName: EditText
    lateinit var textPassword: EditText
    lateinit var textConfirm: EditText
    lateinit var progressBtn: ProgressBar
    lateinit var preferenceManager: PreferenceManager
    lateinit var auth : FirebaseAuth
    var encodedImage : String = ""

    lateinit var database: FirebaseDatabase
    var storageReference: StorageReference? = null
    @RequiresApi(Build.VERSION_CODES.O)
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnSignUp = findViewById(R.id.btnSignUp)
        textEmail = findViewById(R.id.textSignUpEmail)
        textName = findViewById(R.id.textSignUpName)
        textPassword = findViewById(R.id.textSignUpPassword)
        textConfirm = findViewById(R.id.textSignUpConfirmPassword)
        progressBtn = findViewById(R.id.progressSignUpButton)
        preferenceManager = PreferenceManager(applicationContext)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        btnSignUp.setOnClickListener {
            if(isValidSignUp()){
                signUp()

            }
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bitmap : Bitmap) : String{
        var previewWidth = 150
        var previewHeight = bitmap.height * previewWidth / bitmap.width
        var previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        var byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        var bytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signUp() {
        loading(true)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_avatar)
        encodedImage = encodeImage(bitmap)

        auth.createUserWithEmailAndPassword(textEmail.text.toString(), textPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val id = task.result.user?.uid
                    storageReference = FirebaseStorage.getInstance().reference.child("images/" + "user.png")

                    storageReference!!.downloadUrl.addOnSuccessListener {uri ->
                        val user = Users(textEmail.text.toString(),textName.text.toString(), uri.toString(), id!!)

                        database.reference.child("Users").child(id!!).setValue(user)
                            .addOnSuccessListener {
                                Log.d("firebase", "Got value")
                            }.addOnFailureListener{
                                Log.d("firebase", "Error getting data")
                            }

                        updateUserInfo(uri)
                    }



                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Sign up", "createUserWithEmail:failure", task.exception)
                    loading(false)
                    Toast.makeText(baseContext, "Your email already existed",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun updateUserInfo(uri: Uri){
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(textName.text.toString())
            .setPhotoUri(uri)
            .build()
        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("\nUser profile updated.")
            }
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
        else if(!textPassword.text.toString().equals(textConfirm.text.toString()))
        {
            showToast("Password & confirm password must be same")
            return false
        }
        else return true
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            btnSignUp.visibility = View.INVISIBLE
            progressBtn.visibility = View.VISIBLE
        }
        else{
            btnSignUp.visibility = View.VISIBLE
            progressBtn.visibility = View.INVISIBLE
        }
    }
}
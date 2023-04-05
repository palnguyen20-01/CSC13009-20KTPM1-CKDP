package com.example.csc13009_android_ckdp.Profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.SettingFragment
import com.example.csc13009_android_ckdp.utilities.PreferenceManager
import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class InfoChange : AppCompatActivity() {
    lateinit var txtName : EditText
    lateinit var txtEmail : EditText
    lateinit var btnCancel : Button
    lateinit var btnSave : Button
    var encodedImage : String = ""
    lateinit var imageProfile : ImageView

    var imageUri: Uri? = null
    lateinit var preferenceManager: PreferenceManager
    var user: FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var storageReference: StorageReference
    lateinit var progressDialog: ProgressDialog
    @RequiresApi(Build.VERSION_CODES.O)
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changeinfo)

        btnCancel = findViewById(R.id.btnCancelChangeProfile)
        btnSave = findViewById(R.id.btnSaveChangeProfile)
        imageProfile = findViewById(R.id.imageChangeProfile)
        txtName = findViewById(R.id.textChangeProfileName)
        txtEmail = findViewById(R.id.textChangProfileEmail)
        preferenceManager = PreferenceManager(applicationContext)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        var userCurrent = auth.currentUser

        if (userCurrent != null) {
            txtName.setText(userCurrent.displayName)
            txtEmail.setText(userCurrent.email)
            Glide
                .with(this)
                .load(userCurrent.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.user_avatar)
                .into(imageProfile)

            storageReference = FirebaseStorage.getInstance().getReference("images/" + userCurrent.uid)
        }

        imageProfile.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

        btnSave.setOnClickListener {
            user = FirebaseAuth.getInstance().currentUser

            progressDialog.show()

            storageReference.putFile(imageUri!!).addOnCompleteListener{
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    var updateUser = HashMap<String, Any>()
                    updateUser["email"] = txtEmail.text.toString()
                    updateUser["name"] = txtName.text.toString()
                    updateUser["image"] = uri.toString()

                    database.reference.child("Users").child(userCurrent!!.uid).updateChildren(updateUser)
                        .addOnSuccessListener {
                            updateUserInfo(uri)
                            val intent = Intent(this, SettingFragment::class.java)
                            intent.putExtra("name", txtName.text.toString())
                            intent.putExtra("email", txtEmail.text.toString())
                            intent.putExtra("image", encodedImage)
                            setResult(RequestCodeResult.CHANGE_INFORMATION, intent);
                            if(progressDialog.isShowing)
                                progressDialog.dismiss()

                            finish()
                        }
                        .addOnFailureListener {
                            if(progressDialog.isShowing)
                                progressDialog.dismiss()
                            Log.d("Update", "FAILED!")
                            finish()
                        }
                }
            }
            .addOnFailureListener{
                Log.d("Image", " PUSH FAILED!")
                finish()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }

    }

    private fun updateUserInfo(uri: Uri) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(txtName.text.toString())
            .setPhotoUri(uri)
            .build()
        user!!.updateProfile(profileUpdates)
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
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if(result.data != null){
                imageUri = result.data!!.data!!
                try{
                    var inputStream = contentResolver.openInputStream(imageUri!!)
                    var bitmap = BitmapFactory.decodeStream(inputStream)
                    imageProfile.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)

                }catch(e : FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }
    }

}
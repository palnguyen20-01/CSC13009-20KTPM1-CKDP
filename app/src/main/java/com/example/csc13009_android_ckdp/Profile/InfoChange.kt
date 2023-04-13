package com.example.csc13009_android_ckdp.Profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.SettingFragment

import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*

class InfoChange : AppCompatActivity() {
    lateinit var txtName : EditText
    lateinit var txtBirthday : EditText
    lateinit var btnCancel : Button
    lateinit var btnSave : Button
    var encodedImage : String = ""
    lateinit var imageProfile : ImageView
    var birth: String? = null
    var imageUri: Uri? = null
    var isChooseImage: Boolean = false
    var user: FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var storageReference: StorageReference
    lateinit var progressDialog: ProgressDialog
    var userCurrent:FirebaseUser? = null
    @RequiresApi(Build.VERSION_CODES.O)
    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)

        initComponents()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        userCurrent = auth.currentUser

        initData()

        chooseImage()

        btnSave.setOnClickListener {
            user = FirebaseAuth.getInstance().currentUser

            progressDialog.show()

            if(isChooseImage){
                saveProfileWithImage()
            }
            else{
                saveProfile()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chooseImage() {
        imageProfile.setOnClickListener {
            isChooseImage = true
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun initComponents(){
        btnCancel = findViewById(R.id.btnCancelChangeProfile)
        btnSave = findViewById(R.id.btnSaveChangeProfile)
        imageProfile = findViewById(R.id.imageChangeProfile)
        txtName = findViewById(R.id.textChangeProfileName)
        txtBirthday = findViewById(R.id.textChangProfileBirthday)
    }
    private fun initData() {
        database.reference.child("Users").child(userCurrent!!.uid).get()
            .addOnCompleteListener {task ->

                if(task.result.exists())
                {
                    var dataSnapshot = task.result
                    birth = dataSnapshot.child("birthday").value.toString()

                    if (userCurrent != null) {
                        txtName.setText(userCurrent!!.displayName)
                        txtBirthday.setText(birth)
                        GlideApp
                            .with(this)
                            .load(userCurrent!!.photoUrl)
                            .centerCrop()
                            .placeholder(R.drawable.user_avatar)
                            .into(imageProfile)

                        storageReference = FirebaseStorage.getInstance().getReference("images/" + userCurrent!!.uid)
                    }
                }
            }
            .addOnFailureListener {
                showToast("Failed to read data from FirebaseDatabase")
            }


    }

    private fun saveProfileWithImage(){
        storageReference.putFile(imageUri!!).addOnCompleteListener{
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                var updateUser = HashMap<String, Any>()
                updateUser["birthday"] = txtBirthday.text.toString()
                updateUser["name"] = txtName.text.toString()
                updateUser["image"] = uri.toString()

                database.reference.child("Users").child(userCurrent!!.uid).updateChildren(updateUser)
                    .addOnSuccessListener {
                        updateUserInfo(uri)
                        val intent = Intent(this, SettingFragment::class.java)
                        intent.putExtra("name", txtName.text.toString())
                        intent.putExtra("birthday", txtBirthday.text.toString())
                        intent.putExtra("image", encodedImage)
                        setResult(RequestCodeResult.CHANGE_INFORMATION, intent);
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()

                        finish()
                    }
                    .addOnFailureListener {
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()
                        showToast("Update FAILED!")
                        finish()
                    }
            }
        }
            .addOnFailureListener{
                showToast("Image push FAILED!")
                finish()
            }
    }

    private fun saveProfile(){

        var updateUser = HashMap<String, Any>()
        updateUser["birthday"] = txtBirthday.text.toString()
        updateUser["name"] = txtName.text.toString()

        database.reference.child("Users").child(userCurrent!!.uid).updateChildren(updateUser)
            .addOnCompleteListener {
                task->
                if(task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(txtName.text.toString())
                        .build()
                    user!!.updateProfile(profileUpdates)

                    val intent = Intent(this, SettingFragment::class.java)
                    intent.putExtra("name", txtName.text.toString())
                    intent.putExtra("birthday", txtBirthday.text.toString())
                    setResult(RequestCodeResult.CHANGE_INFORMATION, intent);
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()

                    finish()

                }
                else{
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                    showToast("Update profile FAILED!")
                    finish()
                }
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
            else{
                isChooseImage = false
            }
        }
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
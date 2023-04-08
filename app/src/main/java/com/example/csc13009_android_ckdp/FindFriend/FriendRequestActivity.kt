package com.example.csc13009_android_ckdp.FindFriend

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FriendRequestActivity : AppCompatActivity() {

    lateinit var imgView: ImageView
    lateinit var txtName: TextView
    lateinit var userRef: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var btnCancel: Button
    lateinit var btnAccept: Button
    lateinit var auth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var adapter: FirebaseRecyclerAdapter<Users, FindFriendViewHolder>

    var imageUrl: String? = null
    var userName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        imgView = findViewById(R.id.imgFriendRequest)
        txtName = findViewById(R.id.txtNameFriendRequest)
        btnAccept = findViewById(R.id.btnAcceptFriendRequest)
        btnCancel = findViewById(R.id.btnCancelFriendRequest)
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(intent.getStringExtra("uid")!!)

        loadUser()

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadUser() {
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    imageUrl = snapshot.child("image").value.toString()
                    userName = snapshot.child("name").value.toString()

                    GlideApp.with(applicationContext)
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.user_avatar)
                        .into(imgView)

                    txtName.text = userName
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
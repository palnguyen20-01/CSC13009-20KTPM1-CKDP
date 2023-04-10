package com.example.csc13009_android_ckdp.FindFriend

import android.media.Image
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.EventListener
import java.util.HashMap

class FriendRequestActivity : AppCompatActivity() {

    lateinit var imgView: ImageView
    lateinit var txtName: TextView
    lateinit var userRef: DatabaseReference
    lateinit var requestRef: DatabaseReference
    lateinit var friendRef: DatabaseReference
    lateinit var btnCancel: Button
    lateinit var btnAccept: Button
    lateinit var auth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var adapter: FirebaseRecyclerAdapter<Users, FindFriendViewHolder>

    lateinit var userId: String
    var imageUrl: String? = null
    var userName: String? = null
    var currentState: String = "nothing"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        imgView = findViewById(R.id.imgFriendRequest)
        txtName = findViewById(R.id.txtNameFriendRequest)
        btnAccept = findViewById(R.id.btnAcceptFriendRequest)
        btnCancel = findViewById(R.id.btnCancelFriendRequest)
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        userId = intent.getStringExtra("uid")!!
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
        friendRef = FirebaseDatabase.getInstance().reference.child("Friends")

        loadUser()

        btnAccept.setOnClickListener {
            performAction()
        }
        btnCancel.setOnClickListener {
            unFriend()
        }
        checkUserExist()
    }

    private fun unFriend() {
        if(currentState.equals("friend")){
            friendRef.child(mUser.uid).child(userId).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    friendRef.child(userId).child(mUser.uid).removeValue().addOnCompleteListener { task ->
                        showToast("You are unfriended")
                        currentState= "nothing"
                        btnAccept.text = resources.getString(R.string.send_friend_request)
                        btnCancel.visibility = View.GONE

                    }
                }
            }
        }
        else if(currentState.equals("he_sent_pending")){
            var reqMap = HashMap<String, Any>()
            reqMap["status"] = "decline"
            requestRef.child(userId).child(mUser.uid).updateChildren(reqMap).addOnCompleteListener {task->
                if(task.isSuccessful){
                    showToast("You have declined a friend request")
                    btnCancel.visibility = View.GONE
                    currentState = "he_sent_decline"
                    btnAccept.visibility = View.GONE
                }

            }
        }
    }


    private fun performAction() {
        if(currentState.equals("nothing")){
            var reqMap = HashMap<String, Any>()
            reqMap["status"] = "pending"
            requestRef.child(mUser.uid).child(userId).updateChildren(reqMap).addOnCompleteListener {task->
                if(task.isSuccessful){
                    showToast("You have sent a friend request")
                    btnCancel.visibility = View.GONE
                    currentState = "i_sent_pending"
                    btnAccept.text = resources.getString(R.string.cancel_friend_request)
                }

            }
        }
        else if(currentState.equals("i_sent_pending") || currentState.equals("i_sent_decline")){
            requestRef.child(mUser.uid).child(userId).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    showToast("You have canceled a friend request")
                    currentState = "nothing"
                    btnAccept.text = resources.getString(R.string.send_friend_request)
                    btnCancel.visibility = View.GONE
                }

            }
        }
        else if(currentState.equals("he_sent_pending")){ //waiting for accepting
            requestRef.child(userId).child(mUser.uid).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    var reqMap = HashMap<String, Any>()
                    reqMap["status"] = "friend"
                    reqMap["name"] = userName!!
                    reqMap["image"] = imageUrl!!
                    friendRef.child(mUser.uid).child(userId).updateChildren(reqMap).addOnCompleteListener { task->
                        currentState = "friend"
                        showToast("You added friend")
                        btnAccept.visibility = View.GONE
                        btnCancel.text  = resources.getString(R.string.unfriend)
                        btnCancel.visibility = View.VISIBLE

                    }
                }

            }
        }
        else if(currentState.equals("friend")){

        }
    }

    private fun checkUserExist(){
        //check friend from Friend db
        friendRef.child(mUser.uid).child(userId).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    currentState = "friend"

                    btnAccept.visibility = View.GONE
                    btnCancel.text  = resources.getString(R.string.unfriend)
                    btnCancel.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            }
        )

        friendRef.child(userId).child(mUser.uid).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    currentState = "friend"
                    btnAccept.visibility = View.GONE
                    btnCancel.text  = resources.getString(R.string.unfriend)
                    btnCancel.visibility = View.VISIBLE

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            }
        )
        //check if pending request
        requestRef.child(mUser.uid).child(userId).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if(snapshot.child("status").value.toString().equals("pending")) {
                        currentState = "i_sent_pending"
                        btnAccept.text = resources.getString(R.string.cancel_friend_request)
                        btnCancel.visibility = View.GONE
                    }
                    else if(snapshot.child("status").value.toString().equals("decline")){
                        currentState = "i_sent_decline"
                        btnAccept.text = resources.getString(R.string.cancel_friend_request)
                        btnCancel.visibility = View.GONE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

            }
        )
        //receive request
        requestRef.child(userId).child(mUser.uid).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if(snapshot.child("status").value.toString().equals("pending")) {
                        currentState = "he_sent_pending"
                        btnAccept.text = resources.getString(R.string.accept_friend_request)
                        btnCancel.text = resources.getString(R.string.cancel_friend_request)
                        btnCancel.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

            }
        )

        if(currentState.equals("nothing")){
            currentState = "nothing"
            btnAccept.text = resources.getString(R.string.send_friend_request)
            btnCancel.visibility = View.GONE
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
    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
package com.example.csc13009_android_ckdp.Notification

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.MainActivity
import com.example.csc13009_android_ckdp.Models.MyFeatures
import com.example.csc13009_android_ckdp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.nio.charset.Charset

class NotificationFragment : Fragment() {
    lateinit var main : MainActivity
    lateinit var user : FirebaseUser
    lateinit var notiRef : DatabaseReference
    lateinit var userRef : DatabaseReference
    val notificationService = NotificationService()

    var notiList = ArrayList<Notification>()
    lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = FirebaseAuth.getInstance().currentUser!!
        notiRef = FirebaseDatabase.getInstance().reference.child("Notifications")
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        notificationService.seenNoti(main,user.uid)
        createNotiListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        val noti_RCV = view.findViewById<View>(R.id.notificationRCV) as RecyclerView
        noti_RCV.layoutManager = LinearLayoutManager(main)
        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(main, DividerItemDecoration.VERTICAL)
        noti_RCV.addItemDecoration(itemDecoration)

        prepareData()
        adapter = NotificationAdapter(notiList)
        noti_RCV.adapter = adapter

        return view
    }

    private fun createNotiListener(){
        notiRef.child(user.uid).child("lastNotiID").addValueEventListener( object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    notiRef.child(user.uid).child("seenNotiID").get()
                        .addOnCompleteListener {task->
                            if(task.result.exists()){
                                val dataSnapshot = task.result
                                if(snapshot.value.toString() != dataSnapshot.value.toString()){
                                    notiList.clear()
                                    prepareData()
                                }
                            }
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }

    private fun prepareData(){
        notiRef.child(user.uid).get()
            .addOnCompleteListener{task ->
                if(task.result.exists())
                {
                    var dataSnapshot = task.result
                    val lastNotiID = dataSnapshot.child("lastNotiID").value.toString().toInt()
                    for(i in lastNotiID downTo 1){
                        val notiInfo = dataSnapshot.child("Noti_List").child(i.toString())
                        if(notiInfo.value != null) {
                            val type = notiInfo.child("type").value.toString()
                            val srcID = notiInfo.child("srcID").value.toString()
                            val time = notiInfo.child("time").value.toString()
                            val timeAgo = getTime(time.toLong())
                            userRef.child(srcID).child("name").get()
                                .addOnCompleteListener { task ->
                                    if(task.result.exists())
                                    {
                                        var dataSnapshot = task.result
                                        val otherName = dataSnapshot.value.toString()
                                        var content = ""
                                        if (type == "friendReq") {
                                            content = otherName + getString(R.string.noti_friendreq)
                                        }else if(type == "friendAc"){
                                            content = otherName + getString(R.string.noti_friendac)
                                        }else if(type == "acFriend"){
                                            content = getString(R.string.noti_acfriend) + "${otherName}"
                                        }else if (type == "clock") {
                                            content = otherName + getString(R.string.noti_clock)
                                        }
                                        notiList.add(Notification(i.toString(), srcID, type, content, timeAgo))
                                        adapter.notifyDataSetChanged()

                                    }
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                showToast("Failed to read data from FirebaseDatabase")
            }
    }

    private fun getTime(time : Long): String{
        val now : Long = System.currentTimeMillis()
        val ago : CharSequence = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
        return ago.toString()
    }

    private fun showToast(message: String){
        Toast.makeText(main, message, Toast.LENGTH_SHORT).show()
    }
}
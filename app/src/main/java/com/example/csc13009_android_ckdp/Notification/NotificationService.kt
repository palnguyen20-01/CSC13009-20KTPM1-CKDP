package com.example.csc13009_android_ckdp.Notification

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.View
import com.example.csc13009_android_ckdp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class NotificationService {
    var notiRef : DatabaseReference
    var requestRef : DatabaseReference
    constructor(){
        notiRef = FirebaseDatabase.getInstance().reference.child("Notifications")
        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
    }

    public fun notifyForThatPerson(uid : String, type : String, srcID : String, time : String){
        notiRef.child(uid).child("lastNotiID").get()
            .addOnCompleteListener {task->
                val lastNotiIDMap = HashMap<String, Any>()
                var lastNotiID = 0
                val notiMap = HashMap<String, Any>()

                if(task.result.exists()){
                    val dataSnapshot = task.result
                    lastNotiID = dataSnapshot.value.toString().toInt()
                }
                lastNotiIDMap["lastNotiID"] = (lastNotiID+1).toString()
                notiRef.child(uid).updateChildren(lastNotiIDMap)

                notiMap["type"] = type
                notiMap["srcID"] = srcID
                notiMap["time"] = time
                notiRef.child(uid).child("Noti_List").child((lastNotiID+1).toString()).updateChildren(notiMap)
            }
            .addOnFailureListener {
                Log.i("phuc4570","Failed")
            }
    }

    public fun handleFriendRequest(uid : String, srcID : String, answer: Boolean){
        notiRef.child(uid).child("lastNotiID").get()
            .addOnCompleteListener { task ->
                var lastNotiID = 0
                if (task.result.exists()) {
                    val dataSnapshot = task.result
                    lastNotiID = dataSnapshot.value.toString().toInt()
                    notiRef.child(uid).child("Noti_List").get()
                        .addOnCompleteListener {task->
                            if(task.result.exists()){
                                val dataSnapshot = task.result
                                for(i in 1..lastNotiID){
                                    if(dataSnapshot.child(i.toString()).child("srcID").value.toString() == srcID
                                        && dataSnapshot.child(i.toString()).child("type").value.toString() == "friendReq"){
                                        if(!answer) {
                                            notiRef.child(uid).child("Noti_List").child(i.toString()).removeValue()
                                        }else{
                                            var notiMap = HashMap<String, Any>()
                                            notiMap["type"] = "acFriend"
                                            notiRef.child(uid).child("Noti_List").child(i.toString()).updateChildren(notiMap)
                                        }
                                    }
                                }
                            }
                        }
                }
            }

    }

    public fun answerFriendRequest(uid : String, srcID : String, answer : String){
        if(answer == "rejFriend"){
            handleFriendRequest(uid, srcID, false)
            var reqMap = HashMap<String, Any>()
            reqMap["status"] = "decline"
            requestRef.child(srcID).child(uid).updateChildren(reqMap)
        }else if(answer == "acFriend"){
            handleFriendRequest(uid, srcID, true)
        }
    }

    private fun changeNotiIcon(activity: Activity, isNoti : Boolean){
        val nav : BottomNavigationView = activity.findViewById(R.id.bottom_navigation)
        val menu : Menu = nav.menu
        if(isNoti){
            menu.getItem(3).setIcon(R.drawable.bell_overlay)
        }else{
            menu.getItem(3).setIcon(R.drawable.bell)
        }
    }

    private fun createNotiListener(activity: Activity, userID: String){
        notiRef.child(userID).child("lastNotiID").addValueEventListener( object :
            ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        changeNotiIcon(activity, true)
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
    }

    public fun checkStartNoti(activity : Activity){
        val user : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        createNotiListener(activity, user.uid)

        notiRef.child(user.uid).child("seenNotiID").get()
            .addOnCompleteListener{taskSeen->
                if(taskSeen.result.exists()){
                    val dataSeenSnapshot = taskSeen.result
                    notiRef.child(user.uid).child("lastNotiID").get()
                        .addOnCompleteListener {taskLast->
                            if(taskLast.result.exists()){
                                val dataLastSnapshot = taskLast.result
                                if(dataSeenSnapshot.value.toString().toInt() < dataLastSnapshot.value.toString().toInt()){
                                    changeNotiIcon(activity, true)
                                }else{
                                    changeNotiIcon(activity, false)
                                }
                            }
                        }
                }
            }
    }

    public fun seenNoti(activity: Activity, userID : String){
        Log.i("phuc4570","seenNoti")
        changeNotiIcon(activity, false)
        var seenNotiIDMap = HashMap<String, Any>()
        notiRef.child(userID).child("lastNotiID").get()
            .addOnCompleteListener {task->
                if(task.result.exists()){
                    val dataSnapshot = task.result
                    val lastNotiID = dataSnapshot.value.toString()
                    seenNotiIDMap["seenNotiID"] = lastNotiID
                    notiRef.child(userID).updateChildren(seenNotiIDMap)
                }
            }
    }
}
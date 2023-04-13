package com.example.csc13009_android_ckdp.Notification

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NotificationService {
    var notiRef : DatabaseReference

    constructor(){
        notiRef = FirebaseDatabase.getInstance().reference.child("Notifications")
    }

    public fun notifyForThatPerson(uid : String, type : String, srcID : String, time : String){
        val lastNotiID = notiRef.child(uid).child("lastNotiID").get()
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
}
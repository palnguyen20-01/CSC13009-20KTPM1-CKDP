package com.example.csc13009_android_ckdp.Notification

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.MainActivity
import com.example.csc13009_android_ckdp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class NotificationAdapter(private var notifications: ArrayList<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    var onItemClick:((Notification, i: Int)->Unit)? = null
    lateinit var user : FirebaseUser
    lateinit var notiRef : DatabaseReference

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val noti_icon : ImageView = listItemView.findViewById(R.id.notificationIcon)
        val noti_content : TextView = listItemView.findViewById(R.id.notificationTextView)
        val noti_time : TextView = listItemView.findViewById(R.id.notification_timeTextView)
        val delete_button : Button = listItemView.findViewById(R.id.deleteButton)

        init{
            listItemView.setOnClickListener{onItemClick?.invoke(notifications[adapterPosition], adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var layout : Int = R.layout.item_notification
        val notificationView = inflater.inflate(layout, parent, false)

        user = FirebaseAuth.getInstance().currentUser!!
        notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(user.uid)

        return ViewHolder(notificationView)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noti: Notification = notifications.get(position)
        val noti_content = holder.noti_content
        noti_content.setText(noti.srcID)
        val noti_time = holder.noti_time
        noti_time.setText(noti.time)
        val noti_icon = holder.noti_icon
        if(noti.type == "friend"){
            noti_icon.setImageResource(R.drawable.friends)
        }else if(noti.type == "clock"){
            noti_icon.setImageResource(R.drawable.time)
        }
        val delete_button = holder.delete_button
        delete_button.setOnClickListener {
            val id = notifications[position].notiID
            notiRef.child("Noti_List").child(id).removeValue().addOnCompleteListener{task->
                if(task.isSuccessful){
                    Log.i("phuc4570","Successfully")
                }else Log.i("phuc4570", "Error")
            }

            notifications.removeAt(position)
            this.notifyDataSetChanged()
        }
    }
}
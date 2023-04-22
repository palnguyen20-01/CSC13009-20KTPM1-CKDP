package com.example.csc13009_android_ckdp.Notification

import android.app.Activity
import android.provider.ContactsContract.Data
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
import kotlin.collections.HashMap
import kotlin.coroutines.coroutineContext

class NotificationAdapter(private var notifications: ArrayList<Notification>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick:((Notification, i: Int)->Unit)? = null
    lateinit var user : FirebaseUser
    lateinit var notiRef : DatabaseReference
    lateinit var userRef : DatabaseReference
    lateinit var requestRef : DatabaseReference
    lateinit var friendRef : DatabaseReference
    val notificationService = NotificationService()

    inner class ViewHolder_1(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val noti_icon : ImageView = listItemView.findViewById(R.id.friendNotificationIcon)
        val noti_content : TextView = listItemView.findViewById(R.id.friendNotificationTextView)
        val noti_time : TextView = listItemView.findViewById(R.id.friendNotification_timeTextView)
        val reject_button : Button = listItemView.findViewById(R.id.delete_friend_button)
        val accept_button : Button = listItemView.findViewById(R.id.accept_friend_button)

        init{
            listItemView.setOnClickListener{onItemClick?.invoke(notifications[adapterPosition], adapterPosition)}
        }
    }

    inner class ViewHolder_2(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val noti_icon : ImageView = listItemView.findViewById(R.id.notificationIcon)
        val noti_content : TextView = listItemView.findViewById(R.id.notificationTextView)
        val noti_time : TextView = listItemView.findViewById(R.id.notification_timeTextView)
        val delete_button : Button = listItemView.findViewById(R.id.deleteButton)

        init{
            listItemView.setOnClickListener{onItemClick?.invoke(notifications[adapterPosition], adapterPosition)}
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(notifications[position].type == "friendReq"){
            return 1;
        }
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var layout : Int = 0
        if(viewType == 1){
            layout = R.layout.item_friend_notification
        }else if(viewType == 2){
            layout = R.layout.item_notification
        }
        val notificationView = inflater.inflate(layout, parent, false)

        user = FirebaseAuth.getInstance().currentUser!!
        notiRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(user.uid)
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
        friendRef = FirebaseDatabase.getInstance().reference.child("Friends")

        if(viewType == 1){
            return ViewHolder_1(notificationView)
        }else{
            return ViewHolder_2(notificationView)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder_temp: RecyclerView.ViewHolder, position: Int) {
        if(holder_temp.itemViewType == 1){
            val holder = holder_temp as ViewHolder_1
            val noti: Notification = notifications.get(position)
            val noti_content = holder.noti_content
            noti_content.setText(noti.content)
            val noti_time = holder.noti_time
            noti_time.setText(noti.time)
            val noti_icon = holder.noti_icon
            if(noti.type == "friend"){
                noti_icon.setImageResource(R.drawable.friends)
            }else if(noti.type == "clock"){
                noti_icon.setImageResource(R.drawable.time)
            }

            val reject_button = holder.reject_button
            reject_button.setOnClickListener {
                val id = notifications[position].notiID
                notiRef.child("Noti_List").child(id).removeValue()

                notifications.removeAt(position)
                this.notifyDataSetChanged()

                var reqMap = HashMap<String, Any>()
                reqMap["status"] = "decline"
                requestRef.child(notifications[position].srcID).child(user.uid).updateChildren(reqMap)
            }

            val accept_button = holder.accept_button
            accept_button.setOnClickListener {
                notifications[position].type = "acFriend"
                val srcName = notifications[position].content.split(" ").get(0)
                notifications[position].content = holder_temp.itemView.context.getString(R.string.noti_acfriend) + "${srcName}"
                this.notifyDataSetChanged()

                var notiMap = HashMap<String, Any>()
                notiMap["type"] = "acFriend"
                notiRef.child("Noti_List").child(notifications[position].notiID).updateChildren(notiMap)

                requestRef.child(notifications[position].srcID).child(user.uid).removeValue()
                var reqMap = HashMap<String, Any>()

                userRef.child(notifications[position].srcID).child("image").get()
                    .addOnCompleteListener{task->
                        if(task.result.exists()){
                            val dataSnapshot = task.result
                            val image = dataSnapshot.value.toString()
                            reqMap["status"] = "friend"
                            reqMap["name"] = srcName
                            reqMap["image"] = image
                            friendRef.child(user.uid).child(notifications[position].srcID).updateChildren(reqMap)
                        }
                    }

                userRef.child(user.uid).child("name").get()
                    .addOnCompleteListener{task->
                        if(task.result.exists()){
                            val dataSnapshot = task.result
                            val name = dataSnapshot.value.toString()
                            reqMap["status"] = "friend"
                            reqMap["name"] = name
                            userRef.child(user.uid).child("image").get()
                                .addOnCompleteListener{task->
                                    if(task.result.exists()){
                                        val dataSnapshot = task.result
                                        val image = dataSnapshot.value.toString()
                                        reqMap["image"] = image
                                        friendRef.child(notifications[position].srcID).child(user.uid).updateChildren(reqMap)
                                    }
                                }
                        }
                    }

                notificationService.notifyForThatPerson(notifications[position].srcID, "friendAc", user.uid, System.currentTimeMillis().toString())
            }
        }
        else{
            val holder = holder_temp as ViewHolder_2
            val noti: Notification = notifications.get(position)
            val noti_content = holder.noti_content
            noti_content.setText(noti.content)
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
                notiRef.child("Noti_List").child(id).removeValue()

                notifications.removeAt(position)
                this.notifyDataSetChanged()
            }
        }
    }
}
package com.example.csc13009_android_ckdp.FindFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FindFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtTitle: TextView? = null
    var imgAvatar: ImageView? = null


    fun bind(){
        txtTitle = itemView.findViewById<TextView>(R.id.txtTitleFriendRequest)
        imgAvatar = itemView.findViewById<ImageView>(R.id.imgItemFriendRequest)
    }
}
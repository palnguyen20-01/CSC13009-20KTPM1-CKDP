package com.example.csc13009_android_ckdp.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.HomeFragment
import com.example.csc13009_android_ckdp.MyFeatures
import com.example.csc13009_android_ckdp.R

class HomeAdapter(private val contextMain: Context,
                  private val studentList: List<MyFeatures>
    ) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){
    var onItemClick: ((MyFeatures) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView: TextView = listItemView.findViewById<TextView>(R.id.txtTitle)
        val imageView: ImageView = listItemView.findViewById<ImageView>(R.id.logoImgView)

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(studentList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var studentView: View? = null

        studentView = inflater.inflate(R.layout.item_home, parent, false)

        return ViewHolder(studentView)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val contact = studentList[position]
        // Set item views based on your views and data model
        val name = holder.nameTextView
        val avatar = holder.imageView
        name.text = contact.getFeatureName()
        avatar.setImageResource(contact.getImageId())
        holder.itemView.setOnClickListener {

        }
    }

}
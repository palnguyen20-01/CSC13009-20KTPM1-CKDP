package com.example.csc13009_android_ckdp.FirstAid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R

class FirstAidAdapter(private val firstAids: List<FirstAid>): RecyclerView.Adapter<FirstAidAdapter.ViewHolder>() {
    var onItemClick:((FirstAid, i: Int)->Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val nameTextView: TextView = listItemView.findViewById(R.id.nameTextView)
        val imageView: ImageView = listItemView.findViewById(R.id.imageView)

        init{
            listItemView.setOnClickListener{onItemClick?.invoke(firstAids[adapterPosition], adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var layout : Int = R.layout.activity_first_aid_item
        val firstAidView = inflater.inflate(layout, parent, false)
        return ViewHolder(firstAidView)
    }

    override fun getItemCount(): Int {
        return firstAids.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val firstAid: FirstAid = firstAids.get(position)
        val nameTextView = holder.nameTextView
        nameTextView.setText(firstAid.name)
        val imageView = holder.imageView
        imageView.setImageResource(firstAid.imageSource)
    }
}
package com.example.csc13009_android_ckdp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.*
import com.example.csc13009_android_ckdp.BMI.BMIActivity
import com.example.csc13009_android_ckdp.FirstAid.FirstAidActivity
import com.example.csc13009_android_ckdp.HospitalMap.HospitalMapActivity
import com.example.csc13009_android_ckdp.Models.MyFeatures

class HomeAdapter(private val context: Context,
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
        val index: Int = holder.absoluteAdapterPosition
        //.getAdapterPosition()
        holder.itemView.setOnClickListener {
            if(index == 0) {
                val intent = Intent(context, FirstAidActivity::class.java)
                context.startActivity(intent)
            }
            else if(index == 1){
                val intent = Intent(context, BMIActivity::class.java)
                context.startActivity(intent)
            }
            else if(index == 2){
                val intent = Intent(context, AlarmActivity::class.java)
                context.startActivity(intent)
            }
            else if(index == 3){
                val intent = Intent(context,HospitalMapActivity::class.java)
                context.startActivity(intent)
            }else if(index == 4){
                val intent = Intent(context,Health_Advice::class.java)
                context.startActivity(intent)
            }else if(index == 5){
                val intent = Intent(context,DrugInfoActivity::class.java)
                context.startActivity(intent)
            }else if(index == 6){
                val intent = Intent(context,SkinDisease::class.java)
                context.startActivity(intent)
            }
        }
    }

}
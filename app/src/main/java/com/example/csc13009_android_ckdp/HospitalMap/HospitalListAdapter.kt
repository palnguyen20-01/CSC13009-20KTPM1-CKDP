package com.example.csc13009_android_ckdp.HospitalMap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R

class HospitalListAdapter(private val hospitals: List<Hospital>): RecyclerView.Adapter<HospitalListAdapter.ViewHolder>() {
    var onItemClick:((Hospital, i: Int)->Unit)? = null

    inner class ViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val hospitalNameTextView: TextView = listItemView.findViewById(R.id.hospitalNameTextView)
        val hospitalAddressTextView: TextView = listItemView.findViewById(R.id.hospitalAddressTextView)
        val hospitalDistanceTextView: TextView = listItemView.findViewById(R.id.hospitalDistanceTextView)

        init{
            listItemView.setOnClickListener{onItemClick?.invoke(hospitals[adapterPosition], adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var layout : Int = R.layout.activity_hospital_list_item
        val hospitalView = inflater.inflate(layout, parent, false)
        return ViewHolder(hospitalView)
    }

    override fun getItemCount(): Int {
        return hospitals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospital: Hospital = hospitals.get(position)
        val hospitalNameTextView = holder.hospitalNameTextView
        hospitalNameTextView.setText(hospital.name)
        val hospitalAddressTextView = holder.hospitalAddressTextView
        hospitalAddressTextView.setText(hospital.address)
        val hospitalDistanceTextView = holder.hospitalDistanceTextView
        hospitalDistanceTextView.setText(hospital.distanceInText)
    }
}
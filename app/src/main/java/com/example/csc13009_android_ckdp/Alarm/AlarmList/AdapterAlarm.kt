package com.example.csc13009_android_ckdp.Alarm.AlarmList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.ItemAlarmBinding

class AdapterAlarm : RecyclerView.Adapter<AdapterAlarm.AlarmViewHolder>(){

    private var mList= ArrayList<Alarm>()

    init {
        for(i in 0..10){
            mList.add(Alarm(i.toLong(),12,30,false,true,true,true,true,false,false,true))
        }
    }

class AlarmViewHolder(var binding:ItemAlarmBinding):RecyclerView.ViewHolder(binding.root){
fun bind(alarm:Alarm){
    binding.timeTV.text=alarm.getTime()
    binding.weekTV.text=alarm.getRepeat()
    binding.switchBTN.isChecked=alarm.start
}
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
   return AlarmViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
holder.bind(mList.get(position))
    }

}
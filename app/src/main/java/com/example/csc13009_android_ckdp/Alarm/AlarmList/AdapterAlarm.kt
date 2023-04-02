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
private var onToggleListener: OnToggleListener?=null
   private var onClickAlarmListener:OnClickAlarmListener?=null

inner class AlarmViewHolder(var binding:ItemAlarmBinding):RecyclerView.ViewHolder(binding.root),View.OnClickListener,View.OnLongClickListener{

    init {
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
    }
    fun bind(alarm:Alarm){
    binding.timeTV.text=alarm.getTime()
    binding.weekTV.text=alarm.getRepeat()
    binding.switchBTN.isChecked=alarm.start
    binding.switchBTN.setOnCheckedChangeListener { btnView, isCheck ->
        alarm.start=isCheck
        onToggleListener?.onToggle(alarm)  }
}

    override fun onClick(p0: View?) {
        onClickAlarmListener?.onClick(mList.get(adapterPosition))
    }

    override fun onLongClick(p0: View?): Boolean {
        onClickAlarmListener?.onLongClick(mList.get(adapterPosition))
        return true
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

    fun setData(it: List<Alarm>?) {
mList=it as ArrayList<Alarm>
        notifyDataSetChanged()
    }

    fun addOnToggleListener(onToggleListener: OnToggleListener){
        this.onToggleListener=onToggleListener
    }

    fun addOnClickAlarmListener(onClickAlarmListener: OnClickAlarmListener){
        this.onClickAlarmListener=onClickAlarmListener
    }

}
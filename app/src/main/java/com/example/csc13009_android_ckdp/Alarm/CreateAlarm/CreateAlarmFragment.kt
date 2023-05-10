package com.example.csc13009_android_ckdp.Alarm.CreateAlarm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.Alarm.ViewModel.AlarmViewModel
import com.example.csc13009_android_ckdp.Message.ChatActivity
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentCreateAlarmBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.HashMap


class CreateAlarmFragment : Fragment() {

lateinit var binding: FragmentCreateAlarmBinding
lateinit var viewModel:AlarmViewModel
    val adapter= GroupAdapter<ViewHolder>()
companion object{
    var medicineContent=HashMap<String,String>()
}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCreateAlarmBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var alarm = Alarm()
        viewModel=ViewModelProvider(this).get(AlarmViewModel::class.java)
        binding.repeatGroup.addOnButtonCheckedListener(MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                binding.monBTN.id -> alarm.mon=isChecked
                binding.tueBTN.id -> alarm.tue=isChecked
                binding.wedBTN.id -> alarm.wed=isChecked
                binding.thuBTN.id -> alarm.thu=isChecked
                binding.friBTN.id -> alarm.fri=isChecked
                binding.satBTN.id -> alarm.sat=isChecked
                binding.sunBTN.id -> alarm.sun=isChecked
            }
        })
binding.cancelAlarmBTN.setOnClickListener{
    Navigation.findNavController(binding.createAlarmBTN).navigate(R.id.action_createAlarmFragment_to_alarmListFragment)
}
        binding.createAlarmBTN.setOnClickListener{
            alarm.hour=TimePickerUtil.getHour(binding.timePicker)
            alarm.minute=TimePickerUtil.getMinute(binding.timePicker)

//            val c = Calendar.getInstance()
//            var date=c.get(Calendar.DAY_OF_WEEK)
//            val hour = c.get(Calendar.HOUR_OF_DAY)
//            val minute = c.get(Calendar.MINUTE)
//
//            if(alarm.hour<hour|| (alarm.hour==hour && alarm.minute<minute)){
//               date+=1
//                Log.d("DATE",date.toString())
//            }

            var content:String=""
            medicineContent.forEach{
                content+= it.key+":"+it.value+"\n"
            }
            alarm.content=content

            alarm.schedule(requireContext())

            viewModel.insert(alarm)
            Navigation.findNavController(binding.createAlarmBTN).navigate(R.id.action_createAlarmFragment_to_alarmListFragment)

            medicineContent.clear()
        }

        binding.medicinesRV.adapter=adapter
binding.medicinesRV.addItemDecoration( DividerItemDecoration(
    context,
    LinearLayoutManager.HORIZONTAL
)
)

binding.addMedicineBtn.setOnClickListener {
    adapter.add(NewMedicineRow())
    adapter.notifyDataSetChanged()
}

    }
    class NewMedicineRow(): Item<ViewHolder>() {
        lateinit var editText:EditText
        lateinit var quantity:NumberPicker
        override fun bind(viewHolder: ViewHolder, position: Int) {
editText=viewHolder.itemView.findViewById(R.id.medicineET)
quantity=viewHolder.itemView.findViewById(R.id.quantityNumber)
quantity.minValue=0
            quantity.maxValue=100
            quantity.wrapSelectorWheel=true
editText.addTextChangedListener(object : TextWatcher{
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        CreateAlarmFragment.medicineContent.remove(editText.text.toString())
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        CreateAlarmFragment.medicineContent[editText.text.toString()]=quantity.value.toString()
    }
})
            quantity.setOnValueChangedListener{picker,oldVal,newVal->
                CreateAlarmFragment.medicineContent[editText.text.toString()]=quantity.value.toString()
            }
        }

        override fun getLayout(): Int {
            return R.layout.item_add_medicines
        }
    }
}
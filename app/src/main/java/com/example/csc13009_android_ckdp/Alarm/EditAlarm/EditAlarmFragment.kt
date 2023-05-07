package com.example.csc13009_android_ckdp.Alarm.EditAlarm

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csc13009_android_ckdp.Alarm.CreateAlarm.CreateAlarmFragment
import com.example.csc13009_android_ckdp.Alarm.CreateAlarm.TimePickerUtil
import com.example.csc13009_android_ckdp.Alarm.ViewModel.AlarmViewModel
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentEditAlarmBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder


class EditAlarmFragment : Fragment() {

private lateinit var binding: FragmentEditAlarmBinding
private val args:EditAlarmFragmentArgs by navArgs()
    private lateinit var viewModel: AlarmViewModel
    companion object{
        var medicineContent=HashMap<String,String>()
    }
    val adapter= GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentEditAlarmBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(AlarmViewModel::class.java)

        TimePickerUtil.setHour(binding.timePicker,args.Alarm.hour)
        TimePickerUtil.setMinute(binding.timePicker,args.Alarm.minute)

        if(args.Alarm.mon){
            binding.repeatGroup.check(R.id.monBTN)
        }
        if(args.Alarm.tue){
            binding.repeatGroup.check(R.id.tueBTN)
        }
        if(args.Alarm.wed){
            binding.repeatGroup.check(R.id.wedBTN)
        }
        if(args.Alarm.thu){
            binding.repeatGroup.check(R.id.thuBTN)
        }
        if(args.Alarm.fri){
            binding.repeatGroup.check(R.id.friBTN)
        }
        if(args.Alarm.sat){
            binding.repeatGroup.check(R.id.satBTN)
        }
        if(args.Alarm.sun){
            binding.repeatGroup.check(R.id.sunBTN)
        }

        binding.repeatGroup.addOnButtonCheckedListener(MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                binding.monBTN.id -> args.Alarm.mon=isChecked
                binding.tueBTN.id -> args.Alarm.tue=isChecked
                binding.wedBTN.id -> args.Alarm.wed=isChecked
                binding.thuBTN.id -> args.Alarm.thu=isChecked
                binding.friBTN.id -> args.Alarm.fri=isChecked
                binding.satBTN.id -> args.Alarm.sat=isChecked
                binding.sunBTN.id -> args.Alarm.sun=isChecked
            }
        })

        binding.saveAlarmBTN.setOnClickListener {
            findNavController().navigate(R.id.action_editAlarmFragment_to_alarmListFragment)
            this.onDestroy()
        }
        binding.cancelAlarmBTN.setOnClickListener {
            findNavController().navigate(R.id.action_editAlarmFragment_to_alarmListFragment)
        }
        binding.medicinesRV.adapter=adapter
        binding.medicinesRV.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.HORIZONTAL
        )
        )

        binding.addMedicineBtn.setOnClickListener {
            adapter.add(EditAlarmFragment.NewMedicineRow())
            adapter.notifyDataSetChanged()
        }
        var CONTENT=args.Alarm.content.toString().split("\n")
    CONTENT.forEach{
        if(it!=""){
            var temp=it.split(":")
            medicineContent[temp[0]]=temp[1]

            var viewHolder=EditAlarmFragment.NewMedicineRow()
            viewHolder.name=temp[0]
            viewHolder.quantityVal=temp[1].toInt()
            Log.d("TEST",viewHolder.quantityVal.toString())
            adapter.add(viewHolder)
        }
}
    }

    override fun onDestroy() {
        if(args.Alarm.start){
            args.Alarm.cancel(requireContext())
        }
        args.Alarm.hour=TimePickerUtil.getHour(binding.timePicker)
        args.Alarm.minute=TimePickerUtil.getMinute(binding.timePicker)
        var content:String=""
        EditAlarmFragment.medicineContent.forEach{
            content+= it.key+":"+it.value+"\n"
        }
        args.Alarm.content=content
        args.Alarm.schedule(requireContext())
        viewModel.update(alarm=args.Alarm)
        super.onDestroy()
    }

    class NewMedicineRow(): Item<ViewHolder>() {
        lateinit var editText: EditText
        lateinit var quantity: NumberPicker
        public var name:String =""
        public var quantityVal:Int = 0
        override fun bind(viewHolder: ViewHolder, position: Int) {

            editText=viewHolder.itemView.findViewById(R.id.medicineET)
            quantity=viewHolder.itemView.findViewById(R.id.quantityNumber)
            if(name!=""){
                editText.setText(name)
            }
            quantity.minValue=0
            quantity.maxValue=100
            quantity.wrapSelectorWheel=true
            quantity.value=quantityVal

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    EditAlarmFragment.medicineContent.remove(editText.text.toString())
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    EditAlarmFragment.medicineContent[editText.text.toString()]=quantity.value.toString()
                }
            })
            quantity.setOnValueChangedListener{picker,oldVal,newVal->
                EditAlarmFragment.medicineContent[editText.text.toString()]=quantity.value.toString()
            }
        }

        override fun getLayout(): Int {
            return R.layout.item_add_medicines
        }
    }
}
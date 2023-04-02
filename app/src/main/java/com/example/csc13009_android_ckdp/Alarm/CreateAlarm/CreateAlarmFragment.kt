package com.example.csc13009_android_ckdp.Alarm.CreateAlarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.Alarm.ViewModel.AlarmViewModel
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentCreateAlarmBinding
import com.google.android.material.button.MaterialButtonToggleGroup


class CreateAlarmFragment : Fragment() {

lateinit var binding: FragmentCreateAlarmBinding
lateinit var viewModel:AlarmViewModel

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
            alarm.schedule(requireContext())
            viewModel.insert(alarm)
            Navigation.findNavController(binding.createAlarmBTN).navigate(R.id.action_createAlarmFragment_to_alarmListFragment)
        }
    }
}
package com.example.csc13009_android_ckdp.Alarm.EditAlarm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.csc13009_android_ckdp.Alarm.CreateAlarm.TimePickerUtil
import com.example.csc13009_android_ckdp.Alarm.ViewModel.AlarmViewModel
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentEditAlarmBinding
import com.google.android.material.button.MaterialButtonToggleGroup


class EditAlarmFragment : Fragment() {

private lateinit var binding: FragmentEditAlarmBinding
private val args:EditAlarmFragmentArgs by navArgs()
    private lateinit var viewModel: AlarmViewModel

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

    }

    override fun onDestroy() {
        if(args.Alarm.start){
            args.Alarm.cancel(requireContext())
        }
        args.Alarm.hour=TimePickerUtil.getHour(binding.timePicker)
        args.Alarm.minute=TimePickerUtil.getMinute(binding.timePicker)
args.Alarm.schedule(requireContext())
        viewModel.update(alarm=args.Alarm)
        super.onDestroy()
    }
}
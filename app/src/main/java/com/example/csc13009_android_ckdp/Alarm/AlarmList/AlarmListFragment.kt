package com.example.csc13009_android_ckdp.Alarm.AlarmList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentAlarmListBinding


class AlarmListFragment : Fragment() {

private lateinit var binding: FragmentAlarmListBinding
private lateinit var adapter:AdapterAlarm
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAlarmListBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterAlarm()

        binding.recyclerView2.adapter=adapter
        binding.recyclerView2.layoutManager=LinearLayoutManager(context)
    }


}
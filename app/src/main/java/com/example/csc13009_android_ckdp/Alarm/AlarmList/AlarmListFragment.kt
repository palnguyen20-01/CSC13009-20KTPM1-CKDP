package com.example.csc13009_android_ckdp.Alarm.AlarmList

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.Alarm.Service.AlarmService
import com.example.csc13009_android_ckdp.Alarm.ViewModel.AlarmViewModel
import com.example.csc13009_android_ckdp.MainActivity
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.FragmentAlarmListBinding


class AlarmListFragment : Fragment(),OnToggleListener,OnClickAlarmListener {

private lateinit var binding: FragmentAlarmListBinding
private lateinit var adapter:AdapterAlarm
private lateinit var  viewModel:AlarmViewModel
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            activity?.let {
                val intent = Intent(it,MainActivity::class.java)
                it.startActivity(intent)
            }
        }


    }
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
        viewModel= ViewModelProvider(this).get(AlarmViewModel::class.java)

        adapter = AdapterAlarm()
        adapter.addOnToggleListener(this)
        adapter.addOnClickAlarmListener(this)
        viewModel.list.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })



        binding.recyclerView2.adapter=adapter
        binding.recyclerView2.layoutManager=LinearLayoutManager(context)

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)!!


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                adapter.removeItem(viewHolder.adapterPosition, viewHolder,viewModel,requireContext())
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView2)

        binding.floatingActionButton.setOnClickListener{
            Navigation.findNavController(binding.floatingActionButton).navigate(R.id.action_alarmListFragment_to_createAlarmFragment)

//        val intent=Intent(context,AlarmService::class.java)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context?.startForegroundService(intent)
//            }else
//                context?.startService(intent)
        }
    }

    override fun onToggle(alarm: Alarm) {
        if(alarm.start){
            alarm.schedule(requireContext())
        }else{
            alarm.cancel(requireContext())
        }
    }

    override fun onClick(alarm: Alarm) {
        val action = AlarmListFragmentDirections.actionAlarmListFragmentToEditAlarmFragment(alarm)
        this.findNavController().navigate(action)
    }

    override fun onLongClick(alarm: Alarm) {
//       val dialog=AlertDialog.Builder(requireContext())
//           .setTitle("Delete Item")
//           .setMessage("Do you want delete ?")
//           .setNegativeButton("No"){
//               dialog,which-> dialog.dismiss()
//           }
//           .setPositiveButton("Yes"){dialog,which ->
//               if (alarm.start)
//               {
//                   alarm.cancel(requireContext())
//               }
//               viewModel.delete(alarm)
//           }
//dialog.show()
    }


}
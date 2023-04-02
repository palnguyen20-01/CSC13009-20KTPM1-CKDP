package com.example.csc13009_android_ckdp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.csc13009_android_ckdp.databinding.ActivityAlarmBinding


class AlarmActivity : AppCompatActivity() {

   private lateinit var binding:ActivityAlarmBinding
private lateinit var navController:NavController
private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAlarmBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        navHostFragment=supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController=navHostFragment.findNavController()

        setupActionBarWithNavController(navController)



    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
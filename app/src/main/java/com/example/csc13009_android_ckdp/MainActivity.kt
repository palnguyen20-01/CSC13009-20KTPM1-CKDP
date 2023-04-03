package com.example.csc13009_android_ckdp

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    var selectedFragment: Fragment? = null
    var actionBar: ActionBar? = null
    var bottomNavigationView: BottomNavigationView? = null


    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        selectedFragment = HomeFragment(this)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder, selectedFragment!!)
            .commit()
        initiateApp()
    }

    private fun initiateApp()
    {
        actionBar = supportActionBar
        actionBar!!.setDisplayUseLogoEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView?.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId

            if (R.id.itemHome === itemId) {
                selectedFragment = HomeFragment(this)
            } else if (R.id.itemMessage=== itemId) {
                //selectedFragment = Fragment
            } else if (R.id.itemSetting === itemId) {
                selectedFragment = SettingFragment(this)
            }
            // Use addToBackStack to return the previous fragment when the Back button is pressed
            // Checking null was just a precaution
            if (selectedFragment != null) supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentHolder, selectedFragment!!)
                .commit()
            true
        })
    }
}
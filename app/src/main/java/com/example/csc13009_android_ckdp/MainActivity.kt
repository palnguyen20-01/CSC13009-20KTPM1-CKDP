package com.example.csc13009_android_ckdp


import android.app.Activity
import android.content.ClipData.Item
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.csc13009_android_ckdp.FindFriend.FindFriendActivity
import androidx.preference.PreferenceManager
import com.example.csc13009_android_ckdp.Notification.NotificationFragment
import com.example.csc13009_android_ckdp.Notification.NotificationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import java.util.*

class MainActivity : AppCompatActivity() {
    var selectedFragment: Fragment? = null
    var actionBar: ActionBar? = null
    var bottomNavigationView: BottomNavigationView? = null


    public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appSetting()

        setContentView(R.layout.activity_home)

        selectedFragment = HomeFragment()
        (selectedFragment as HomeFragment).main = this
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder, selectedFragment!!)
            .commit()
        initiateApp()

        val notificationService = NotificationService()
        notificationService.checkStartNoti(this)
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }

    private fun appSetting() {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(this)
        val mode = prefs.getBoolean("switch", false)
        if (mode) {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
        } else {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
        }

        val language = prefs?.getString("language", "1")

        when (language?.toInt()) {
            1 -> {
                setLocale(this, "en")
            }

            2 -> {
                setLocale(this, "vi")
            }
        }

    }

    private fun initiateApp() {
        actionBar = supportActionBar
        actionBar!!.setDisplayUseLogoEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView?.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId

            if (R.id.itemHome === itemId) {
                selectedFragment = HomeFragment()
                (selectedFragment as HomeFragment).main = this
            } else if (R.id.itemMessage === itemId) {
                //selectedFragment = Fragment
            } else if (R.id.itemSetting === itemId) {
                selectedFragment = SettingFragment()
                (selectedFragment as SettingFragment).main = this

            }else if (R.id.itemNotice == itemId) {
                selectedFragment = NotificationFragment()
                (selectedFragment as NotificationFragment).main = this
            }else if (R.id.itemFriendRequest == itemId) {
                startActivity(Intent(applicationContext, FindFriendActivity::class.java))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(
                    this,
                    SettingsActivity::class.java
                )
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
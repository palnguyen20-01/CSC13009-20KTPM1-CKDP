package com.example.csc13009_android_ckdp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.csc13009_android_ckdp.Profile.InfoChange
import com.example.csc13009_android_ckdp.Profile.PasswordChange

class SettingFragment : Fragment() {

    private var main: MainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = try {
            activity as MainActivity?
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MainActivity must implement callbacks")
        }
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        (activity as MainActivity).supportActionBar!!.setDisplayUseLogoEnabled(true)
        (activity as MainActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingsFragment: View = inflater.inflate(R.layout.fragment_setting, container, false)
        var lin =  settingsFragment.findViewById<ScrollView>(R.id.profileScrollView);

        val card_pass = settingsFragment.findViewById<CardView>(R.id.card_password);
        val card_info = settingsFragment.findViewById<CardView>(R.id.card_info);
        val card_email = settingsFragment.findViewById<CardView>(R.id.card_email);
        val card_phone = settingsFragment.findViewById<CardView>(R.id.card_phone);

        card_pass.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivity(intent)
        }
        card_info.setOnClickListener {
            val intent = Intent(context, InfoChange::class.java)
            startActivity(intent)
        }
        card_email.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivity(intent)
        }
        card_phone.setOnClickListener {
            val intent = Intent(context, PasswordChange::class.java)
            startActivity(intent)
        }



        return settingsFragment
    }

}
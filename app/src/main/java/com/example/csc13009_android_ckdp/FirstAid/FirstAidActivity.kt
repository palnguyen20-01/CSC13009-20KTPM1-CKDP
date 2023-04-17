package com.example.csc13009_android_ckdp.FirstAid

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R

class FirstAidActivity : AppCompatActivity() {
    lateinit var firstAidGridView : RecyclerView
    lateinit var firstAids : ArrayList<FirstAid>
    lateinit var adapter : FirstAidAdapter

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        androidx.appcompat.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)

        firstAidGridView = findViewById(R.id.firstAidRCV)
        firstAids = ArrayList<FirstAid>()
        firstAids.add(FirstAid(R.string.title_firstaid, R.drawable.first_aid, R.string.firstaid))
        firstAids.add(FirstAid(R.string.title_anaphylaxis, R.drawable.anaphylaxis, R.string.anaphylaxis))
        firstAids.add(FirstAid(R.string.title_animalbite, R.drawable.animal_bite, R.string.animalbite))
        firstAids.add(FirstAid(R.string.title_brokenbone, R.drawable.broken_bone, R.string.brokenbone))
        firstAids.add(FirstAid(R.string.title_burn, R.drawable.burn, R.string.burn))
        firstAids.add(FirstAid(R.string.title_choking, R.drawable.choking, R.string.choking))
        firstAids.add(FirstAid(R.string.title_cut, R.drawable.cut, R.string.cut))
        firstAids.add(FirstAid(R.string.title_drowning, R.drawable.drowning, R.string.drowning))
        firstAids.add(FirstAid(R.string.title_electricshock, R.drawable.electric_shock, R.string.electricshock))
        firstAids.add(FirstAid(R.string.title_emergency, R.drawable.emergency, R.string.emergency))
        firstAids.add(FirstAid(R.string.title_heartattack, R.drawable.heart_attack, R.string.heartattack))
        firstAids.add(FirstAid(R.string.title_nosebleeding, R.drawable.nose_bleeding, R.string.nosebleeding))
        firstAids.add(FirstAid(R.string.title_poisonousplant, R.drawable.poisonous_plant, R.string.poisonousplant))
        firstAids.add(FirstAid(R.string.title_sprain, R.drawable.sprain, R.string.sprain))
        firstAids.add(FirstAid(R.string.title_stroke, R.drawable.stroke, R.string.stroke))
        firstAids.add(FirstAid(R.string.title_thorns, R.drawable.thorns, R.string.thorns))

        adapter = FirstAidAdapter(firstAids)
        firstAidGridView.adapter = adapter

        adapter.onItemClick = {firstaid, i->
            val intent = Intent(this, FirstAidInfo::class.java)
            intent.putExtra("name",firstaid.name.toString())
            intent.putExtra("image",firstaid.imageSource.toString())
            intent.putExtra("code",firstaid.code.toString())
            startActivity(intent)
        }

        firstAidGridView.layoutManager = GridLayoutManager(this,3)

        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        firstAidGridView.addItemDecoration(itemDecoration)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.title_activity_first_aid)
    }
}
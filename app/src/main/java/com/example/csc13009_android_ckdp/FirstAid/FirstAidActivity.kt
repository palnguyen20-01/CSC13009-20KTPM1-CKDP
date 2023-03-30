package com.example.csc13009_android_ckdp.FirstAid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R

class FirstAidActivity : AppCompatActivity() {
    lateinit var firstAidGridView : RecyclerView
    lateinit var firstAids : ArrayList<FirstAid>
    lateinit var adapter : FirstAidAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)

        firstAidGridView = findViewById(R.id.firstAidRCV)
        firstAids = ArrayList<FirstAid>()
        firstAids.add(FirstAid("First Aid", R.drawable.first_aid, R.string.firstaid))
        firstAids.add(FirstAid("Anaphulaxis", R.drawable.anaphylaxis, R.string.anaphylaxis))
        firstAids.add(FirstAid("Animal Bites", R.drawable.animal_bite, R.string.animalbite))
        firstAids.add(FirstAid("Broken Bone", R.drawable.broken_bone, R.string.brokenbone))
        firstAids.add(FirstAid("Burn", R.drawable.burn, R.string.burn))
        firstAids.add(FirstAid("Choking", R.drawable.choking, R.string.choking))
        firstAids.add(FirstAid("Cut", R.drawable.cut, R.string.cut))
        firstAids.add(FirstAid("Drowning", R.drawable.drowning, R.string.drowning))
        firstAids.add(FirstAid("Electric Shock", R.drawable.electric_shock, R.string.electricshock))
        firstAids.add(FirstAid("Emergency", R.drawable.emergency, R.string.emergency))
        firstAids.add(FirstAid("Heart Attack", R.drawable.heart_attack, R.string.heartattack))
        firstAids.add(FirstAid("Nose Bleed", R.drawable.nose_bleeding, R.string.nosebleeding))
        firstAids.add(FirstAid("Poisonous Plant", R.drawable.poisonous_plant, R.string.poisonousplant))
        firstAids.add(FirstAid("Sprain", R.drawable.sprain, R.string.sprain))
        firstAids.add(FirstAid("Stroke", R.drawable.stroke, R.string.stroke))
        firstAids.add(FirstAid("Thorns", R.drawable.thorns, R.string.thorns))

        adapter = FirstAidAdapter(firstAids)
        firstAidGridView.adapter = adapter

        adapter.onItemClick = {firstaid, i->
            val intent = Intent(this, FirstAidInfo::class.java)
            intent.putExtra("name",firstaid.name)
            intent.putExtra("image",firstaid.imageSource.toString())
            intent.putExtra("code",firstaid.code.toString())
            startActivity(intent)
        }

        firstAidGridView.layoutManager = GridLayoutManager(this,3)

        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        firstAidGridView.addItemDecoration(itemDecoration)
    }
}
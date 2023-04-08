package com.example.csc13009_android_ckdp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.adapter.HomeAdapter

class HomeFragment : Fragment {
    private var main: MainActivity? = null
    private var featureNames = arrayOf<String>("First Aid", "BMI", "Alarm", "Orange")
    private var featureImageIds = arrayOf<Int>(R.drawable.first_aid, R.drawable.bmi,R.drawable.ic_home, R.drawable.ic_home)

    constructor(main: MainActivity?) : super() {
        this.main = main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingsFragment: View = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = settingsFragment.findViewById<View>(R.id.recyclerViewHome) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(main, 2)
        recyclerView.layoutManager = layoutManager
        val featArrayList: ArrayList<MyFeatures> = prepareData()
        val adapter = main?.let { HomeAdapter(it, featArrayList) }
        recyclerView.adapter = adapter
        return settingsFragment
    }

    private fun prepareData(): ArrayList<MyFeatures> {
        val featureArrayList: ArrayList<MyFeatures> = ArrayList()
        for (i in featureNames.indices) {
            val feats = MyFeatures()
            feats.setFeatureName(featureNames[i])
            feats.setImageId(featureImageIds[i])
            featureArrayList.add(feats)
        }
        return featureArrayList
    }

}
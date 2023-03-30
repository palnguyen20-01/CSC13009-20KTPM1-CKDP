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
        val settingsFragment: View = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = settingsFragment.findViewById<View>(R.id.recyclerViewHome) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(main, 2)
        recyclerView.layoutManager = layoutManager
        val newspaperArrayList: ArrayList<MyFeatures> = prepareData()
        val adapter = main?.let { HomeAdapter(it, newspaperArrayList) }
        recyclerView.adapter = adapter
        return settingsFragment
    }

    private fun prepareData(): ArrayList<MyFeatures> {
        val featureArrayList: ArrayList<MyFeatures> = ArrayList()
        for (i in featureNames.indices) {
            val newspaper = MyFeatures()
            newspaper.setNewspaperName(featureNames[i])
            newspaper.setImageId(featureImageIds[i])
            featureArrayList.add(newspaper)
        }
        return featureArrayList
    }

}
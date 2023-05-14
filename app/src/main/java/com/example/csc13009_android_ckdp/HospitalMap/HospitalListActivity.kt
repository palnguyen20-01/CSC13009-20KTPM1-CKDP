package com.example.csc13009_android_ckdp.HospitalMap

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalListActivity : AppCompatActivity(), LocationListener{
    lateinit var hospitalListView : RecyclerView
    lateinit var hospitals : ArrayList<Hospital>
    lateinit var adapter : HospitalListAdapter
    private lateinit var locationManager: LocationManager
    private val REQUEST_LOCATION_PERMISSION = 1
    private var oldPostion : LatLng = LatLng(0.0,0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_list)

        enableMyLocation()

        hospitalListView = findViewById(R.id.hospitalListRCV)
        hospitals = ArrayList<Hospital>()
        adapter = HospitalListAdapter(hospitals)
        hospitalListView.adapter = adapter

        adapter.onItemClick = {hospital, i->
            val intent = Intent(this, HospitalMapActivity::class.java)
            intent.putExtra("type","hospital")
            intent.putExtra("name",hospital.name)
            intent.putExtra("address",hospital.address)
            intent.putExtra("lat",hospital.lat.toString())
            intent.putExtra("lng",hospital.lng.toString())
            startActivity(intent)
        }

        hospitalListView.layoutManager = LinearLayoutManager(this)

        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        hospitalListView.addItemDecoration(itemDecoration)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.title_activity_hospital_map)
    }

    fun getHospitalList(){
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog.show()

        val lat : String = oldPostion.latitude.toString()
        val lng : String = oldPostion.longitude.toString()
        val latlng : String = lat + "," + lng
        val hospitalAPI_Interface = Hospital_API_Interface.create().getResouces(
            "98fd21346d83bee24dc734231f7609c9",
            latlng,
            "3000",
            "",
            "hospital",
            "",
            ""
        )
        hospitalAPI_Interface.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                dialog.dismiss()
                if(response?.body() != null){
                    var s = response.body()!!.string()
                    Log.i("phuc4570", oldPostion.toString())
                    val responseJSONObject = JSONObject(s)
                    val responseJSONArray = responseJSONObject.getJSONArray("result")
                    for (i in 0 .. responseJSONArray.length() - 1) {
                        val obj = responseJSONArray.getJSONObject(i)
                        val name = obj.get("name").toString()
                        val address = obj.get("address").toString()
                        val location = obj.getJSONObject("location")
                        val lat = location.get("lat").toString().toDouble()
                        val lng = location.get("lng").toString().toDouble()
                        val destination_latlng : String = lat.toString() + "," + lng.toString()
                        val distanceAPI_Interface = Distance_API_Interface.create().getResouces(
                            "98fd21346d83bee24dc734231f7609c9",
                            latlng,
                            destination_latlng,
                            "motorcycle"
                        )
                        distanceAPI_Interface.enqueue(object: Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if(response?.body() != null){
                                    var res = response.body()!!.string()
                                    val resJSONObject = JSONObject(res).getJSONObject("result").getJSONArray("routeRows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance")
                                    val distanceInText = resJSONObject.get("text").toString()
                                    val distanceInDouble = resJSONObject.get("value").toString().toDouble()
                                    val hospital = Hospital(name, address, lat, lng, distanceInText, distanceInDouble)
                                    hospitals.add(hospital)
                                    hospitals.sortBy { i -> i.distanceInDouble }
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.i("phuc4570","onFailure")
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("phuc4570","onFailure")
            }
        })

    }

    override fun onLocationChanged(location: Location) {
        oldPostion = LatLng(location.latitude, location.longitude)
        Log.i("phuc4570", "location change")
        getHospitalList()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("phuc4570","enableLocation")
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val providers: List<String> = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            if(location == null){
                location = bestLocation!!
            }
            Log.i("phuc4570", "getLocation")
            oldPostion = LatLng(location.latitude, location.longitude)
            getHospitalList()
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}
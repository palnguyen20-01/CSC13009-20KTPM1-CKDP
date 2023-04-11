package com.example.csc13009_android_ckdp

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.DrugInfo.DrugAPI
import com.example.csc13009_android_ckdp.DrugInfo.DrugAdapter
import com.example.csc13009_android_ckdp.DrugInfo.DrugModel
import com.example.csc13009_android_ckdp.SkinDiaseaseAPI.SkinDiseaseAdapter
import com.example.csc13009_android_ckdp.SkinDiaseaseAPI.SkinDiseaseModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class DrugInfoActivity : AppCompatActivity() {
    val DrugInfoAPI = DrugAPI()

    lateinit var tvTitle: TextView
    lateinit var tvDrugInteractionsDetails: TextView
    lateinit var tvIndicationsDetails: TextView
    lateinit var tvDosageDetails: TextView

    lateinit var ETNameofDrug: EditText

    lateinit var BTNSearch: ImageButton
    lateinit var context: Context
    lateinit var DrugUIDB: DrugModel
    lateinit var DrugHistory: ArrayList<DrugModel>

    fun getInfo(nameDrug: String) {
        runOnUiThread {
            tvDrugInteractionsDetails.setText("Waitting...")
            tvIndicationsDetails.setText("Waitting...")
            tvDosageDetails.setText("Waitting...")
        }
        DrugInfoAPI.searchDrug(nameDrug, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string())
                if (json == null) {
                    Toast.makeText(context, "No Response From API", Toast.LENGTH_LONG).show()
                }
                val humanReadableOutput = DrugInfoAPI.processJsonResponse(json)
                runOnUiThread {
                    val (indications, dosage, interactions) = humanReadableOutput
                    tvDrugInteractionsDetails.setText(indications)
                    tvIndicationsDetails.setText(dosage)
                    tvDosageDetails.setText(interactions)

                    DrugUIDB.indications = indications
                    DrugUIDB.dosage = dosage
                    DrugUIDB.interactions = interactions

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Xử lý lỗi khi gửi yêu cầu API
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_info2)
        context = this
        supportActionBar?.hide()

        //todo use DAO load database

        DrugHistory = ArrayList<DrugModel>()
        DrugHistory.add(DrugModel("1", "1", "1", "1", "1"))
        DrugUIDB = DrugHistory[0]
        DrugHistory.add(DrugModel("2", "2", "2", "2", "2"))
        DrugHistory.add(DrugModel("3", "3", "3", "3", "3"))
        DrugHistory.add(DrugModel("4", "4", "4", "4", "4"))

        //end todo

        tvTitle = findViewById(R.id.tvTitle)
        tvDrugInteractionsDetails = findViewById(R.id.tvDrugInteractionsDetails)
        tvIndicationsDetails = findViewById(R.id.tvIndicationsDetails)
        tvDosageDetails = findViewById(R.id.tvDosageDetails)


        ETNameofDrug = findViewById(R.id.ETNameofDrug)
        BTNSearch = findViewById(R.id.BTNSearch)

        BTNSearch.setOnClickListener {
            val nameDrug: String = ETNameofDrug.text.toString()

            DrugUIDB.Name = nameDrug
            tvTitle.setText(nameDrug)

            getInfo(nameDrug)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val chatboxList: RecyclerView = navigationView.findViewById(R.id.RVChatBox)


        val DrugAdapter = DrugAdapter(
            this, DrugHistory, tvTitle, tvDrugInteractionsDetails,
            tvIndicationsDetails, tvDosageDetails,
            DrugUIDB
        )
        chatboxList.adapter = DrugAdapter
        chatboxList.layoutManager = LinearLayoutManager(this)

        DrugAdapter.onItemClick = { ChatBox ->
            DrugUIDB=ChatBox
            tvTitle.setText(DrugUIDB.Name)
            tvDrugInteractionsDetails.setText(DrugUIDB.interactions)
            tvIndicationsDetails.setText(DrugUIDB.indications)
            tvDosageDetails.setText(DrugUIDB.dosage)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        val addButton: ImageButton = findViewById(R.id.IMGBTNAdd)

        addButton.setOnClickListener {
            val editText = EditText(this)
            val dialog = AlertDialog.Builder(this)
                .setTitle("Add new Chatbox")
                .setMessage("Enter a title for the new ChatBox")
                .setView(editText)
                .setPositiveButton("Add") { dialog, _ ->
                    val title = editText.text.toString()
                    // TODO: Add the new item to your list
                    val tempModel=DrugModel(title)
                    DrugUIDB=tempModel
                    DrugHistory.add(tempModel)

                    DrugAdapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()

            dialog.show()
        }

    }

}
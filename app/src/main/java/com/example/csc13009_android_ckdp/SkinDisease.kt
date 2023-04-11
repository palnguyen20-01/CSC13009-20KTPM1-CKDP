package com.example.csc13009_android_ckdp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.SkinDiaseaseAPI.SkinDiseaseAPI
import com.example.csc13009_android_ckdp.SkinDiaseaseAPI.SkinDiseaseAdapter
import com.example.csc13009_android_ckdp.SkinDiaseaseAPI.SkinDiseaseModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class SkinDisease : AppCompatActivity() {
    lateinit var IBDiseaseSkin: ImageButton
    lateinit var TVDiagnose: TextView
    val REQUEST_IMAGE_GET=111
    val SkinDiseaseAPI= SkinDiseaseAPI()
    private lateinit var context: Context
    lateinit var SkinModelUIAndDB: SkinDiseaseModel
    lateinit var SkinDiseaseHistory : ArrayList<SkinDiseaseModel>

    fun formatDiseaseProbabilities(diseaseProbabilities: JSONObject,BodyPart:String,topK:Int): String {
        val map = diseaseProbabilities.keys().asSequence().associate { it to diseaseProbabilities.getDouble(it) }
        val topDiseases = map.entries.sortedByDescending { it.value }.associate { it.toPair() }
        val topDiseaseNames = topDiseases.keys.toList().take(topK)
        val topDiseaseProbabilities = topDiseases.values.toList().take(topK)

        val result = StringBuilder()
        for (i in topDiseaseNames.indices) {
            val diseaseName = topDiseaseNames[i]
            val probability = topDiseaseProbabilities[i] * 100
            if (probability >= 20) {
                result.append(String.format("%.0f%% chance of getting %s", probability, diseaseName))
                if (i < topDiseaseNames.size - 1) {
                    result.append(", ")
                }
            }
        }
        return "Your %s have a %s".format(BodyPart,result.toString())
    }
    fun getDiagnosefromAPI(imageBytes:ByteArray){
        SkinDiseaseAPI.SendtoAPI(imageBytes, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                val errorMessage = e.message ?: "An unknown error"
                runOnUiThread {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                // Handle response
                val json = JSONObject(response.body?.string())
                Log.d("ResponeFromAPIDiseaseSkin", json.toString())
                var ErrorFromAPI=json.getString("error_msg")
                if (ErrorFromAPI!=""){
                    runOnUiThread {
                        TVDiagnose.setText(ErrorFromAPI)
                        SkinModelUIAndDB.diagnose=ErrorFromAPI
                    }
                }
                else{
                    var PredictFromAPI=json.getJSONObject("data")
                    var BodyPart=PredictFromAPI.getString("body_part")
                    val results = PredictFromAPI.getJSONObject("results_english")
                    var resultToUI:String=formatDiseaseProbabilities(results,BodyPart,3)
                    runOnUiThread {
                        TVDiagnose.setText(resultToUI)
                        SkinModelUIAndDB.diagnose=resultToUI
                    }
                }

            }
        })

    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skin_disease)
        context=this
        supportActionBar?.hide()
        //todo use DAO load database

        SkinDiseaseHistory= ArrayList<SkinDiseaseModel>()
        var imageBytes: ByteArray = byteArrayOf(-1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 0, 0, 72, 0, 72, 0, 0, -1, -31, 0, 76, 69, 120, 105, 102, 0, 0, 77, 77, 0, 42, 0, 0, 0, 8, 0, 2, 1, 18, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, -121, 105, 0, 4, 0, 0, 0, 1, 0, 0, 0, 38, 0, 0, 0, 0, 0, 2, -96, 2, 0, 4, 0, 0, 0, 1, 0, 0, 7, -128, -96, 3, 0, 4, 0, 0, 0, 1, 0, 0, 10, 0, 0, 0, 0, 0, -1, -19, 0, 56, 80, 104, 111, 116, 111, 115, 104, 111, 112, 32, 51, 46, 48, 0, 56, 66, 73, 77, 4, 4, 0, 0, 0, 0, 0, 0, 56, 66, 73, 77, 4, 37, 0, 0, 0, 0, 0, 16, -44, 29, -116, -39, -113, 0, -78, 4, -23, -128, 9, -104, -20, -8, 66, 126, -1, -30, 2, 40, 73, 67, 67, 95, 80, 82, 79, 70, 73, 76, 69, 0, 1, 1, 0, 0, 2, 24, 97, 112, 112, 108, 4, 0, 0, 0, 109, 110, 116, 114, 82, 71, 66, 32, 88, 89, 90, 32, 7, -26, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 97, 99, 115, 112, 65, 80, 80, 76, 0, 0, 0, 0, 65, 80, 80, 76, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -10, -42, 0, 1, 0, 0, 0, 0, -45, 45, 97, 112, 112, 108, -20, -3, -93, -114, 56, -123, 71, -61, 109, -76, -67, 79, 122, -38, 24, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 100, 101, 115, 99, 0, 0, 0, -4, 0, 0, 0, 48, 99, 112, 114, 116, 0, 0, 1, 44, 0, 0, 0, 80, 119, 116, 112, 116, 0, 0, 1, 124, 0, 0, 0, 20, 114, 88, 89, 90, 0, 0, 1, -112, 0, 0, 0, 20, 103, 88, 89, 90, 0, 0, 1, -92, 0, 0, 0, 20, 98, 88, 89, 90, 0, 0, 1, -72, 0, 0, 0, 20, 114, 84, 82, 67, 0, 0, 1, -52, 0, 0, 0, 32, 99, 104, 97, 100, 0, 0, 1, -20, 0, 0, 0, 44, 98, 84, 82, 67, 0, 0, 1, -52, 0, 0, 0, 32, 103, 84, 82, 67, 0, 0, 1, -52, 0, 0, 0, 32, 109, 108, 117, 99, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 12, 101, 110, 85, 83, 0, 0, 0, 20, 0, 0, 0, 28, 0, 68, 0, 105, 0, 115, 0, 112, 0, 108, 0, 97, 0, 121, 0, 32, 0, 80, 0, 51, 109, 108, 117, 99, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 12, 101, 110, 85, 83, 0, 0, 0, 52, 0, 0, 0, 28, 0, 67, 0, 111, 0, 112, 0, 121, 0, 114, 0, 105, 0, 103, 0, 104, 0, 116, 0, 32, 0, 65, 0, 112, 0, 112, 0, 108, 0, 101, 0, 32, 0, 73, 0, 110, 0, 99, 0, 46, 0, 44, 0, 32, 0, 50, 0, 48, 0, 50, 0, 50, 88, 89, 90, 32, 0, 0, 0, 0, 0, 0, -10, -43, 0, 1, 0, 0, 0, 0, -45, 44, 88, 89, 90, 32, 0, 0, 0, 0, 0, 0, -125, -33, 0, 0, 61, -65, -1, -1, -1, -69, 88, 89, 90, 32, 0, 0, 0, 0, 0, 0, 74, -65, 0, 0, -79, 55, 0, 0, 10, -71, 88, 89, 90, 32, 0, 0, 0, 0, 0, 0, 40, 56, 0, 0, 17, 11, 0, 0, -56, -71, 112, 97, 114, 97, 0, 0, 0, 0, 0, 3, 0, 0, 0, 2, 102, 102, 0, 0, -14, -89, 0, 0, 13, 89, 0, 0, 19, -48, 0, 0, 10, 91, 115, 102, 51, 50, 0, 0, 0, 0, 0, 1, 12, 66, 0, 0, 5, -34, -1, -1, -13, 38, 0, 0, 7, -109, 0, 0, -3, -112, -1, -1, -5, -94, -1, -1, -3, -93, 0, 0, 3, -36, 0, 0, -64, 110, -1, -64, 0, 17, 8, 10, 0, 7, -128, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72)
        SkinDiseaseHistory.add(SkinDiseaseModel("sui mao ga",imageBytes,"sui mao ga 90%"))
        SkinModelUIAndDB=SkinDiseaseHistory[0]
        SkinDiseaseHistory.add(SkinDiseaseModel("Giang mai",imageBytes,"Giang Mai 100%"))
        SkinDiseaseHistory.add(SkinDiseaseModel("Me day",imageBytes,"Me day 30%"))
        SkinDiseaseHistory.add(SkinDiseaseModel("Mun",imageBytes,"Mun 20%"))

        //end todo

        IBDiseaseSkin=findViewById(R.id.IBDiseaseSkin)
        TVDiagnose=findViewById(R.id.TVDiagnose)



        val bitmap = BitmapFactory.decodeByteArray(SkinModelUIAndDB.imageBytes, 0,
                        SkinModelUIAndDB.imageBytes.size)
        IBDiseaseSkin.setImageBitmap(bitmap)
        TVDiagnose.setText(SkinModelUIAndDB.diagnose)

        //
        IBDiseaseSkin.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }



        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val chatboxList: RecyclerView = navigationView.findViewById(R.id.RVChatBox)


        val SkinDiseaseAdapter = SkinDiseaseAdapter(this, SkinDiseaseHistory,IBDiseaseSkin,TVDiagnose,
                                                    SkinModelUIAndDB)

        chatboxList.adapter = SkinDiseaseAdapter
        chatboxList.layoutManager = LinearLayoutManager(this)

        SkinDiseaseAdapter.onItemClick = { ChatBox ->
            SkinModelUIAndDB=ChatBox
            val bitmap = BitmapFactory.decodeByteArray(ChatBox.imageBytes, 0, ChatBox.imageBytes.size)
            IBDiseaseSkin.setImageBitmap(bitmap)
            TVDiagnose.setText(ChatBox.diagnose)
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
                    val tempModel=SkinDiseaseModel(title)
                    SkinModelUIAndDB=tempModel
                    SkinDiseaseHistory.add(tempModel)

                    SkinDiseaseAdapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()

            dialog.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { uri ->
                var imageBytes=getImageBytesFromURI(uri)
                getDiagnosefromAPI(imageBytes)
                SkinModelUIAndDB.imageBytes=imageBytes
//                Log.d("example Image Bytes", Arrays.toString(imageBytes))
                runOnUiThread {
                    IBDiseaseSkin.setImageURI(uri)
                }
                Log.d("exampleURI",uri.toString())

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun getImageBytesFromURI(uri: Uri): ByteArray {
        val inputStream: InputStream = contentResolver.openInputStream(uri)!!
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
        val bytes = outputStream.toByteArray()
        inputStream.close()
        outputStream.close()
        return bytes
    }

}
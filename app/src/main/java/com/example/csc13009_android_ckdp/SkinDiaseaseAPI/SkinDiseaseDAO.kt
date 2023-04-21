package com.example.csc13009_android_ckdp.SkinDiaseaseAPI
import android.util.Base64
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import okio.ByteString.Companion.encode

class SkinDiseaseDAO {
    fun save(database: FirebaseDatabase, id:String, ChatBoxes:ArrayList<SkinDiseaseModel>){
        val chatBoxMap = ArrayList<HashMap<String, String>>()
        for (chatBox in ChatBoxes) {
            val map = HashMap<String, String>()
            map["title"] = chatBox.title
            map["diagnose"] = chatBox.diagnose
            map["imageBytes"] = Base64.encodeToString(chatBox.imageBytes, Base64.DEFAULT)
            chatBoxMap.add(map)
        }
        database.reference.child("SkinDisease").child(id!!).setValue(chatBoxMap)
            .addOnSuccessListener {
                Log.d("firebase", "Got value")
            }.addOnFailureListener{
                Log.d("firebase", "Error getting data")
            }

    }
    fun loadSync(database: FirebaseDatabase, id: String, callback: (ArrayList<SkinDiseaseModel>) -> Unit) {
        val chatBoxes = ArrayList<SkinDiseaseModel>()
        database.reference.child("SkinDisease").child(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val dataSnapshot = task.result
                    val genericObject = dataSnapshot.getValue()
                    if (genericObject is ArrayList<*>) {
                        Log.d("FireBase:Task  Arraylist Drugs","hit")
                        for (chatBox in genericObject) {
                            if (chatBox is HashMap<*, *>) {
                                val title = chatBox["title"] as String
                                val diagnose = chatBox["diagnose"] as String
                                val imageBytesStr = chatBox["imageBytes"] as String

                                val imageBytes= Base64.decode(imageBytesStr, Base64.DEFAULT)
                                val chatBoxModel = SkinDiseaseModel(title,imageBytes,diagnose )
                                chatBoxes.add(chatBoxModel)
                            }
                        }
                    }

                    callback(chatBoxes) // Return the chatBoxes list via the callback
                } else {
                    Log.d("faild firebase", "Failed to read data from FirebaseDatabase")
                    callback(chatBoxes)
                }
            }
    }
}
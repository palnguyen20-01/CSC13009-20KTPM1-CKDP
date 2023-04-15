package com.example.csc13009_android_ckdp.DrugInfo
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class DrugDAO {

    fun save(database: FirebaseDatabase, id:String,ChatBoxes:ArrayList<DrugModel>){
        database.reference.child("DrugInfo").child(id!!).setValue(ChatBoxes)
            .addOnSuccessListener {
                Log.d("firebase", "Got value")
            }.addOnFailureListener{
                Log.d("firebase", "Error getting data")
            }
    }
    fun loadSync(database: FirebaseDatabase, id: String, callback: (ArrayList<DrugModel>) -> Unit) {
        val chatBoxes = ArrayList<DrugModel>()
        database.reference.child("DrugInfo").child(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val dataSnapshot = task.result
                    val genericObject = dataSnapshot.getValue()
                    if (genericObject is ArrayList<*>) {
                        Log.d("FireBase:Task  Arraylist Drugs","hit")
                        for (chatBox in genericObject) {
                            if (chatBox is HashMap<*, *>) {
                                val title = chatBox["title"] as String
                                val interactions = chatBox["interactions"] as String
                                val indications = chatBox["indications"] as String
                                val dosage = chatBox["dosage"] as String
                                val Name = chatBox["name"] as String
                                val chatBoxModel = DrugModel(title,interactions,indications,dosage,Name )
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
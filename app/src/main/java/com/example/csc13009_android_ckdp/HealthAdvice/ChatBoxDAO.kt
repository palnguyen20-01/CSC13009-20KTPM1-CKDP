package com.example.csc13009_android_ckdp.HealthAdvice

import android.util.Log
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ChatBoxDAO {

    fun save(database: FirebaseDatabase, id:String,ChatBoxes:ArrayList<ChatBoxModel>){
        database.reference.child("Health_Advice").child(id!!).setValue(ChatBoxes)
            .addOnSuccessListener {
                Log.d("firebase", "Got value")
            }.addOnFailureListener{
                Log.d("firebase", "Error getting data")
            }
    }

    fun load(database: FirebaseDatabase, id:String,chatBoxes:ArrayList<ChatBoxModel>){
        database.reference.child("Health_Advice").child(id!!).get()
            .addOnCompleteListener {task ->
                if(task.result.exists())
                {
                    Log.d("FireBase:Task  Exist","hit")
                    val dataSnapshot = task.result
                    val genericObject = dataSnapshot.getValue() // Read the data as a generic object
                    if (genericObject is ArrayList<*>) { // Check if the generic object is an ArrayList
                        Log.d("FireBase:Task  Arraylist Chatbox","hit")
                        for (chatBox in genericObject) { // Loop through the ArrayList
                            if (chatBox is HashMap<*, *>) { // Check if each item is a HashMap
                                val title = chatBox["title"] as String // Extract the title
                                Log.d("FireBase:Task  ItemHashMap Chatbox","hit")
                                Log.d("FireBase:Task  Title Chatbox",title)

                                val messages = if (chatBox.contains("messages")) {
                                    chatBox["messages"] as ArrayList<HashMap<String, String>>
                                } else {
                                    ArrayList<HashMap<String, String>>()
                                }
                                val messageModels = ArrayList<MessageModel>() // Create a list to store the MessageModels
                                for (message in messages) { // Loop through the messages
                                    Log.d("FireBase:Task  Loop Messages","hit")

                                    val msg = message["message"] as String // Extract the message
                                    Log.d("FireBase:Task  Messages Chatbox",msg)
                                    val sentBy = message["sentBy"] as String // Extract the sentBy
                                    val messageModel = MessageModel(msg, sentBy) // Create a new MessageModel
                                    messageModels.add(messageModel) // Add the MessageModel to the list
                                }
                                val chatBoxModel = ChatBoxModel(title, messageModels) // Create a new ChatBoxModel
                                chatBoxes.add(chatBoxModel) // Add the ChatBoxModel to the list
                                Log.d("FireBase:Task  Sizeof Chatbox",chatBoxes.size.toString())

                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("faild firebase","Failed to read data from FirebaseDatabase")
            }
        Log.d("FireBase:Task  Sizeof Chatbox",chatBoxes.size.toString())
    }
    fun loadSync(database: FirebaseDatabase, id: String, callback: (ArrayList<ChatBoxModel>) -> Unit) {
        val chatBoxes = ArrayList<ChatBoxModel>()
        database.reference.child("Health_Advice").child(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val dataSnapshot = task.result
                    val genericObject = dataSnapshot.getValue()
                    if (genericObject is ArrayList<*>) {
                        Log.d("FireBase:Task  Arraylist Chatbox","hit")

                        for (chatBox in genericObject) {
                            if (chatBox is HashMap<*, *>) {
                                val title = chatBox["title"] as String

                                val messages = if (chatBox.contains("messages")) {
                                    chatBox["messages"] as ArrayList<HashMap<String, String>>
                                } else {
                                    ArrayList<HashMap<String, String>>()
                                }

                                val messageModels = ArrayList<MessageModel>()
                                for (message in messages) {
                                    val msg = message["message"] as String
                                    val sentBy = message["sentBy"] as String
                                    val messageModel = MessageModel(msg, sentBy)
                                    messageModels.add(messageModel)
                                }

                                val chatBoxModel = ChatBoxModel(title, messageModels)
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
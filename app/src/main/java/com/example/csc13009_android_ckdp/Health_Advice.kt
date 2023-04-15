package com.example.csc13009_android_ckdp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.HealthAdvice.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class Health_Advice : AppCompatActivity() {
    lateinit var RVHealthAdvice: RecyclerView
    lateinit var TVWelcome: TextView
    lateinit var ETMessageBox: EditText
    lateinit var IBTSendMessage: ImageButton
    lateinit var ChatBoxes:ArrayList<ChatBoxModel>
    lateinit var Messages: ArrayList<MessageModel>
    lateinit var currentMessagesModel: ArrayList<MessageModel>



    val chatGPTAPI = GPTAPI()
    val ChatBoxDAO =ChatBoxDAO()

    private lateinit var context: Context
    var messageadapter:MessageAdapter?=null
    private lateinit var chatboxArrayAdapter : ChatBoxAdapter

    lateinit var auth : FirebaseAuth
    lateinit var database: FirebaseDatabase
//    var storageReference: StorageReference? = null

    fun sendMessage(Messages: ArrayList<MessageModel>) {
        runOnUiThread {
            Messages.add(MessageModel("Typing...", MessageModel.SENT_BY_BOT))
            currentMessagesModel.add(MessageModel("Typing...", MessageModel.SENT_BY_BOT))

            messageadapter?.notifyDataSetChanged()
            RVHealthAdvice.smoothScrollToPosition(messageadapter!!.getItemCount())
        }
        chatGPTAPI.sendMessageByRapidAPI(Messages, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string())

                if (json.has("error")){
                    Log.d("ERRORAPI", json.getJSONObject("error").toString())
                }
                else {
                    Log.d("ERRORAPI", json.toString())
                    val completion =
                        json.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                    val messageRespone = completion.getString("content")
                    val roleRespone = completion.getString("role")

                    runOnUiThread {

                        Messages.removeAt(Messages.size - 1)
                        messageadapter?.notifyDataSetChanged()
                        Messages.add(MessageModel(messageRespone, roleRespone))
                        messageadapter?.notifyDataSetChanged()

                        currentMessagesModel.removeAt(Messages.size - 1)
                        currentMessagesModel.add(MessageModel(messageRespone, roleRespone))

                        RVHealthAdvice.smoothScrollToPosition(messageadapter!!.getItemCount())

                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = e.message ?: "An unknown error"
                runOnUiThread {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_advice)
        context=this
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()

        database=FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        ChatBoxes=ArrayList<ChatBoxModel>()
        ChatBoxDAO.loadSync(database, auth.uid!!) { chatBoxes ->
            ChatBoxes.clear()
            ChatBoxes.addAll(chatBoxes)
            if (ChatBoxes.isEmpty()) {
                ChatBoxes.add(ChatBoxModel("No title"))
            }

            chatboxArrayAdapter?.notifyDataSetChanged()
            Messages.clear()
            Messages.addAll(ChatBoxes[0].messages)
            if (!Messages.isEmpty()){
                
                TVWelcome.visibility= View.GONE
            }
            else{
                TVWelcome.visibility= View.VISIBLE
            }
            currentMessagesModel=ChatBoxes[0].messages
            messageadapter?.notifyDataSetChanged()
        }

        Messages= ArrayList<MessageModel>()
        if (ChatBoxes.isEmpty()) {
            ChatBoxes.add(ChatBoxModel("No title"))
        }
        currentMessagesModel = ChatBoxes[0].messages
        Messages.addAll(currentMessagesModel)


        RVHealthAdvice=findViewById(R.id.RVHealthAdvice)
        TVWelcome=findViewById(R.id.TVWelcome)
        ETMessageBox=findViewById(R.id.ETNameofDrug)
        IBTSendMessage=findViewById(R.id.BTNSearch)
        if (!Messages.isEmpty()){
            
            TVWelcome.visibility= View.GONE
        }
        else{
            TVWelcome.visibility= View.VISIBLE
        }

        val rvStudents = findViewById<RecyclerView>(R.id.RVHealthAdvice) as RecyclerView
        messageadapter = MessageAdapter(this,Messages)
        rvStudents.adapter = messageadapter
        rvStudents.layoutManager = LinearLayoutManager(this)


        IBTSendMessage.setOnClickListener{
            var query:String=ETMessageBox.text.toString().trim()
            if (query!=""){


                Messages.add(MessageModel(query,MessageModel.SENT_BY_USER))
                messageadapter?.notifyDataSetChanged()
                if (!Messages.isEmpty()){
                    
                    TVWelcome.visibility= View.GONE
                }
                else{
                    TVWelcome.visibility= View.VISIBLE
                }


                currentMessagesModel.add(MessageModel(query,MessageModel.SENT_BY_USER))

                RVHealthAdvice.smoothScrollToPosition(messageadapter!!.getItemCount())
                ETMessageBox.setText("")

                sendMessage(Messages)
            }
//            Toast.makeText(this,query,Toast.LENGTH_LONG).show()
        }





        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val chatboxList: RecyclerView = navigationView.findViewById(R.id.RVChatBox)


        chatboxArrayAdapter = ChatBoxAdapter(this, ChatBoxes,messageadapter,Messages,currentMessagesModel)
        chatboxList.adapter = chatboxArrayAdapter
        chatboxList.layoutManager = LinearLayoutManager(this)

        chatboxArrayAdapter.onItemClick = { ChatBox ->
            Messages.clear()
            Messages.addAll(ChatBox.messages)
            if (!Messages.isEmpty()){
                
                TVWelcome.visibility= View.GONE
            }
            else{
                TVWelcome.visibility= View.VISIBLE
            }

            currentMessagesModel=ChatBox.messages
            messageadapter?.notifyDataSetChanged()

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
                    var temp=ChatBoxModel(title)

                    Messages.clear()
                    Messages.addAll(temp.messages)
                    if (!Messages.isEmpty()){
                        
                        TVWelcome.visibility= View.GONE
                    }
                    else{
                        TVWelcome.visibility= View.VISIBLE
                    }

                    ChatBoxes.add(temp)
                    currentMessagesModel=temp.messages

                    chatboxArrayAdapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()

            dialog.show()
        }
        val ICBacktoHome: ImageView = findViewById(R.id.ICBacktoHome)
        ICBacktoHome.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        var id=auth.uid
        Log.d("Store Chat Boxes", "Storing")
        ChatBoxDAO.save(database,id!!,ChatBoxes)
    }
}
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
    private lateinit var context: Context
    var messageadapter:MessageAdapter?=null

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

        //todo use ChatBoxes DAO load message from Data base
        ChatBoxes=ArrayList<ChatBoxModel>()
        ChatBoxes.add(ChatBoxModel("Dau Rang"))

        Messages= ArrayList<MessageModel>()

        ChatBoxes[0].messages.add(MessageModel("hello GPT",MessageModel.SENT_BY_USER))
        ChatBoxes[0].messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_BOT))
        ChatBoxes[0].messages.add(MessageModel("I hurt",MessageModel.SENT_BY_USER))
        ChatBoxes[0].messages.add(MessageModel("Where",MessageModel.SENT_BY_BOT))
        currentMessagesModel=ChatBoxes[0].messages
        Messages.addAll(currentMessagesModel)

        ChatBoxes.add(ChatBoxModel("Dau Bung"))
        ChatBoxes[1].messages.add(MessageModel("hello GPT1",MessageModel.SENT_BY_USER))
        ChatBoxes[1].messages.add(MessageModel("hello USER, Can I help you?1",MessageModel.SENT_BY_BOT))
        ChatBoxes[1].messages.add(MessageModel("I hurt1",MessageModel.SENT_BY_USER))
        ChatBoxes[1].messages.add(MessageModel("Where1",MessageModel.SENT_BY_BOT))

        ChatBoxes.add(ChatBoxModel("Dau Dau"))
        ChatBoxes[2].messages.add(MessageModel("hello GPT2",MessageModel.SENT_BY_USER))
        ChatBoxes[2].messages.add(MessageModel("hello USER, Can I help you?2",MessageModel.SENT_BY_BOT))
        ChatBoxes[2].messages.add(MessageModel("I hurt2",MessageModel.SENT_BY_USER))
        ChatBoxes[2].messages.add(MessageModel("Where2",MessageModel.SENT_BY_BOT))

        //end todo




        RVHealthAdvice=findViewById(R.id.RVHealthAdvice)
        TVWelcome=findViewById(R.id.TVWelcome)
        ETMessageBox=findViewById(R.id.ETNameofDrug)
        IBTSendMessage=findViewById(R.id.BTNSearch)


        if (!Messages.isEmpty()){
            TVWelcome.setText("")
            TVWelcome.visibility= View.GONE
        }

        val rvStudents = findViewById<RecyclerView>(R.id.RVHealthAdvice) as RecyclerView
        messageadapter = MessageAdapter(this,Messages)
        rvStudents.adapter = messageadapter
        rvStudents.layoutManager = LinearLayoutManager(this)


        IBTSendMessage.setOnClickListener{
            var query:String=ETMessageBox.text.toString().trim()
            if (query!=""){
                TVWelcome.setText("")
                TVWelcome.visibility= View.GONE
                Messages.add(MessageModel(query,MessageModel.SENT_BY_USER))
                messageadapter?.notifyDataSetChanged()

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


        val chatboxArrayAdapter = ChatBoxAdapter(this, ChatBoxes,messageadapter,Messages,currentMessagesModel)
        chatboxList.adapter = chatboxArrayAdapter
        chatboxList.layoutManager = LinearLayoutManager(this)

        chatboxArrayAdapter.onItemClick = { ChatBox ->
            Messages.clear()
            Messages.addAll(ChatBox.messages)
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
    }
}
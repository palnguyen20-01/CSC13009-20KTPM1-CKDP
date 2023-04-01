package com.example.csc13009_android_ckdp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.*
import com.example.csc13009_android_ckdp.HealthAdvice.GPTAPI
import com.example.csc13009_android_ckdp.HealthAdvice.MessageAdapter
import com.example.csc13009_android_ckdp.HealthAdvice.MessageModel
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
    lateinit var Messages:ArrayList<MessageModel>
    val chatGPTAPI = GPTAPI()

    var adapter:MessageAdapter?=null

    fun sendMessage(messages: ArrayList<MessageModel>) {
        runOnUiThread {
            Messages.add(MessageModel("typing...", MessageModel.SENT_BY_BOT))
            adapter?.notifyDataSetChanged()
            RVHealthAdvice.smoothScrollToPosition(adapter!!.getItemCount())
        }
        chatGPTAPI.sendMessage(messages, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string())
//
//                runOnUiThread {
//                    TVWelcome.visibility= View.VISIBLE
//                    TVWelcome.setText(json.toString())
//                    ETMessageBox.setText(json.toString())ào
//                }
                if (json.has("error")){
//                    TVWelcome.visibility= View.VISIBLE
//                    TVWelcome.setText(json.toString())
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
                        adapter?.notifyDataSetChanged()
                        Messages.add(MessageModel(messageRespone, roleRespone))
                        adapter?.notifyDataSetChanged()
                        RVHealthAdvice.smoothScrollToPosition(adapter!!.getItemCount())

                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Xử lý lỗi khi gửi yêu cầu API
//                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_advice)

        Messages= ArrayList<MessageModel>()
//        Messages.add(MessageModel("hello GPT",MessageModel.SENT_BY_USER))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_BOT))
//        Messages.add(MessageModel("I hurt",MessageModel.SENT_BY_USER))
//        Messages.add(MessageModel("Where",MessageModel.SENT_BY_BOT))
//        Messages.add(MessageModel("hello USER, Can I help you hello USER, Can I help you hello USER, Can I help you ?",MessageModel.SENT_BY_USER))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_BOT))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_USER))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_BOT))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_USER))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_BOT))
//        Messages.add(MessageModel("hello USER, Can I help you?",MessageModel.SENT_BY_USER))


        RVHealthAdvice=findViewById(R.id.RVHealthAdvice)
        TVWelcome=findViewById(R.id.TVWelcome)
        ETMessageBox=findViewById(R.id.ETMessageBox)
        IBTSendMessage=findViewById(R.id.IBTSendMessage)


        if (!Messages.isEmpty()){
            TVWelcome.setText("")
            TVWelcome.visibility= View.GONE
        }

        val rvStudents = findViewById<RecyclerView>(R.id.RVHealthAdvice) as RecyclerView
        adapter = MessageAdapter(this,Messages)
        rvStudents.adapter = adapter
        rvStudents.layoutManager = LinearLayoutManager(this)





        IBTSendMessage.setOnClickListener{
            var query:String=ETMessageBox.text.toString().trim()
            if (query!=""){
                TVWelcome.setText("")
                TVWelcome.visibility= View.GONE
                Messages.add(MessageModel(query,MessageModel.SENT_BY_USER))
                adapter?.notifyDataSetChanged()

                RVHealthAdvice.smoothScrollToPosition(adapter!!.getItemCount())
                ETMessageBox.setText("")

                sendMessage(Messages)
            }
//            Toast.makeText(this,query,Toast.LENGTH_LONG).show()
        }
    }
}
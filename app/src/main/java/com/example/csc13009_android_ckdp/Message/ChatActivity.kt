package com.example.csc13009_android_ckdp.Message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    lateinit private var binding:ActivityChatBinding

    companion object {
        val TAG = "ChatLog"
    }

private val adapter = GroupAdapter<ViewHolder>()

private var toUser :Users? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerviewChatLog.adapter=adapter

        toUser = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)


        supportActionBar?.title=toUser?.name

//setUpDummyData()
        listenForMessages()



        binding.sendButtonChatLog.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.userId

        val ref = FirebaseDatabase.getInstance().getReference("/Users_Messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatToItem(chatMessage.text,toUser))

                    } else {

                        val currentUser = MessageActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {
// how do we actually send a message to firebase...
        val text = binding.edittextChatLog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)
        val toId = user?.userId

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/Users_Messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/Users_Messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/Latest_Messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/Latest_Messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }

}

class ChatFromItem(val text:String, val user: Users?):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatTV: TextView =viewHolder.itemView.findViewById(R.id.textview_from_row)
        chatTV.text=text

        val image=viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_chat_from_row)
        var uri=user?.image
        Picasso.get().load(uri).into(image)
    }

    override fun getLayout(): Int {
        return R.layout.item_chat_from_row
    }

}
class ChatToItem(val text:String,val user: Users?):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
val chatTV: TextView =viewHolder.itemView.findViewById(R.id.textview_to_row)
        chatTV.text=text

        val image=viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_chat_to_row)
        var uri=user?.image
        Picasso.get().load(uri).into(image)
    }

    override fun getLayout(): Int {
        return R.layout.item_chat_to_row
    }

}
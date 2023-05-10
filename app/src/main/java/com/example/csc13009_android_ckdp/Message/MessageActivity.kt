package com.example.csc13009_android_ckdp.Message

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.LoginActivity
import com.example.csc13009_android_ckdp.Message.NewMessageActivity.Companion.USER_KEY
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.Notification.NotificationService
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.databinding.ActivityChatBinding
import com.example.csc13009_android_ckdp.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text

class MessageActivity : AppCompatActivity() {
    companion object {
        var currentUser: Users? = null

    }
    lateinit private var binding: ActivityMessageBinding

    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var mList:ArrayList<LatestMessageRow> = arrayListOf()

    private var count:Int= 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerviewLatestMessages.adapter=adapter

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as LatestMessageRow

if(FirebaseAuth.getInstance().uid != userItem.friendId){
            val intent= Intent(view.context,ChatActivity::class.java)
            intent.putExtra(USER_KEY, userItem.friendId)
            startActivity(intent)
        }
        }

        listenForLatestMessages()


        fetchCurrentUser()

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete)!!

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                adapter.removeItem(viewHolder.bindingAdapterPosition,mList)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewLatestMessages)

    }

    class LatestMessageRow(val chatMessage: ChatMessage,val friendId:String): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val lastestChat=viewHolder.itemView.findViewById<TextView>(R.id.message_textview_latest_message)
            if(chatMessage.type==1) {
                lastestChat.text = chatMessage.text
            }else if(chatMessage.type==2){
                lastestChat.text="Image"
            }
            val image=viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_latest_message)
val username=viewHolder.itemView.findViewById<TextView>(R.id.username_textview_latest_message)

            FirebaseDatabase.getInstance().getReference("/Users/$friendId").get().addOnSuccessListener {
                var toUser=it.getValue(Users::class.java)
                val button: Button =viewHolder.itemView.findViewById(R.id.remindToTakeMedicineBtn)
                button.setOnClickListener{
                    val notificationService= NotificationService()
                    var mUserId=FirebaseAuth.getInstance().uid
                    if (mUserId != null) {
                        notificationService.notifyForThatPerson(toUser!!.userId,"clock",mUserId,System.currentTimeMillis().toString())
                    }
                }
                username.text=toUser?.name
                Picasso.get().load(toUser?.image).into(image)

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }


        }

        override fun getLayout(): Int {
            return R.layout.item_last_message
        }
    }
    val adapter=GroupAdapter<ViewHolder>()

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        mList.clear()


        latestMessagesMap.forEach {task ->
            val friendId= if (FirebaseAuth.getInstance().uid==task.value.fromId) task.value.toId else task.value.fromId

            mList.add(LatestMessageRow(task.value, friendId.toString()))
            adapter.add(LatestMessageRow(task.value, friendId.toString()))
}

    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Latest_Messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(Users::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.image}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
                when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}

private fun <VH : ViewHolder?> GroupAdapter<VH>.removeItem(id:Int,list:ArrayList<MessageActivity.LatestMessageRow>) {
    var temp=this.getItem(id)
    var removeItemId=list[id].friendId
    var myId=if (list[id].chatMessage.fromId == removeItemId) list[id].chatMessage.toId else list[id].chatMessage.fromId
list.remove(list[id])
    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/Latest_Messages/$myId/${removeItemId}").removeValue()
this.remove(temp)
    this.notifyDataSetChanged()
}

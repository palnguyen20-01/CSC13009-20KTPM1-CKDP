package com.example.csc13009_android_ckdp.Message

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.example.csc13009_android_ckdp.SettingFragment
import com.example.csc13009_android_ckdp.databinding.ActivityChatBinding
import com.example.csc13009_android_ckdp.utilities.RequestCodeResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit private var binding:ActivityChatBinding

    companion object {
        val TAG = "ChatLog"
        private val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
    }

private val adapter = GroupAdapter<ViewHolder>()

private var toUser :Users? =null
private var toUserId:String?=null
    var imageUri: Uri? = null
    var encodedImage : String = ""
    var isChooseImage: Boolean = false
    lateinit var storageReference: StorageReference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerviewChatLog.adapter=adapter

       toUserId = intent.getStringExtra(NewMessageActivity.USER_KEY)



//setUpDummyData()
        listenForMessages()

        binding.sendButtonChatLog.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }

        binding.imageButton.setOnClickListener {
            isChooseImage = true
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)

        }
    }

    private fun getRandomString(sizeOfRandomString: Int = 100): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().getReference("/Users/$toUserId").get().addOnSuccessListener {
            toUser = it.getValue(Users::class.java)
            val toId = toUserId
            supportActionBar?.title=toUser?.name

            val ref = FirebaseDatabase.getInstance().getReference("/Users_Messages/$fromId/$toId")

            ref.addChildEventListener(object: ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val chatMessage = p0.getValue(ChatMessage::class.java)

                    if (chatMessage != null) {

                        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                            val currentUser = MessageActivity.currentUser ?:return
                            adapter.add(ChatToItem(chatMessage,currentUser))

                        } else {

                            adapter.add(ChatFromItem(chatMessage,toUser))
                        }
                    }
                    binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)

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

    }



    private fun performSendMessage(type:Int=1) {
// how do we actually send a message to firebase...
        val text = binding.edittextChatLog.text.toString()
        binding.edittextChatLog.setText("")

        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getStringExtra(NewMessageActivity.USER_KEY)

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/Users_Messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/Users_Messages/$toId/$fromId").push()

        if(type==1) {
            val chatMessage =
                ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
                }

            toReference.setValue(chatMessage)

            val latestMessageRef =
                FirebaseDatabase.getInstance().getReference("/Latest_Messages/$fromId/$toId")
            latestMessageRef.setValue(chatMessage)

            val latestMessageToRef =
                FirebaseDatabase.getInstance().getReference("/Latest_Messages/$toId/$fromId")
            latestMessageToRef.setValue(chatMessage)
        }else if(type==2){
            storageReference = FirebaseStorage.getInstance().getReference("message-images/" + getRandomString())
            storageReference.putFile(imageUri!!).addOnCompleteListener{
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageMessage =
                        ChatMessage(reference.key!!, uri.toString(), fromId, toId, System.currentTimeMillis() / 1000,2)
                    reference.setValue(imageMessage)

                    toReference.setValue(imageMessage)

                    val latestMessageRef =
                        FirebaseDatabase.getInstance().getReference("/Latest_Messages/$fromId/$toId")
                    latestMessageRef.setValue(imageMessage)

                    val latestMessageToRef =
                        FirebaseDatabase.getInstance().getReference("/Latest_Messages/$toId/$fromId")
                    latestMessageToRef.setValue(imageMessage)

                        .addOnSuccessListener {
                            Log.d(TAG, "Saved our image message: ${reference.key}")
                            binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount - 1)
                        }
                }
            }
                .addOnFailureListener {
                    finish()
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if(result.data != null){
                imageUri = result.data!!.data!!
                try{
                    var inputStream = contentResolver.openInputStream(imageUri!!)
                    var bitmap = BitmapFactory.decodeStream(inputStream)
                    encodedImage = encodeImage(bitmap)
                    performSendMessage(2)
                }catch(e : FileNotFoundException){
                    e.printStackTrace()
                }
            }
            else{
                isChooseImage = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bitmap : Bitmap) : String{
        var previewWidth = 150
        var previewHeight = bitmap.height * previewWidth / bitmap.width
        var previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        var byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        var bytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

}

class ChatFromItem(val chatMessage:ChatMessage, val user: Users?):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val chatTV: TextView =viewHolder.itemView.findViewById(R.id.textview_from_row)
        val imageMessage=viewHolder.itemView.findViewById<ImageView>(R.id.image_message_from_row)
        val image=viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_chat_from_row)

        Log.d("Type",chatMessage.type.toString())
        Log.d("Text",chatMessage.text.toString())

        var uri=user?.image
        Picasso.get().load(uri).into(image)
        if(chatMessage.type==1){
            chatTV.text=chatMessage.text
        }else if(chatMessage.type==2){
            chatTV.visibility= View.GONE
imageMessage.visibility=View.VISIBLE
            var image_message_uri=chatMessage.text
            Picasso.get().load(image_message_uri).into(imageMessage)

        }

    }

    override fun getLayout(): Int {
        return R.layout.item_chat_from_row
    }

}
class ChatToItem(val chatMessage:ChatMessage,val user: Users?):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
val chatTV: TextView =viewHolder.itemView.findViewById(R.id.textview_to_row)
        val imageMessage=viewHolder.itemView.findViewById<ImageView>(R.id.image_message_to_row)
        val image=viewHolder.itemView.findViewById<CircleImageView>(R.id.imageview_chat_to_row)

        var uri=user?.image
        Picasso.get().load(uri).into(image)
if(chatMessage.type==1){
    chatTV.text=chatMessage.text

}else if(chatMessage.type==2){
    chatTV.visibility= View.GONE
    imageMessage.visibility=View.VISIBLE
    var image_message_uri=chatMessage.text
Picasso.get().load(image_message_uri).into(imageMessage)
}
    }

    override fun getLayout(): Int {
        return R.layout.item_chat_to_row
    }

}
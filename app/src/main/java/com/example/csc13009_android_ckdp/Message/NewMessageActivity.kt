package com.example.csc13009_android_ckdp.Message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.Models.Users
import com.example.csc13009_android_ckdp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {
    lateinit var newMessageRecyclerView: RecyclerView
    private var mUser=FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        newMessageRecyclerView =findViewById(R.id.newMessageRV)

        supportActionBar?.title="Select User"

//        val adapter=GroupAdapter<ViewHolder>()
//
//
//        newMessageRecyclerView.adapter=adapter

fetchUser()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUser() {
     val ref = FirebaseDatabase.getInstance().getReference("/Friends/$mUser")
   ref.addListenerForSingleValueEvent(object: ValueEventListener{
       override fun onDataChange(snapshot: DataSnapshot) {
           val adapter=GroupAdapter<ViewHolder>()

           snapshot.children.forEach{
          Log.d("NewMessage",it.toString())
          val user=it.getValue(Users::class.java)
               user?.userId= it.key.toString()
               if(user!=null ){
                   adapter.add(UserItem(user))
               }
      }
           
           adapter.setOnItemClickListener { item, view ->

               val userItem = item as UserItem

               val intent= Intent(view.context,ChatActivity::class.java)
               intent.putExtra(USER_KEY, userItem.user.userId)
               startActivity(intent)

               finish()
           }
           
           newMessageRecyclerView.adapter=adapter

       }


       override fun onCancelled(error: DatabaseError) {
           TODO("Not yet implemented")
       }

   })
    }
}
class UserItem(val user:Users): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
val username:TextView=viewHolder.itemView.findViewById(R.id.username_textview_new_message)
        val image: ImageView =viewHolder.itemView.findViewById(R.id.imageview_new_message)

        username.text=user.name
if(user.image.isNotEmpty())
        Picasso.get().load(user.image).into(image)

    }

    override fun getLayout(): Int {
        return R.layout.item_new_message
    }
}
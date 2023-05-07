package com.example.csc13009_android_ckdp.FindFriend

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.Models.Users
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.example.csc13009_android_ckdp.GlideApp
import com.example.csc13009_android_ckdp.R

class FindFriendActivity : AppCompatActivity() {


    lateinit var userRef: DatabaseReference
    lateinit var recyclerView: RecyclerView
    lateinit var auth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var adapter: FirebaseRecyclerAdapter<Users, FindFriendViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)

        recyclerView = findViewById(R.id.recyclerViewFindFriend)
        recyclerView.layoutManager = LinearLayoutManager(this)


        supportActionBar?.title = "Find friends"
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!

        loadUsers("")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
    }

    private fun loadUsers(s: String) {
        Log.d("Search", s)
        var query = userRef.orderByChild("name").startAt(s).endAt(s + "\uf8ff")
        var options = FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFriendViewHolder {
                return FindFriendViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_friend_request, parent, false))
            }

            override fun onBindViewHolder(
                holder: FindFriendViewHolder,
                position: Int,
                model: Users
            ) {
                holder.bind()

                Log.d("User", model.name)
                if(!mUser.uid.equals(getRef(position).key.toString())){

                    holder.txtTitle?.text = model.name
                    holder.imgAvatar?.let {
                        GlideApp.with(applicationContext)
                            .load(model.image)
                            .centerCrop()
                            .placeholder(R.drawable.user_avatar)
                            .into(it)
                    }
                }
                else{

                    holder.itemView.visibility = View.VISIBLE
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(0,0)
                }
                holder.itemView.setOnClickListener {
                    val intent = Intent(applicationContext, FriendRequestActivity::class.java)
                    intent.putExtra("uid", getRef(position).key.toString())
                    startActivity(intent)
                }
            }
        }
        adapter.startListening()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

//    override fun onStop() {
//        super.onStop()
//        adapter.stopListening()
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_menu, menu)

        var menuItem = menu?.findItem(R.id.search)
        var searchView =  menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                loadUsers(qString)
                return false
            }

            override fun onQueryTextSubmit(qString: String): Boolean {

                return false
            }
        })
        return true
    }
}
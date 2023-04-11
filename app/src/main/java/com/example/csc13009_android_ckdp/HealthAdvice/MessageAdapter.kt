package com.example.csc13009_android_ckdp.HealthAdvice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R

class MessageAdapter(private val context: Context,
                    private val messages: List<MessageModel>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    var onItemClick: ((MessageModel) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val LLUserChat:LinearLayout=listItemView.findViewById<LinearLayout>(R.id.LLUserChat)
        val LLGPTChat:LinearLayout=listItemView.findViewById<LinearLayout>(R.id.LLGPTChat)
        val TVUserChat: TextView = listItemView.findViewById<TextView>(R.id.TVUserChat)
        val TVGPTChat: TextView = listItemView.findViewById<TextView>(R.id.TVGPTChat)

        init {
            listItemView.setOnClickListener {
                 onItemClick?.invoke(messages[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var messageView: View? = null

        messageView = inflater.inflate(R.layout.item_chat, parent, false)

        return ViewHolder(messageView)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Get the data model based on position
            val Message:MessageModel = messages[position]
            // Set item views based on your views and data model
            val LLUserChat =holder.LLUserChat
            val LLGPTChat   =holder.LLGPTChat
            val TVUserChat = holder.TVUserChat
            val TVGPTChat = holder.TVGPTChat

    //        name.text = contact.getFeatureName()
    //        avatar.setImageResource(contact.getImageId())
            val index: Int = holder.absoluteAdapterPosition
            //.getAdapterPosition()
            if (Message.sentBy.equals(MessageModel.SENT_BY_USER)) {
                LLUserChat.visibility=View.VISIBLE
                LLGPTChat.visibility=View.GONE
                TVUserChat.text = Message.message
            }
            else {
                LLUserChat.visibility=View.GONE
                LLGPTChat.visibility=View.VISIBLE
                TVGPTChat.text = Message.message
            }


    }


}
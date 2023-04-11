package com.example.csc13009_android_ckdp.HealthAdvice

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R


class ChatBoxAdapter(private val context: Context,
                     private val titles: ArrayList<ChatBoxModel>,
                     private var messageadapter: RecyclerView.Adapter<*>?,
                     private var currentMessage: ArrayList<MessageModel>,
                     private var currentMessagesModel: ArrayList<MessageModel>,


) : RecyclerView.Adapter<ChatBoxAdapter.ViewHolder>(){

    private var alertDialog: AlertDialog? = null
    var onItemClick: ((ChatBoxModel) -> Unit)? = null
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {

        val TVTitle = listItemView.findViewById(R.id.TVTitle) as TextView
        val IMGBTNIconEdit = listItemView.findViewById(R.id.IMGBTNIconEdit) as ImageButton
        val IMGBTNIconDelete = listItemView.findViewById(R.id.IMGBTNIconDelete) as ImageButton

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(titles[adapterPosition])
            }
            IMGBTNIconEdit.setOnClickListener {
                showEditTitleDialog(adapterPosition)
            }
        }
        private fun showEditTitleDialog(position: Int) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Edit Title")

            val input = EditText(itemView.context)
            input.setText(titles[position].title)
            input.setSelection(titles[position].title.length)
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                titles[position].title = input.text.toString()
                notifyItemChanged(position)
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBoxAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        var ChatBoxView: View? = null

        ChatBoxView = inflater.inflate(R.layout.item_chatbox, parent, false)

        return ViewHolder(ChatBoxView)
    }

    override fun getItemCount(): Int {
        return titles.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val ChatBox:ChatBoxModel = titles[position]
        // Set item views based on your views and data model

        val TVTitle=holder.TVTitle
        val IMGBTNIconEdit = holder.IMGBTNIconEdit
        val IMGBTNIconDelete = holder.IMGBTNIconDelete

        IMGBTNIconDelete.setOnClickListener {
            showDeleteConfirmDialog(position)
        }


        //        name.text = contact.getFeatureName()
        //        avatar.setImageResource(contact.getImageId())
        val index: Int = holder.absoluteAdapterPosition
        //.getAdapterPosition()

        TVTitle.text = ChatBox.title

    }
    private fun showDeleteConfirmDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete ChatBox")
        builder.setMessage("Are you sure you want to delete this ChatBox?")
        builder.setPositiveButton("Yes") { _, _ ->

            var deletedMessages = titles[position].messages
            titles.removeAt(position)
            if (currentMessage.equals(deletedMessages)) {
                currentMessage.clear()
                currentMessagesModel.clear()

                messageadapter?.notifyDataSetChanged()
                if (titles.isNotEmpty() && titles[0] != null) {
                    currentMessage.addAll(titles[0].messages)
                    currentMessagesModel=titles[0].messages
                    messageadapter?.notifyDataSetChanged()

                }
            }

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, titles.size)
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()
    }


}
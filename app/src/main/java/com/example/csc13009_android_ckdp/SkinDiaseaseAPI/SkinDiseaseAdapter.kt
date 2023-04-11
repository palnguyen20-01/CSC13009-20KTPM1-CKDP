package com.example.csc13009_android_ckdp.SkinDiaseaseAPI

import com.example.csc13009_android_ckdp.HealthAdvice.ChatBoxModel
import com.example.csc13009_android_ckdp.HealthAdvice.MessageModel

import android.content.Context
import android.graphics.BitmapFactory
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


class SkinDiseaseAdapter(private val context: Context,
                        private val titles: ArrayList<SkinDiseaseModel>,
                        private var IBDiseaseSkin: ImageButton,
                         private var TVDiagnose: TextView,
                        private var SkinModelUIAndDB: SkinDiseaseModel,
                     ) : RecyclerView.Adapter<SkinDiseaseAdapter.ViewHolder>(){
    private var alertDialog: AlertDialog? = null
    var onItemClick: ((SkinDiseaseModel) -> Unit)? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinDiseaseAdapter.ViewHolder {
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
        val ChatBox: SkinDiseaseModel = titles[position]
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

            var deletedMessages = titles[position]
            titles.removeAt(position)
            if (SkinModelUIAndDB==deletedMessages) {
                if (titles.isNotEmpty() && titles[0] != null) {
                    SkinModelUIAndDB=titles[0]
                    val imageBytes=titles[0].imageBytes
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    IBDiseaseSkin.setImageBitmap(bitmap)
                    TVDiagnose.setText(titles[0].diagnose)
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
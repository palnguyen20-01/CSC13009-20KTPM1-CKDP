package com.example.csc13009_android_ckdp.DrugInfo


import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.csc13009_android_ckdp.R



class DrugAdapter(private val context: Context,
                         private val titles: ArrayList<DrugModel>,

                         private var tvTitle: TextView,
                         private var tvDrugInteractionsDetails: TextView,
                         private var tvIndicationsDetails: TextView,
                         private var tvDosageDetails: TextView,

                         private var DrugUIDB: DrugModel
) : RecyclerView.Adapter<DrugAdapter.ViewHolder>(){
    private var alertDialog: AlertDialog? = null
    var onItemClick: ((DrugModel) -> Unit)? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugAdapter.ViewHolder {
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

        val ChatBox: DrugModel = titles[position]


        val TVTitle=holder.TVTitle
        val IMGBTNIconEdit = holder.IMGBTNIconEdit
        val IMGBTNIconDelete = holder.IMGBTNIconDelete

        IMGBTNIconDelete.setOnClickListener {
            showDeleteConfirmDialog(position)
        }

        val index: Int = holder.absoluteAdapterPosition


        TVTitle.text = ChatBox.title

    }
    private fun showDeleteConfirmDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete ChatBox")
        builder.setMessage("Are you sure you want to delete this ChatBox?")
        builder.setPositiveButton("Yes") { _, _ ->

            var deletedMessages = titles[position]
            titles.removeAt(position)
            if (DrugUIDB==deletedMessages) {
                if (titles.isNotEmpty() && titles[0] != null) {
                    DrugUIDB=titles[0]
                    tvTitle.setText(DrugUIDB.Name)
                    tvDrugInteractionsDetails.setText(DrugUIDB.interactions)
                    tvIndicationsDetails.setText(DrugUIDB.indications)
                    tvDosageDetails.setText(DrugUIDB.dosage)
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
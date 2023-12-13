package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ItemAdapter(val context: Context, private val items: MutableList<Item>):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var onClick: ((Item) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvItem)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        var itemPosition: Int = 0


        init {
            cbDone.setOnClickListener {
                if(items[itemPosition].uid.isEmpty()) {
                    Toast.makeText(itemView.context, "Something went wrong, try again.", Toast.LENGTH_SHORT).show()
                } else {
                    items[itemPosition].done = cbDone.isChecked
                    replaceItemToDB(itemView, items[itemPosition], cbDone.isChecked)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val itemView = layoutInflater.inflate(R.layout.item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].name
        holder.cbDone.isChecked = items[position].done

        holder.itemPosition  = position


    }

    private fun replaceItemToDB(view: View, item: Item, done: Boolean) {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        if(user == null) {
            return
        } else {
            db.collection("users").document(user.uid).collection("items").document(item.uid).update("done", done)
                .addOnSuccessListener {
                    Toast.makeText(view.context, "Item changed", Toast.LENGTH_SHORT).show()
                }
        }
    }


}
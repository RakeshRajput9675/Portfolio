package com.example.interntask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val items: MutableList<User>,
                  private val onEditClick: (User, Int) -> Unit,
                  private val onDeleteClick: (User, Int) -> Unit) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.tvTitle)
        val descText: TextView = itemView.findViewById(R.id.tvDescription)
        val edit:Button=itemView.findViewById(R.id.btn_edit)
        val delete: Button =itemView.findViewById(R.id.btn_Delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.descText.text = item.description
        holder.edit.setOnClickListener {
            onEditClick(item, position)
        }

        holder.delete.setOnClickListener {
            onDeleteClick(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItem(position: Int, updatedUser: User) {
        items[position] = updatedUser
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(item: User) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
}

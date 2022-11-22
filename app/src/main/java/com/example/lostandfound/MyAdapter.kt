package com.example.lostandfound

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val mList: ArrayList<MyDataClass>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        val imageUrl = ItemsViewModel.image
        val id = ItemsViewModel.id
        val location = "Location: " + ItemsViewModel.location
        val date = "Date" + ItemsViewModel.date
        val type = ItemsViewModel.type
        Glide.with(holder.image.context).load(imageUrl).into(holder.image)
        holder.desc.text = ItemsViewModel.desc
        holder.location.text = location
        holder.date.text = date
        holder.itemView.setOnClickListener(View.OnClickListener {
            Intent(holder.itemView.context,MyItem::class.java).also {
                it.putExtra("id",id)
                it.putExtra("type", type)
                holder.itemView.context.startActivity(it)
                Log.d("abd", "Intent: $id")
            }
        })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageOfFoundItem)
        val desc: TextView = itemView.findViewById(R.id.title)
        val location: TextView = itemView.findViewById(R.id.tvfoundLocation)
        val date: TextView = itemView.findViewById(R.id.dbfoundLocation)
    }
}
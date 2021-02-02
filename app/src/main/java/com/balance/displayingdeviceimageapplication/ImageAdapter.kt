package com.balance.displayingdeviceimageapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    var data : MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, null))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .applyDefaultRequestOptions(RequestOptions().override(150, 150))
            .load(data[position])
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    fun setImage(item : String){
        this.data.add(item)
        notifyDataSetChanged()
    }

    fun setImageList(items: MutableList<String>){
        this.data = items
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById(R.id.image_view) as ImageView
        val title = itemView.findViewById(R.id.title) as TextView
    }
}
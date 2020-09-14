package com.example.travelassistant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.travelassistant.R
import com.example.travelassistant.models.LocationModel
import kotlinx.android.synthetic.main.layout_list_item.view.*

class LocationsListAdapter(private val locations: MutableList<LocationModel>) : RecyclerView.Adapter<LocationsListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listImage = holder.itemView.list_image
        Glide.with(holder.itemView).load(locations[position].imageURLs?.get(0)).apply(RequestOptions().override(listImage.width, listImage.height)).into(listImage)

        holder.itemView.list_name.text = locations[position].name
        holder.itemView.list_country.text = locations[position].country
    }

    override fun getItemCount(): Int = locations.size
}
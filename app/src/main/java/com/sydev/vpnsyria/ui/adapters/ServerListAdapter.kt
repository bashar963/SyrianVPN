package com.sydev.vpnsyria.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sydev.vpnsyria.R


class ServerListAdapter(private val dataSet:MutableList<ServerItem>) : RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.server_item, parent, false)
        return ViewHolder(view)    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.countryFlag.setImageResource(dataSet[position].flagRes)
        holder.serverName.text = dataSet[position].serverName
        holder.serverStrength.setImageDrawable(dataSet[position].ServerStrengthRes)

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var countryFlag : ImageView = view.findViewById(R.id.serverCountryFlag)
        var serverName : TextView= view.findViewById(R.id.serverName)
        var serverStrength : ImageView = view.findViewById(R.id.serverStrength)

    }



}

data class ServerItem(var serverId:String ,var flagRes:Int,var serverName:String,var ServerStrengthRes:Drawable)
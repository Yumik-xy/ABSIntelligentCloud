package com.absintelligentcloud.android.ui.device

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.Device

class DeviceAdapter(private val fragment: Fragment, private val deviceList: List<Device>) :

    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceId: TextView = view.findViewById(R.id.deviceId)
        val deviceName: TextView = view.findViewById(R.id.deviceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = deviceList[position]
        holder.deviceId.text = device.id
        holder.deviceName.text = device.name
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }


}
package com.absintelligentcloud.android.ui.device

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.*

class DeviceAdapter(
    private val context: Context,
    private val deviceList: List<DeviceResponse.Device>
) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    companion object {
        const val FUN_UPDATE = 3
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val absType: TextView = view.findViewById(R.id.absType)
        val deviceId: TextView = view.findViewById(R.id.deviceId)
        val userName: TextView = view.findViewById(R.id.userName)
        val contactNumber: TextView = view.findViewById(R.id.contactNumber)
//        val agentName: TextView = view.findViewById(R.id.agentName)
        val tireBrand: TextView = view.findViewById(R.id.tireBrand)
//        val productionDate: TextView = view.findViewById(R.id.productionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = deviceList[position]
        holder.absType.text = device.absType
        holder.deviceId.text = device.deviceId
        holder.userName.text = device.userName
        holder.contactNumber.text = device.contactNumber
//        holder.agentName.text = "代理商：" + device.agentName
        holder.tireBrand.text = device.tireBrand
//        val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        holder.productionDate.text = simpleDataFormat.format(device.productionDate)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("device_id", device.deviceId)
            intent.putExtra("function", FUN_UPDATE)
            context.startActivity(intent)
        }

//        holder.itemView.setOnLongClickListener {
//            val intent = Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:" + device.contactNumber)
//            context.startActivity(intent)
//            true
//        }
    }

    override fun getItemCount(): Int = deviceList.size

}
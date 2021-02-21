package com.absintelligentcloud.android.ui.history

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
import com.absintelligentcloud.android.logic.model.HistoryResponse
import com.absintelligentcloud.android.ui.detail.DetailActivity
import com.absintelligentcloud.android.ui.device.DeviceAdapter
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val context: Context,
    private val historyList: List<HistoryResponse.History>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    companion object {
        const val FUN_READONLY = 4
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceId: TextView = view.findViewById(R.id.deviceId)
        val monitorDate: TextView = view.findViewById(R.id.monitorDate)
        val faultName: TextView = view.findViewById(R.id.faultName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.deviceId.text = history.deviceId
        holder.faultName.text = history.faultName
//        val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        holder.monitorDate.text = simpleDataFormat.format(history.monitorDate)
        holder.monitorDate.text = history.monitorDate

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("device_id", history.deviceId)
            intent.putExtra("function", FUN_READONLY)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = historyList.size

}
package com.yumik.absintelligentcloud.module.porcelain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.yumik.absintelligentcloud.databinding.AdapterProcelainBinding
import com.yumik.absintelligentcloud.logic.model.Porcelain

class PorcelainAdapter : RecyclerView.Adapter<PorcelainAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: AdapterProcelainBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val view = itemView
    }

    private val dataList =
        SortedList(Porcelain::class.java, object : SortedListAdapterCallback<Porcelain>(this) {
            override fun compare(o1: Porcelain, o2: Porcelain): Int {
//                return o2.id.compareTo(o1.id) // 倒序，id更大的在上面
                return o1.id.compareTo(o2.id) // 顺序，id更大的在下面
            }

            override fun areContentsTheSame(oldItem: Porcelain, newItem: Porcelain): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: Porcelain, item2: Porcelain): Boolean {
                return item1.id == item2.id
            }
        })

    fun add(item: Porcelain) {
        dataList.add(item)
    }

    fun add(list: List<Porcelain>) {
        dataList.addAll(list)
    }

    fun clear() {
        dataList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterProcelainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.view.apply {
            imageResource.setImageResource(item.imageResource)
            title.text = item.title
            cellItem.setOnClickListener {
                item.clickListener()
            }
        }
    }

    override fun getItemCount(): Int = dataList.size()
}
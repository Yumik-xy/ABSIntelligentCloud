package com.absintelligentcloud.android.ui.area

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.AreaResponse
import kotlinx.android.synthetic.main.area_item.view.*


class AreaAdapter(private val fragment: Fragment, private val areaList: List<AreaResponse.Data>) :
    SpinnerAdapter, BaseAdapter() {
    override fun getCount() = areaList.size

    override fun getItem(position: Int) = areaList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val area = areaList[position]
        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(fragment.context).inflate(R.layout.area_item, parent, false)
            view.areaName.text = area.areaName
        }
        return view
    }

}
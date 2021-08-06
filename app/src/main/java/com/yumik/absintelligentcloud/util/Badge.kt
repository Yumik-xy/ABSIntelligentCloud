package com.yumik.absintelligentcloud.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yumik.absintelligentcloud.R

class Badge(private val context: Context, private val navView: BottomNavigationView) {

    var badgeView: View? = null

    fun setBadge(itemNum: Int) {
        // 获取底部菜单
        val menuView = navView.getChildAt(0) as BottomNavigationMenuView
        // 获取对应ItemView
        val itemView = menuView.getChildAt(itemNum) as BottomNavigationItemView
        // 引入Badge
        badgeView =
            LayoutInflater.from(context).inflate(R.layout.badge_without_number, menuView, false)
        //把badgeView添加到itemView中
        itemView.addView(badgeView)
        //不显示则隐藏
        //count.visibility=View.GONE
    }

    fun setBadge(itemNum: Int, Number: Int) {
        // 获取底部菜单
        val menuView = navView.getChildAt(0) as BottomNavigationMenuView
        // 获取对应ItemView
        val itemView = menuView.getChildAt(itemNum) as BottomNavigationItemView
        // 引入Badge
        badgeView = LayoutInflater.from(context).inflate(R.layout.badge, menuView, false)
        //把badgeView添加到itemView中
        itemView.addView(badgeView)
        //获取子view并设置显示数目
        val count = badgeView!!.findViewById<TextView>(R.id.tv_badge)
        //count.text = Number.toString()
        count.text = Number.toString()
        //不显示则隐藏
        //count.visibility=View.GONE
    }

    fun hidBadge(itemNum: Int) {
        // 获取底部菜单
        val menuView = navView.getChildAt(0) as BottomNavigationMenuView
        // 获取对应ItemView
        val itemView = menuView.getChildAt(itemNum) as BottomNavigationItemView
        // 引入Badge
        val badgeView = LayoutInflater.from(context).inflate(R.layout.badge, menuView, false)
        //把badgeView从itemView中移除
        itemView.removeView(badgeView)
    }
}
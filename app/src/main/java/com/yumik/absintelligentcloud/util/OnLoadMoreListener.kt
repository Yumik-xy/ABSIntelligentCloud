package com.yumik.absintelligentcloud.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class OnLoadMoreListener : RecyclerView.OnScrollListener() {
    private var layoutManager: LinearLayoutManager? = null
    private var itemCount = 0
    private var lastPosition = 0
    private var lastItemCount = 0
    abstract fun onLoadMore()
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            itemCount = layoutManager!!.itemCount
            lastPosition = layoutManager!!.findLastCompletelyVisibleItemPosition()
        } else {
            return
        }
        if (lastItemCount != itemCount && lastPosition == itemCount - 1) {
            lastItemCount = itemCount
            onLoadMore()
        }
    }
}
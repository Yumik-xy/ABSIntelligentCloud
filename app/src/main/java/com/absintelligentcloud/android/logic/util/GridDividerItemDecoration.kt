package com.absintelligentcloud.android.logic.util

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.ceil


open class GridDividerItemDecoration(context: Context) : ItemDecoration() {
    private val mDivider: Drawable

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left: Int = child.left - params.leftMargin
            val right: Int = (child.right + params.rightMargin
                    + mDivider.intrinsicWidth)
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top: Int = child.top - params.topMargin
            val bottom: Int = child.bottom + params.bottomMargin
            val left: Int = child.right + params.rightMargin
            val right = left + mDivider.intrinsicWidth
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    /**
     * 是否是最后一行
     */
    private fun isLastRow(itemPosition: Int, parent: RecyclerView): Boolean {
        val spanCount = getSpanCount(parent)
        val layoutManager = parent.layoutManager
        //有多少列
        if (layoutManager is GridLayoutManager) {
            val childCount = parent.adapter!!.itemCount
            val count = ceil(childCount.toDouble() / spanCount.toDouble()) //总行数
            val currentCount = ceil((itemPosition + 1).toDouble() / spanCount) //当前行数

            //最后当前数量小于总的
            if (currentCount < count) {
                return false
            }
        }
        return true
    }

    /**
     * 判断是否是最后一列
     */
    private fun isLastColumn(itemPosition: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        //有多少列
        if (layoutManager is GridLayoutManager) {
            val spanCount = getSpanCount(parent)
            if ((itemPosition + 1) % spanCount == 0) { //因为是从0可以所以要将ItemPosition先加1
                return true
            }
        }
        return false
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isLastRow(parent.getChildLayoutPosition(view), parent)) // 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0.toInt(), 0, mDivider.intrinsicHeight)
        }
        if (isLastColumn(parent.getChildLayoutPosition(view), parent)) // 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, mDivider.intrinsicWidth, 0)
        }
    }

    companion object {
        private val ATTRS = intArrayOf(R.attr.listDivider)
    }

    init {
        val a: TypedArray = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)!!
        a.recycle()
    }
}
package com.absintelligentcloud.android.logic.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView


class ObservableScrollView : ScrollView {
    private var scrollViewListener: ScrollViewListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setScrollViewListener(scrollViewListener: ScrollViewListener?) {
        this.scrollViewListener = scrollViewListener
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollChanged(this, x, y, oldx, oldy)
        }
    }


}

public interface ScrollViewListener {
    fun onScrollChanged(
        scrollView: ScrollView?,
        x: Int,
        y: Int,
        oldx: Int,
        oldy: Int
    )
}
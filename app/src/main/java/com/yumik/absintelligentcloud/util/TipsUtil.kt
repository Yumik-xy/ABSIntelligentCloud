package com.yumik.absintelligentcloud.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.yumik.absintelligentcloud.R

object TipsUtil {
    fun View.showSnackbar(
        text: String,
        actionText: String? = null,
        duration: Int = Snackbar.LENGTH_SHORT,
        block: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(this, text, duration)
//        snackbar.set
        if (actionText != null && block != null) {
            snackbar.setAction(actionText) {
                block()
            }
        }
        snackbar.show()
    }

    fun View.showMySnackbar(
        text: String,
        color: Int = R.color.primary,
        actionText: String? = null,
        duration: Int = Snackbar.LENGTH_SHORT,
        block: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(this, text, duration)
        if (actionText != null && block != null) {
            snackbar.setAction(actionText) {
                block()
            }
        }
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this.context, color))
        snackbar.setActionTextColor(ContextCompat.getColor(this.context, R.color.white))
        /**
        // 设置snackBar图标 这里是获取到snackBar的textView 然后给textView增加左边图标的方式来实现的
        View snackBarView = snackbar.getView();
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_vector_notification);//图片自己选择
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
        //增加文字和图标的距离
        textView.setCompoundDrawablePadding(20);
        */
        snackbar.show()
    }

    fun String.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, this, duration).show()
    }
}
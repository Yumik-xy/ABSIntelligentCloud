package com.yumik.absintelligentcloud.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OneShotPreDrawListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : Fragment() {

    /**
     * `viewModel` 通过反射方法获取的 `ViewModel`
     */
    protected val viewModel: VM by lazy {
        BindingReflex.reflexViewModel(javaClass, this)
    }

    /**
     * `binding` 通过反射方法获取的 `ViewBinding`
     *
     * 建议避免使用反射的方法获取，可以在 [onCreateView] 中传递 `inflater` 和 `container`，在原 `Fragment` 中获取
     * @url: https://blog.csdn.net/choimroc/article/details/104756365
     */
    protected val binding: VB by lazy {
        BindingReflex.reflexViewBinding(javaClass, layoutInflater)
    }

    /**
     * `TAG` 为对应Fragment的 `javaClass.simpleName`
     */
    protected val TAG: String = javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        initViewAndData()
        initLiveData()
        // 方法1  扩展函数doOnPreDraw
        // (view.parent as? ViewGroup)?.doOnPreDraw {
        //     startPostponedEnterTransition()
        // }
        // 方法2 doOnPreDraw = OneShotPreDrawListener.add()
        (view.parent as? ViewGroup)?.apply {
            OneShotPreDrawListener.add(this) {
                startPostponedEnterTransition()
            }
        }
    }

    /**
     * 处理页面和数据请求
     */
    abstract fun initViewAndData()

    /**
     * 处理数据监听
     */
    abstract fun initLiveData()

}
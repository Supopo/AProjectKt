package com.zoo.xxx.base

import android.view.View
import androidx.databinding.ViewDataBinding
import com.zoo.mvvmkt.util.inflateBindingWithGeneric
import com.zoo.mvvmkt.viewmodel.BaseViewModel

/**
 * 描述　: 包含 ViewModel 和 ViewBinding ViewModelActivity基类，把ViewModel 和 ViewBinding 注入进来了
 * 需要使用 Databind 的请继承它
 */
abstract class MBaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : MBaseVmActivity<VM>() {

    override fun layoutId() = 0

    lateinit var mDatabind: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        return mDatabind.root
    }
}
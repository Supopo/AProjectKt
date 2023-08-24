package com.zoo.xxx.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.zoo.mvvmkt.base.fragment.BaseVmDbFragment
import com.zoo.mvvmkt.util.dismissLoadingExt
import com.zoo.mvvmkt.util.showLoadingExt
import com.zoo.mvvmkt.viewmodel.BaseViewModel


abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbFragment<VM,DB>() {
    abstract override fun initView(savedInstanceState: Bundle?)
    override fun createObserver() {}
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }
    override fun dismissLoading() {
        dismissLoadingExt()
    }

}
package com.zoo.xxx.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.zoo.mvvmkt.util.dismissLoadingExt
import com.zoo.mvvmkt.util.showLoadingExt
import com.zoo.mvvmkt.viewmodel.BaseViewModel


abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : MBaseVmDbActivity<VM,DB>() {
    abstract override fun initView(savedInstanceState: Bundle?)
    override fun createObserver() {}
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }
    override fun dismissLoading() {
        dismissLoadingExt()
    }

}
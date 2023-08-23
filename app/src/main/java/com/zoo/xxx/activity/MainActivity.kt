package com.zoo.xxx.activity

import android.os.Bundle
import android.view.View
import com.zoo.xxx.viewmodel.MainViewModel
import com.zoo.xxx.base.BaseActivity
import com.zoo.xxx.databinding.ActivityMainBinding


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {

    }


    fun onButtonClick(view: View?) {
        // 处理点击事件逻辑

    }
}
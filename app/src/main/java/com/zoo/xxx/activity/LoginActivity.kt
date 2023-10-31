package com.zoo.xxx.activity

import android.os.Bundle
import android.view.View
import com.zoo.mvvmkt.util.ActivityMessenger
import com.zoo.xxx.base.BaseActivity
import com.zoo.xxx.bean.UserInfo
import com.zoo.xxx.databinding.ActivityLoginBinding
import com.zoo.xxx.utils.CacheUtil
import com.zoo.xxx.viewmodel.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<MainViewModel, ActivityLoginBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun createObserver() {
    }

    fun onButtonClick(view: View) {
        CacheUtil.setIsLogin(true)
        CacheUtil.setUser(UserInfo(userName = "张三"))

        GlobalScope.launch {
            delay(2000)
            ActivityMessenger.startActivity<MainActivity>(this@LoginActivity)
            finish()
        }
    }


}
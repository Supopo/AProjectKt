package com.zoo.xxx.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.zoo.mvvmkt.util.ActivityMessenger
import com.zoo.xxx.base.BaseActivity
import com.zoo.xxx.databinding.ActivitySplashBinding
import com.zoo.xxx.utils.CacheUtil
import com.zoo.xxx.viewmodel.MainViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<MainViewModel, ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        //获取缓存标记看是否登陆了
        val isLogin = CacheUtil.isLogin()
        if (isLogin) {
            val userInfo = CacheUtil.getUser()
            Log.e("-----", "${userInfo?.userName}")
            ActivityMessenger.startActivity<MainActivity>(this@SplashActivity)
        } else {
            ActivityMessenger.startActivity<LoginActivity>(this@SplashActivity)
        }
        finish()
    }

    override fun createObserver() {
    }

}
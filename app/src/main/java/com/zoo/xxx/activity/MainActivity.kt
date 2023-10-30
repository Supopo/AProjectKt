package com.zoo.xxx.activity

import android.os.Bundle
import android.widget.Toast
import com.zoo.xxx.base.BaseActivity
import com.zoo.xxx.databinding.ActivityMainBinding
import com.zoo.xxx.viewmodel.MainViewModel


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {

    }


    private var firstClickTime: Long = 0
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - firstClickTime
        if (duration in 0..2000) {
            finish()
        } else {
            firstClickTime = currentTime
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show()
        }
    }
}
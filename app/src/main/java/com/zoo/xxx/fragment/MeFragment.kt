package com.zoo.xxx.fragment

import android.os.Bundle
import android.util.Log
import com.zoo.mvvmkt.ext.nav
import com.zoo.mvvmkt.ext.navigateAction
import com.zoo.mvvmkt.util.ActivityMessenger
import com.zoo.mvvmkt.util.ActivityMessenger.finish
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.R
import com.zoo.xxx.activity.LoginActivity
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.bean.UserInfo
import com.zoo.xxx.databinding.FragmentMeBinding
import com.zoo.xxx.utils.CacheUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by SuperPer'GPT on 2023/08/24.
 */
class MeFragment : BaseFragment<BaseViewModel, FragmentMeBinding>() {
    private val TAG = "MeFragment"

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.click = ProxyClick()
    }

    override fun lazyLoadData() {

    }

    inner class ProxyClick {
        fun toLogin() {
            ActivityMessenger.startActivity<LoginActivity>(this@MeFragment)
        }

        fun toSetting() {
            nav().navigateAction(R.id.to_fragment_setting)
        }
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(TAG, "hidden: $hidden")
    }
}
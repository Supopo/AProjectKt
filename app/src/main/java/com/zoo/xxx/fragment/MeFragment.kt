package com.zoo.xxx.fragment

import android.os.Bundle
import android.util.Log
import com.zoo.mvvmkt.ext.nav
import com.zoo.mvvmkt.ext.navigateAction
import com.zoo.mvvmkt.util.ActivityMessenger
import com.zoo.xxx.R
import com.zoo.xxx.activity.LoginActivity
import com.zoo.xxx.appViewModel
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.databinding.FragmentMeBinding
import com.zoo.xxx.viewmodel.MeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by SuperPer'GPT on 2023/08/24.
 */
class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {
    private val TAG = "MeFragment"

    override fun initView(savedInstanceState: Bundle?) {
        //很重要！！！别忘记
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
    }

    override fun lazyLoadData() {
        appViewModel.userInfo.value?.let {
            //要使用mViewModel.name，先绑定xml和viewModel
            mViewModel.name.set(it.userName!!.ifEmpty { it.userName!! })
        }

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
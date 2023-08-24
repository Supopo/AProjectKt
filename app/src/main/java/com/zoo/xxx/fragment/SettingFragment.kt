package com.zoo.xxx.fragment

import android.os.Bundle
import android.util.Log
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.databinding.FragmentSettingBinding

/**
 * Created by SuperPer'GPT on 2023/08/24.
 */
class SettingFragment : BaseFragment<BaseViewModel, FragmentSettingBinding>() {
    private val TAG = "SettingFragment"

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun lazyLoadData() {

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
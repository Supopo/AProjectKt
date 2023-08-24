package com.zoo.xxx.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.zoo.mvvmkt.ext.init
import com.zoo.mvvmkt.ext.initMain
import com.zoo.mvvmkt.ext.interceptLongClick
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.R
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.databinding.FragmentMainBinding

/**
 * Created by SuperPer'GPT on 2023/08/24.
 * 当前Fragment的生命周期和MainActivity同步: onPause onResume
 */
class MainFragment : BaseFragment<BaseViewModel, FragmentMainBinding>() {
    private val TAG = "MainFragment"

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        val fragments = ArrayList<Fragment>()
        fragments.add(HomeFragment())
        fragments.add(ListFragment())
        fragments.add(MeFragment())
        mDatabind.viewPager2.initMain(this, fragments)

        //初始化 bottomBar
        mDatabind.bottomNavigationView.init {
            when (it) {
                R.id.menu_main -> mDatabind.viewPager2.setCurrentItem(0, false)
                R.id.menu_list -> mDatabind.viewPager2.setCurrentItem(1, false)
                R.id.menu_me -> mDatabind.viewPager2.setCurrentItem(2, false)
            }
        }

        //拦截BottomNavigation长按事件 防止长按时出现Toast
        mDatabind.bottomNavigationView.interceptLongClick(
            R.id.menu_main,
            R.id.menu_list,
            R.id.menu_me
        )
    }

    override fun lazyLoadData() {

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
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
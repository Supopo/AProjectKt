package com.zoo.xxx.fragment

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.zoo.mvvmkt.widget.CustomLoadMoreAdapter
import com.zoo.xxx.R
import com.zoo.xxx.adapter.HeaderAdapter
import com.zoo.xxx.adapter.ListAdapter
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.databinding.FragmentListBinding
import com.zoo.xxx.viewmodel.ListViewModel

/**
 * Created by SuperPer'GPT on 2023/08/24.
 */
class ListFragment : BaseFragment<ListViewModel, FragmentListBinding>() {
    private val TAG = "ListFragment"
    private val mAdapter: ListAdapter by lazy { ListAdapter() }
    private lateinit var helper: QuickAdapterHelper
    private var pageNum = 1
    private val pageSize = 10

    override fun initView(savedInstanceState: Bundle?) {
        initRefresh()
        initAdapter()
        refresh()
    }

    private fun initAdapter() {
        //初始化RecyclerView
        val layoutManger = LinearLayoutManager(activity)
        mDatabind.rvContent.layoutManager = layoutManger

        // 加载更多，第一步，创建 mAdapter
        // 实例化自定义"加载更多"的adapter类
        val loadMoreAdapter = CustomLoadMoreAdapter()
        // 第二部，使用 Builder 创建 QuickAdapterHelper 对象，这里需要传入你的 mAdapter
        loadMoreAdapter.setOnLoadMoreListener(object : TrailingLoadStateAdapter.OnTrailingListener {
            override fun onLoad() {
                // 执行加载更多的操作，通常都是网络请求
                request()
            }

            override fun onFailRetry() {
                // 加载失败后，点击重试的操作，通常都是网络请求
                request()
            }

            override fun isAllowLoading(): Boolean {
                // 是否允许触发“加载更多”，通常情况下，下拉刷新的时候不允许进行加载更多
                return !mDatabind.refreshLayout.isRefreshing
            }
        })

        helper = QuickAdapterHelper.Builder(mAdapter)
            // 加载更多
            .setTrailingLoadStateAdapter(loadMoreAdapter)
            .build()

        /**
         * 第三步，给 RecyclerView 设置 Adapter，
         * 注意：这个adapter不是前面创建的 mAdapter，而是 helper 所提供的 adapter（ConcatAdapter）
         */
        mDatabind.rvContent.adapter = helper.adapter

        //添加头布局
        helper.addBeforeAdapter(HeaderAdapter())

        //Adapter子View点击事件
        mAdapter.setOnItemClickListener() { _, _, position ->
            //跳转页面

        }

        mAdapter.addOnItemChildClickListener(R.id.ll_root) { _, _, position ->
            //跳转页面

        }
    }

    private fun initRefresh() {
        //设置下拉距离
        mDatabind.refreshLayout.setDistanceToTriggerSync(100)
        //监听 SwipeRefreshLayout
        mDatabind.refreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh() {
        pageNum = 1
        // 重置“加载更多”时状态
        helper.trailingLoadState = LoadState.None
        request()
    }

    fun request() {
        //请求数据
        mViewModel.getDataList(pageNum)
    }

    override fun createObserver() {
        //往adapter里面加载数据
        mViewModel.dataList.observe(this) { dataList ->
            if (dataList != null) {
                if (pageNum == 1) {
                    mAdapter.submitList(dataList)
                } else {
                    mAdapter.addAll(dataList)
                }

                // 如果在数据不满足一屏时，暂停加载更多，请调用下面方法
                helper.trailingLoadStateAdapter?.checkDisableLoadMoreIfNotFullPage()

                if (dataList.size < pageSize) {
                    // 没有分页数据了
                    /*
                    Set the status to not loaded, and there is no paging data.
                    设置状态为未加载，并且没有分页数据了
                    */
                    helper.trailingLoadState = LoadState.NotLoading(true)
                } else {
                    // 后续还有分页数据
                    /*
                    Set the state to not loaded, and there is also paginated data
                    设置状态为未加载，并且还有分页数据
                    */
                    helper.trailingLoadState = LoadState.NotLoading(false)
                }
                pageNum++
            }
        }

        //改变刷新状态
        mViewModel.reqState.observe(this) { reqState ->
            when (reqState) {
                2 -> mDatabind.refreshLayout.isRefreshing = false
                3 -> {
                    mDatabind.refreshLayout.isRefreshing = false
                    helper.trailingLoadState = LoadState.Error(RuntimeException("load fail"))
                }
            }
        }
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
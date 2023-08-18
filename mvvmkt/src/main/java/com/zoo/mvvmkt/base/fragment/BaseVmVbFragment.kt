package com.zoo.mvvmkt.base.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zoo.mvvmkt.util.inflateBindingWithGeneric
import com.zoo.mvvmkt.viewmodel.BaseViewModel


/**
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment和 ViewBinding 注入进来了
 * 需要使用 ViewBinding 的请继承它
 */
abstract class BaseVmVbFragment<VM : BaseViewModel, VB : ViewBinding> : BaseVmFragment<VM>() {

    override fun layoutId() = 0

    //该类绑定的 ViewBinding
    private var _binding: VB? = null
    val mViewBind: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding  = inflateBindingWithGeneric(inflater,container,false)
        return mViewBind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
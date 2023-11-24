package com.zoo.xxx.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener
import com.luck.picture.lib.interfaces.OnRequestPermissionListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.zoo.mvvmkt.ext.nav
import com.zoo.mvvmkt.ext.navigateAction
import com.zoo.mvvmkt.util.ActivityMessenger
import com.zoo.mvvmkt.util.FileUtil
import com.zoo.mvvmkt.util.GlideEngine
import com.zoo.xxx.R
import com.zoo.xxx.activity.LoginActivity
import com.zoo.xxx.appViewModel
import com.zoo.xxx.base.BaseFragment
import com.zoo.xxx.databinding.FragmentMeBinding
import com.zoo.xxx.viewmodel.MeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

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

        fun toSelectPhoto(){
            checkPic()
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

    //选择图片
    private fun checkPic() {

        // 进入相册

        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(1)
            .setPermissionsInterceptListener(object :OnPermissionsInterceptListener{
                override fun requestPermission(
                    fragment: Fragment?,
                    permissionArray: Array<out String>?,
                    call: OnRequestPermissionListener?
                ) {
                    // 1、发起权限申请
                    // 2、call.onCall();返回申请状态
                }

                override fun hasPermissions(
                    fragment: Fragment?,
                    permissionArray: Array<out String>?
                ): Boolean {
                    // 验证权限是否申请成功
                    return false
                }

            })
            .setSandboxFileEngine(object : UriToFileTransformEngine {
                override fun onUriToFileAsyncTransform(
                    context: Context?,
                    srcPath: String?,
                    mineType: String?,
                    call: OnKeyValueResultCallbackListener?
                ) {
                    if (call != null) {
                        val sandboxPath =
                            SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                        call.onCallback(srcPath, sandboxPath)
                    }
                }
            })
            .setCompressEngine(object : CompressFileEngine {
                override fun onStartCompress(
                    context: Context?,
                    source: java.util.ArrayList<Uri>?,
                    call: OnKeyValueResultCallbackListener?
                ) {
                    //压缩
                    Luban.with(context).load(source).ignoreBy(100)
                        .setCompressListener(object : OnNewCompressListener {
                            override fun onStart() {
                            }

                            override fun onSuccess(source: String?, compressFile: File?) {
                                call?.onCallback(source, compressFile!!.getAbsolutePath())
                            }

                            override fun onError(source: String?, e: Throwable?) {
                                call?.onCallback(source, null)
                            }
                        }).launch()
                }
            })
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>) {

                    for (localMedia in result) {

                        //获取源路径 但在Android Q版本上返回的是content:// Uri类型
                        val path = FileUtil.getFilePath(activity, Uri.parse(localMedia!!.path))
                        var compressedPath = path
                        //如果有压缩去压缩后路径
                        if (localMedia.isCompressed) {
                            compressedPath = localMedia.compressPath
                        }

                        runOnUiThread {
                            Glide.with(this@MeFragment)
                                .load(
                                    compressedPath
                                )
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(
                                    mDatabind.ivPhoto
                                )
                        }
                    }
                }

                override fun onCancel() {

                }
            })
    }
}
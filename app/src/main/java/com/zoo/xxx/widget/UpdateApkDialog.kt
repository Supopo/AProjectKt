package com.zoo.xxx.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.zoo.mvvmkt.util.StoragePermissionUtil
import com.zoo.xxx.BuildConfig
import com.zoo.xxx.Constans
import com.zoo.xxx.R
import com.zoo.xxx.bean.VersionInfo
import com.zoo.xxx.databinding.LayoutUpdateApkDialogBinding
import com.zoo.xxx.utils.DownloadApkUtil
import com.zoo.xxx.utils.DownloadListener
import java.io.File


/**
 * 应用升级弹窗
 *
 * 在activity中调用StoragePermissionUtil.registerForActivityResult(this)
 * 然后正常展示弹窗就行
 *
 */
class UpdateApkDialog(
    private val activity: FragmentActivity,
    private val versionInfo: VersionInfo,
) :
    Dialog(activity, R.style.CustomDialogStyle) {
    lateinit var binding: LayoutUpdateApkDialogBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)


        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.layout_update_apk_dialog, null, false)
        setContentView(binding.root)
        binding.tvContent.text = "发现新版本 ${versionInfo.packageVersion} 是否更新？"

        binding.tvConfirm.setOnClickListener {
            checkPermission()
        }
        binding.tvCancle.setOnClickListener {
            downloadApkUtil?.stopDownload()
            dismiss()
        }
    }

    //修改Dialog方法，让弹窗展示时候虚拟导航栏不会出现
    override fun show() {
        //在show之前添加禁止获取焦点
        this.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        super.show()

        val decorView = this.window?.decorView

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        decorView?.systemUiVisibility = flags

        //在show之后取消禁止获取焦点属性，否则会导致dialog无法处理点击
        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private var downloadApkUtil: DownloadApkUtil? = null
    private var downThread: Thread? = null
    private val savePath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    private var apkPath: String? = null


    private fun checkPermission() {
        //检测文件读写权限
        StoragePermissionUtil.requestStoragePermission(activity) {
            //检测安装权限
            StoragePermissionUtil.requestInstallPermission(activity) {
                downAPK()
            }
        }
    }

    private fun downAPK() {
        if (!TextUtils.isEmpty(apkPath)) {
            openAPK(apkPath!!)
            return
        }

        if (downloadApkUtil == null) {
            downloadApkUtil =
                DownloadApkUtil(
                    context,
                    versionInfo.packagePath,
                    Constans.APK_NAME,
                    savePath
                )
        }

        downloadApkUtil!!.setListener(object : DownloadListener {
            @SuppressLint("SetTextI18n")
            override fun onProgress(progress: Int) {
                activity.runOnUiThread {
                    binding.llProgress.isVisible = true
                    binding.tvContent.text = "正在下载..."
                    binding.tvProgress.text = "${progress}%"
                    binding.progressBar.progress = progress
                }

            }

            override fun onError() {
                activity.runOnUiThread {
                    binding.tvContent.text = "下载失败！"
                }
            }

            override fun onSuccess(filePath: String) {
                activity.runOnUiThread {
                    binding.tvContent.text = "下载完成！"
                    apkPath = filePath
                    //跳转安装
                    openAPK(filePath)
                }
            }

        })

        downThread = Thread(downloadApkUtil)
        downThread!!.start()


    }

    private fun openAPK(apkPath: String) {
        val apkFile = File(apkPath)

        //修改文件读写权限
        try {
            val command = "chmod 755 ${apkFile.absolutePath}"
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val intent = Intent(Intent.ACTION_VIEW)
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For Android 7.0 and above, use a content URI
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            uri = FileProvider.getUriForFile(
                activity,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                apkFile
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            // For Android 6.0 and below, use a file URI
            uri = Uri.parse("file://$apkPath")
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }
}
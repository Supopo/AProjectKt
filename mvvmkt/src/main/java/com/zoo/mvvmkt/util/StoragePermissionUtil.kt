package com.zoo.mvvmkt.util

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

/**
 * Created by SuperPer'GPT on 2023/11/2.
 * 文件权限获取
 * eg:
 *    StoragePermissionUtil.registerForActivityResult(this)
 *    StoragePermissionUtil.requestStoragePermission(this) {   //todo已获取到文件权限  }
 */
object StoragePermissionUtil {

    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var android11StoragePermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultCallback: () -> Unit

    /**
     * 需求：
     * androidx.activity，1.2.0 或更高版本。
     * androidx.fragment，1.3.0 或更高版本。
     * 示例如下：
     * implementation "androidx.activity:activity-ktx:1.3.0"
     * implementation "androidx.fragment:fragment-ktx:1.3.6"
     */
    fun registerForActivityResult(activity: FragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11或更高的版本
            android11StoragePermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (::resultCallback.isInitialized) {
                    requestStoragePermission(activity, resultCallback)
                }
            }
        } else {
            // Android 10或更低的版本
            storagePermissionLauncher =
                activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        Log.i("ABCD", "此手机是版本低于Android 11，且已获得存储权限")
                        resultCallback()
                    } else {
                        Log.i("ABCD", "此手机是版本低于Android 11，且没有存储权限")

                        val desc = if (ActivityCompat.shouldShowRequestPermissionRationale(
                                activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            // 权限被拒绝
                            """本应用需要获取"存储"权限，请给予此权限，否则无法使用本应用"""
                        } else {
                            // 权限被设置为不再提示
                            """本App需要使用"存储"权限，您需要到设置中打开此权限，否则无法使用本app"""
                        }
                        showDialog(activity, desc) {
                            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
        }
    }

    /** 请求存储权限 */
    fun requestStoragePermission(activity: FragmentActivity, resultCallback: () -> Unit) {
        this.resultCallback = resultCallback
        Log.i("ABCD", "当前手机版本：API ${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
            if (Environment.isExternalStorageManager()) {
                Log.i("ABCD", "此手机是Android 11或更高的版本，且已获得访问所有文件权限")
                resultCallback()
            } else {
                Log.i("ABCD", "此手机是Android 11或更高的版本，且没有访问所有文件权限")
                showDialog(
                    activity,
                    """本应用需要获取"访问所有文件"权限，请给予此权限，否则无法使用本应用"""
                ) {
                    android11StoragePermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                }
            }
        } else {
            // Android 10或更低的版本，申请存储权限
            Log.i("ABCD", "此手机是版本低于Android 11，开始申请存储权限")
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showDialog(activity: FragmentActivity, message: String, okClick: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定") { _, _ -> okClick() }
            .setCancelable(false)
            .show()
    }

    /** 请求安装权限 */
    fun requestInstallPermission(activity: FragmentActivity, resultCallback: () -> Unit) {
        this.resultCallback = resultCallback
        Log.i("ABCD", "当前手机版本：API ${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hasInstallPermission =
                activity.packageManager.canRequestPackageInstalls()


            // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
            if (hasInstallPermission) {
                Log.i("ABCD", "已获得安装权限")
                resultCallback()
            } else {
                Log.i("ABCD", "没有安装权限")
                showDialog(
                    activity,
                    """本应用需要允许"安装未知应用"权限，请给予此权限，否则无法使用本应用"""
                ) {
                    //跳转至“安装未知应用”权限界面，引导用户开启权限
                    val selfPackageUri =
                        Uri.parse("package:" + activity.packageName)
                    val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        selfPackageUri
                    )
                    android11StoragePermissionLauncher.launch(intent)
                }
            }
        }
    }
}
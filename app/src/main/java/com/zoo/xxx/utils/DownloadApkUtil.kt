package com.zoo.xxx.utils

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by SuperPer'GPT on 2023/9/2.
 * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
 * 上面地址为文件管理中的Down文件夹
 *
 *    val downThread = Thread(downloadApkUtil)
 *     downThread!!.start()
 *
 *     downloadApkUtil!!.setListener(object : DownloadListener {
 *     override fun onProgress(progress: Int) {
 *     }
 *
 *     override fun onError() {
 *     }
 *
 *     override fun onSuccess(filePath: String) {
 *     }
 *     })
 *
 */
internal class DownloadApkUtil(
    var context: Context,
    var url: String,
    var apkName: String,
    var savePath: String
) : Runnable {
    var inputStream: InputStream? = null
    var fos: FileOutputStream? = null
    var isStop = false

    fun stopDownload() {
        isStop = true
    }

    // 定义一个监听器属性
    private var listener: DownloadListener? = null

    // 注册监听器
    fun setListener(listener: DownloadListener) {
        this.listener = listener
    }

    override fun run() {
        //判断文件存在不存在
        delApkFile()

        val client = OkHttpClient()
        val request = Request.Builder().get().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("======", "开始下载了。")
                //获取内容总长度
                val contentLength = response.body()!!.contentLength()
                //设置最大值(百分比)
                //默认设置100
                val apkFile = File(
                    savePath,
                    apkName
                )
                fos = FileOutputStream(apkFile)
                //获得输入流
                inputStream = response.body()!!.byteStream()
                //定义缓冲区大小
                val bys = ByteArray(1024)
                var progress = 0
                var len = -1
                while (inputStream!!.read(bys).also { len = it } != -1) {
                    try {
                        if (isStop) {
                            Log.e("======", "下载停止！")
                            closeIO()
                            delApkFile()
                            return
                        }
                        Thread.sleep(1)
                        fos!!.write(bys, 0, len)
                        fos!!.flush()
                        progress += len
                        //设置进度
                        val n = (progress.toDouble() / contentLength) * 100
                        listener?.onProgress(n.toInt())
                        Log.d("======", "下载中：$n")
                    } catch (_: InterruptedException) {
                    }
                }
                listener?.onSuccess(apkFile.path)
                Log.d("======", "下载结束了。" + apkFile.path)
            } else {
                listener?.onError()
                Log.e("======", "下载失败！")
            }
        } catch (e: IOException) {
            listener?.onError()
            Log.e("======", "下载失败！")
        } finally {
            closeIO()
        }
    }

    private fun delApkFile() {
        val file = File(savePath, apkName)
        if (file.exists()) {
            //如果存在先删除
            if (file.delete()) {
                Log.d("======", "删除安装包！")
            }
        }
    }

    private fun closeIO() {
        //关闭io流
        if (inputStream != null) {
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            inputStream = null
        }
        if (fos != null) {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            fos = null
        }
    }
}

interface DownloadListener {
    fun onProgress(progress: Int)
    fun onError()
    fun onSuccess(filePath: String)
}
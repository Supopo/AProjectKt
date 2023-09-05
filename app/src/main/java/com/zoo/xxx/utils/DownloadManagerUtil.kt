package com.zoo.xxx.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Android自带下载，任务栏展示
 */
class DownloadManagerUtil(private val context: Context) {

    private val downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private val _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> = _progressLiveData

    private val _statusLiveData = MutableLiveData<DownloadStatus>()
    val statusLiveData: LiveData<DownloadStatus> = _statusLiveData

    private val _fileLiveData = MutableLiveData<String>()
    val fileLiveData: LiveData<String> = _fileLiveData

    private var downloadId: Long = -1

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                        val columnIndexUri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val uri = cursor.getString(columnIndexUri)
                        _statusLiveData.postValue(DownloadStatus.Success)
                        _fileLiveData.postValue(uri)
                    } else {
                        val columnIndexReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        val reason = cursor.getInt(columnIndexReason)
                        _statusLiveData.postValue(DownloadStatus.Failure(reason))
                    }
                }
                cursor.close()
            }
        }
    }

    @SuppressLint("Range")
    private val queryRunnable = Runnable {
        var downloading = true
        while (downloading) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            val cursor: Cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                    _statusLiveData.postValue(DownloadStatus.Success)
                    downloading = false
                }
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val progress = (bytesDownloaded * 100 / bytesTotal)
                Log.d("----------", "bytesDownloaded：" + bytesDownloaded)
                Log.d("----------", "进度：" + progress)
                _progressLiveData.postValue(progress)
            }
            cursor.close()
        }
    }

    fun downloadApk(apkUrl: String, apkName: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl))
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Downloading $apkName")
        request.setDescription("Downloading $apkName")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName)

        downloadId = downloadManager.enqueue(request)

        // Register BroadcastReceiver to listen for download completion
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, filter)

        Thread(queryRunnable).start()
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(downloadReceiver)
    }

    sealed class DownloadStatus {
        object Success : DownloadStatus()
        data class Failure(val reason: Int) : DownloadStatus()
    }
}



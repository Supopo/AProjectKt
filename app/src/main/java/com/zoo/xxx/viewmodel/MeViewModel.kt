package com.zoo.xxx.viewmodel

import android.text.TextUtils
import com.zoo.mvvmkt.callback.databind.StringObservableField
import com.zoo.mvvmkt.ext.request
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.network.apiService
import com.zoo.xxx.utils.showToast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class MeViewModel : BaseViewModel() {

    var name = StringObservableField("请先登录~")

    fun uploadImage(imageUrl: String?) {
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }
        val file = File(imageUrl!!)
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        builder.addFormDataPart(
            "file",
            file.name,
            RequestBody.create(MediaType.parse("image/*"), file)
        )


        request({ apiService.uploadImage(builder.build()) }, {

        }, {
            it.message?.showToast()
        }, true, "正在上传...")
    }
}

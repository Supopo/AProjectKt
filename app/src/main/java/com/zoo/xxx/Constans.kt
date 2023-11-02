package com.zoo.xxx

/**
 * Created by SuperPer'GPT on 2023/10/17.
 */
class Constans {
    companion object {
        @JvmField
        val APK_NAME =
            if (BuildConfig.IS_DEV) "shoring_dev.apk" else "shoring.apk"

    }
}
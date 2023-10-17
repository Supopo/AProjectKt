package com.zoo.mvvmkt.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import java.net.NetworkInterface
import java.util.UUID

/**
 * 获取系统相关
 */
object SystemUtil {

    @SuppressLint("HardwareIds")
    fun getDeviceUUID(context: Context): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getRandomNonce(): String {
        // 使用 UUID 随机生成一个字符串
        return UUID.randomUUID().toString()
    }

    fun getMacAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val macAddress = networkInterface.hardwareAddress
                if (macAddress != null && macAddress.isNotEmpty()) {
                    val stringBuilder = StringBuilder()
                    for (byte in macAddress) {
                        stringBuilder.append(String.format("%02X:", byte))
                    }
                    if (stringBuilder.isNotEmpty()) {
                        stringBuilder.deleteCharAt(stringBuilder.length - 1)
                    }
                    return stringBuilder.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getAppVersionName(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getAppVersionCode(context: Context): Int? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getPadInformation(): String {
        val sb = StringBuilder()
        sb.append("Device Manufacturer: ${Build.MANUFACTURER}\n")
        sb.append("Device Model: ${Build.MODEL}\n")
        sb.append("Android Version: ${Build.VERSION.RELEASE}\n")
        sb.append("SDK Version: ${Build.VERSION.SDK_INT}\n")
        return sb.toString()
    }

    fun getPadInformationModel(): String {
        return Build.MODEL.toString()
    }

    @SuppressLint("HardwareIds")
    fun getSerialNumber(): String? {
        return  Build.SERIAL
    }
}
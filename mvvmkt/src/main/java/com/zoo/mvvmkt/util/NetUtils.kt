package com.zoo.mvvmkt.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager

/**
 * 判断网络信号
 */
object NetUtils {
    const val NETWORK_NONE = 0 // 没有网络连接
    const val NETWORK_WIFI = 1 // wifi连接
    const val NETWORK_2G = 2 // 2G
    const val NETWORK_3G = 3 // 3G
    const val NETWORK_4G = 4 // 4G
    const val NETWORK_5G = 5 // 5G
    const val NETWORK_MOBILE = 9 // 手机流量

    /**
     * 获取运营商名字
     *
     * @param context context
     * @return int
     */
    fun getOperatorName(context: Context): String {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.simOperatorName
    }

    /**
     * 获取当前网络连接的类型
     *
     * @param context context
     * @return int
     */
    fun getNetworkState(context: Context): Int {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: // 为空则认为无网络
                return NETWORK_NONE // 获取网络服务
        // 获取网络类型，如果为空，返回无网络
        val activeNetInfo = connManager.activeNetworkInfo
        if (activeNetInfo == null || !activeNetInfo.isAvailable) {
            return NETWORK_NONE
        }
        // 判断是否为WIFI
        val wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (null != wifiInfo) {
            val state = wifiInfo.state
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI
                }
            }
        }
        // 若不是WIFI，则去判断是2G、3G、4G网
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkType = telephonyManager.networkType
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G
            TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G
            TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
            TelephonyManager.NETWORK_TYPE_NR -> NETWORK_5G
            else -> NETWORK_MOBILE
        }
    }

    /**
     * 判断网络是否连接
     *
     * @param context context
     * @return true/false
     */
    fun isNetConnected(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                if (info.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 判断是否wifi连接
     *
     * @param context context
     * @return true/false
     */
    @Synchronized
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null) {
            val networkInfoType = networkInfo.type
            if (networkInfoType == ConnectivityManager.TYPE_WIFI || networkInfoType == ConnectivityManager.TYPE_ETHERNET) {
                return networkInfo.isConnected
            }
        }
        return false
    }

    fun getNetworkWifiLevel(context: Context): Int {
        if (!isWifiConnected(context)) {
            return 0
        }
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        //获得信号强度值
        val level = wifiInfo.rssi
        //根据获得信号的强度发送信息
        return if (level <= 0 && level >= -50) { //最强
            1
        } else if (level < -50 && level >= -70) { //较强
            2
        } else if (level < -70 && level >= -80) { //较弱
            3
        } else if (level < -80 && level >= -100) { //微弱
            4
        } else {
            5
        }
    }
}
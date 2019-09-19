package com.sydev.vpnsyria.utilties

import android.content.Context
import android.net.ConnectivityManager
import com.sydev.vpnsyria.VPNApp

class NetworkState {
    companion object{
        fun isOnline(): Boolean {
            val cm = VPNApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
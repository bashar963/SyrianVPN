package com.sydev.vpnsyria.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener

import com.sydev.vpnsyria.VPNApp
import com.sydev.vpnsyria.utilties.Constants
import com.sydev.vpnsyria.utilties.Constants.Companion.BASE_FILE_NAME
import com.sydev.vpnsyria.utilties.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.concurrent.TimeUnit




class LoaderViewModel(application: Application):AndroidViewModel(application) {

    private val serverRepository = getApplication<VPNApp>().getServerRepository()

    var loadingState : MutableLiveData<Int> = MutableLiveData()


    fun downloadCSVFile(){

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        AndroidNetworking.download(BASE_URL, getApplication<VPNApp>().cacheDir.path, BASE_FILE_NAME)
            .setTag("downloadCSV")
            .setPriority(Priority.IMMEDIATE)
            .setOkHttpClient(okHttpClient)
            .build()

            .startDownload(object : DownloadListener{
                override fun onDownloadComplete() {
                    parseCSVFile(BASE_FILE_NAME)
                }

                override fun onError(anError: ANError?) {
                    loadingState.value=Constants.LOAD_ERROR
                }
            })
 }

    private fun parseCSVFile(fileName: String) {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader(getApplication<VPNApp>().cacheDir.path+"/"+fileName))
        } catch (e: IOException) {
            e.printStackTrace()
            loadingState.value=Constants.LOAD_ERROR
        }

        if (reader != null) {
            try {
                val startLine = 2
                val type = 0

                serverRepository.clearTable()

                var counter = 0

                reader.readLines().forEach {
                    if (counter >= startLine) {
                        serverRepository.insertLine(it, type)
                    }
                    counter++
                }
                loadingState.value=Constants.LOADING_SUCCESS

            } catch (e: Exception) {
                e.printStackTrace()
                loadingState.value=Constants.LOAD_ERROR
            }

        }
    }

}
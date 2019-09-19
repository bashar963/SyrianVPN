package com.sydev.vpnsyria.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.utilties.Constants
import com.sydev.vpnsyria.utilties.NetworkState
import com.sydev.vpnsyria.ui.viewmodels.LoaderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import net.frakbot.jumpingbeans.JumpingBeans


// this Activity will work as Splash Activity

class MainActivity : AppCompatActivity() {

    private lateinit var loaderViewModel: LoaderViewModel
   // private var autoUpdateState = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeFullScreen)
        setContentView(R.layout.activity_main)
        loaderViewModel = ViewModelProviders.of(this).get(LoaderViewModel::class.java)
        //autoUpdateState = ServerPreferences.getAutoUpdateState()
        progressBar.start()
        checkNetworkState()
        checkLoadingState()

       JumpingBeans.with(textView6)
           .appendJumpingDots()
           .build()

        adView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.visibility = View.VISIBLE
            }
        }
    }

    private fun checkLoadingState() {
        loaderViewModel.loadingState.observe(this, Observer{
            when(it){
                Constants.LOAD_ERROR->{
                }
                Constants.LOADING_SUCCESS->{
                    Handler().postDelayed( {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra(Constants.fromSplash,true)
                        startActivity(intent)
                        finish()
                    },750)
                }
            }
        })
    }

    private fun checkNetworkState() {
        if (NetworkState.isOnline()) {
            adView.loadAd(AdRequest.Builder().build())
           /* if (!autoUpdateState) {
                if (ServerPreferences.isExpired()){
                    ServerPreferences.setExpirationDate()
                    loaderViewModel.downloadCSVFile()
                }else{
                    val myIntent = Intent(this, HomeActivity::class.java)
                    startActivity(myIntent)
                    finish()
                }

            } else {
                // do load
                loaderViewModel.downloadCSVFile()
            }*/
            loaderViewModel.downloadCSVFile()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.network_error))
                .setMessage(getString(R.string.network_error_message))
                .setNegativeButton(getString(R.string.ok)
                ) { dialog, _ ->
                    dialog.cancel()
                    onBackPressed()
                }
            val alert = builder.create()
            alert.show()
        }
    }
}

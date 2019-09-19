package com.sydev.vpnsyria.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.*
import android.net.Uri
import android.net.VpnService
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pixplicity.generate.Rate
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.ui.activities.MainActivity
import com.sydev.vpnsyria.utilties.Constants.Companion.START_VPN_PROFILE
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.android.synthetic.main.home_fragment.*
import com.sydev.vpnsyria.ui.viewmodels.HomeViewModel
import com.sydev.vpnsyria.utilties.Constants
import com.sydev.vpnsyria.utilties.DataTraffic
import com.sydev.vpnsyria.utilties.ServerPreferences
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private lateinit var rate:Rate
    private lateinit var mInterstitialAd: InterstitialAd
    private var profileValueHasChanged=false
    private lateinit var trafficReceiver: BroadcastReceiver
    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adView.loadAd(AdRequest.Builder().build())
        mInterstitialAd = InterstitialAd(this.activity)
        mInterstitialAd.adUnitId = "ca-app-pub-9576521051407477/6867742282"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        rateMyAppDialog()
        rate.count()
        trafficReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                receiveTraffic( intent)
            }

        }
        this.activity!!.registerReceiver(trafficReceiver, IntentFilter(DataTraffic.TRAFFIC_ACTION))

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        initButtons()
        if (activity!!.intent != null){
            if (activity!!.intent.getStringExtra(Constants.KEY_COUNTRY_LONG)==getString(R.string.Auto_select_server)){
                viewModel.countryName.value = "all"
                viewModel.fromServerList.value = activity!!.intent.getBooleanExtra(Constants.From_Server_List,false)
            }else{
                viewModel.countryName.value = activity!!.intent.getStringExtra(Constants.KEY_COUNTRY_LONG)
                viewModel.fromServerList.value = activity!!.intent.getBooleanExtra(Constants.From_Server_List,false)
            }

        }
        if (ServerPreferences.getConnectOnStart()){
            if (!VpnStatus.isVPNActive()&& viewModel.isConnected.value==0)
                viewModel.connectToServer()
        }

    }
    private fun rateMyAppDialog() {
        rate = Rate.Builder(this.requireContext())
            .setTriggerCount(6)
            .setMinimumInstallTime(TimeUnit.DAYS.toMillis(1).toInt())
            .setMessage(getString(R.string.rate_message))
            .setFeedbackAction(Uri.parse("mailto:syriandev96@gmail.com"))
            .setPositiveButton(getString(R.string.rate))
            .setCancelButton(getString(R.string.no_thanks))
            .setNegativeButton(getString(R.string.dont_ask))
            .build()
    }
    fun receiveTraffic(intent: Intent){
        inData.text  = intent.getStringExtra(DataTraffic.DOWNLOAD_SESSION)
        outData.text = intent.getStringExtra(DataTraffic.UPLOAD_SESSION)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.activity!!.unregisterReceiver(trafficReceiver)
    }

    private fun initViewModelObservers(){
        viewModel.restart.observe(this, Observer {
            if (it){
                startActivity(Intent(this.context,MainActivity::class.java))
                this.activity!!.finish()
            }

        })
        viewModel.currentCountryConnected.observe(this, Observer {
            card_connection.visibility = View.VISIBLE
            connected_location.text = it.countryLong
            countryFlag.setImageResource(resources.getIdentifier(it.countryShort!!.toLowerCase(),"drawable",activity!!.packageName))
        })
        viewModel.isConnected.observe(this, Observer {
            when(it){
                0->{
                    card_connection.visibility = View.GONE
                    warningScreen.visibility = View.INVISIBLE
                    connection_status.text = getString(R.string.tap_to_connect)
                    buttonConnect.progress = 0
                }
                1->{
                    connection_status.text = getString(R.string.connecting)
                    warningScreen.visibility = View.VISIBLE
                    buttonConnect.isIndeterminateProgressMode =true
                    buttonConnect.progress = 50
                    card_connection.visibility = View.GONE
                }
                2->{
                    rate.showRequest()
                    if (mInterstitialAd.isLoaded)
                        mInterstitialAd.show()
                    card_connection.visibility = View.VISIBLE
                    warningScreen.visibility = View.INVISIBLE
                    connection_status.text = getString(R.string.tap_to_disconnect)
                    buttonConnect.progress = 100
                }
            }
        })

        viewModel.vpnProfile.observe(this, Observer {
            profileValueHasChanged = it!=null
        })
        viewModel.startVPNActivityForResult.observe(this, Observer {
            when(it){
                true->{
                    val intent = VpnService.prepare(this.context)
                    if (intent!=null){
                        try {
                            startActivityForResult(intent, START_VPN_PROFILE)
                        } catch (ane: ActivityNotFoundException) {
                            VpnStatus.logError(R.string.no_vpn_support_image)
                        }

                    }else{
                        onActivityResult(START_VPN_PROFILE, RESULT_OK, null)
                    }
                }

            }
        })
    }
    private fun initButtons() {
        initViewModelObservers()
        buttonConnect.setOnClickListener {

            when {
                viewModel.isConnected.value==0 -> {
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                    viewModel.connectToServer()
                }
                viewModel.isConnected.value==1 ->{
                    card_connection.visibility = View.GONE
                    viewModel.stopVpn()
                }
                viewModel.isConnected.value==2 ->{
                card_connection.visibility = View.GONE
                    viewModel.stopVpn()
            }
            }
           
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==RESULT_OK&&requestCode== START_VPN_PROFILE&&profileValueHasChanged){
            VPNLaunchHelper.startOpenVpn(viewModel.vpnProfile.value, this.context)


        }
    }
}

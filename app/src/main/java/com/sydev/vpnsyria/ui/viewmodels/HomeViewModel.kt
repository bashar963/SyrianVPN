package com.sydev.vpnsyria.ui.viewmodels

import android.app.Application
import android.content.*
import android.os.AsyncTask
import android.os.Handler
import android.os.IBinder
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sydev.vpnsyria.R
import com.sydev.vpnsyria.VPNApp
import com.sydev.vpnsyria.model.Servers
import com.sydev.vpnsyria.utilties.*
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.*
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class HomeViewModel(application: Application) : AndroidViewModel(application) {



    private val serverRepository = getApplication<VPNApp>().getServerRepository()
    private var isBandedService = false
    private var numberTry=0
    private var br: BroadcastReceiver
    var vpnProfile: MutableLiveData<VpnProfile> = MutableLiveData()
    var restart:MutableLiveData<Boolean> = MutableLiveData()
    private var waitConnection: WaitConnectionAsync? = null
    var startVPNActivityForResult: MutableLiveData<Boolean> = MutableLiveData()
    var currentCountryConnected:MutableLiveData<Servers> = MutableLiveData()
    private var currentServer: Servers? = null
    private var statusConnection = false
    private var fastConnection: Boolean = false
    private  var mConnection:ServiceConnection
    var fromServerList:MutableLiveData<Boolean> = MutableLiveData()
    var countryName:MutableLiveData<String> = MutableLiveData()
    var isConnected : MutableLiveData<Int> = MutableLiveData()
    companion object{
        private var mVPNService: OpenVPNService?=null
        var connectedServer: Servers? = null
    }

    init {
        countryName.value = "all"
        fromServerList.value = false
         mConnection = object : ServiceConnection {
            override fun onServiceConnected(
                className: ComponentName,
                service: IBinder
            ) {
                val binder = service as OpenVPNService.LocalBinder
                mVPNService = binder.service
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                mVPNService = null
            }
        }
        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                receiveStatus(intent)
            }
        }
        getApplication<VPNApp>().registerReceiver(br, IntentFilter(Constants.BROADCAST_ACTION))


        if (currentServer != null){
            if (currentServer!!.city.isNullOrEmpty())
                serverRepository.getIpInfo(currentServer!!)
        }

        val intent = Intent(getApplication(), OpenVPNService::class.java)
        intent.action = OpenVPNService.START_SERVICE
        isBandedService = getApplication<VPNApp>().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        if (VpnStatus.isVPNActive() || connectedServer!=null)
        changeServerStatus(VpnStatus.ConnectionStatus.valueOf(VpnStatus.ConnectionStatus.LEVEL_CONNECTED.name))
        else
            changeServerStatus(VpnStatus.ConnectionStatus.valueOf(VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED.name))

    }


    //helper classes
    private fun receiveStatus(intent: Intent) {
        if (checkStatus()) {
            changeServerStatus(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status")))
        }

        if (intent.getStringExtra("detailstatus") == "NOPROCESS") {
            try {
                TimeUnit.SECONDS.sleep(1)
                if (!VpnStatus.isVPNActive())
                    prepareStopVPN()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    private fun changeServerStatus(connectionStatus: VpnStatus.ConnectionStatus) {

        when (connectionStatus) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                statusConnection = true
                isConnected.value = 2
                currentCountryConnected.value = connectedServer!!

                // server is disconnected
            }
            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED,VpnStatus.ConnectionStatus.LEVEL_AUTH_FAILED,VpnStatus.ConnectionStatus.LEVEL_NONETWORK,VpnStatus.ConnectionStatus.LEVEL_VPNPAUSED,VpnStatus.ConnectionStatus.UNKNOWN_LEVEL -> {
                // server is connected
                isConnected.value=0
                statusConnection = false
            }
            VpnStatus.ConnectionStatus.LEVEL_START,VpnStatus.ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET,VpnStatus.ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED,VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT->{
                isConnected.value=1
                statusConnection = false
            }
        }

    }

    private fun checkStatus(): Boolean {
        if (connectedServer !=null && currentServer != null){
            return if ( connectedServer!!.hostName.equals(currentServer!!.hostName)) {
                VpnStatus.isVPNActive()

            } else false
        }
        return false

    }
    private fun loadVpnProfile(): Boolean {
        val data: ByteArray
        try {
            data = Base64.decode(currentServer!!.configData, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        val cp = ConfigParser()
        val isr = InputStreamReader(ByteArrayInputStream(data))
        try {
            cp.parseConfig(isr)
            vpnProfile.value = cp.convertProfile()
            vpnProfile.value!!.mName = currentServer!!.countryLong

            ProfileManager.getInstance(getApplication()).addProfile(vpnProfile.value)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: ConfigParser.ConfigParseError) {
            e.printStackTrace()
            return false
        }


        return true
    }
    // start vpn functions
    private fun prepareVpn() {
        if (loadVpnProfile()) {
            waitConnection = WaitConnectionAsync()
            waitConnection!!.execute()
            startVpn()
        } else {
            Toast.makeText(getApplication(), "Error loading Profile", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startVpn(){
        connectedServer = currentServer
        VpnStatus.updateStateString(
                "USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT
            )
        startVPNActivityForResult.value = true
    }

    fun connectToServer(){

        if (checkStatus()|| VpnStatus.isVPNActive()){
            stopVpn()
            return
        }
        val server:Servers?
        if (ServerPreferences.getCountryPriority()&&!fromServerList.value!!){
            server = if (!ServerPreferences.getSelectedCountry().isNullOrEmpty()){
                serverRepository.getGoodRandomServer(ServerPreferences.getSelectedCountry())
            }else{
                serverRepository.getGoodRandomServer(null)
            }
        }else{
             server = if (fromServerList.value!!){
                if (countryName.value!="all")
                    serverRepository.getGoodRandomServer(countryName.value)
                else
                    serverRepository.getGoodRandomServer(null)
            }else{
                serverRepository.getGoodRandomServer(null)
            }
        }


        if (server != null){
            val fastConnect= ServerPreferences.getAutomaticSwitching()
            connect(server, fastConnect )
        }
        else{

            Toast.makeText(this.getApplication(),getApplication<VPNApp>().getString(R.string.errorConnecting),Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                restart.value = true
            },5000)
        }
    }

    private fun connect(server:Servers?, fastConnect:Boolean){
        fastConnection=fastConnect
        if (server!=null){
            currentServer = server
            if (currentServer!!.city.isNullOrEmpty())
                serverRepository.getIpInfo(currentServer!!)
            if (connectedServer !=null){
                currentServer = connectedServer
            }

        }
        prepareVpn()

    }




    //stop vpn functions

    override fun onCleared() {
        super.onCleared()
        if (isBandedService) {
            isBandedService = false
            getApplication<VPNApp>().unbindService(mConnection)
        }
        getApplication<VPNApp>().unregisterReceiver(br)
    }
    private fun prepareStopVPN() {
        statusConnection = false
        if (waitConnection != null)
            waitConnection!!.cancel(false)

        connectedServer = null
    }
    fun stopVpn(){
        ProfileManager.setConntectedVpnProfileDisconnected(getApplication())
        if (mVPNService != null && mVPNService!!.management != null)
            mVPNService!!.management.stopVPN(false)
        isConnected.value=0
        startVPNActivityForResult.value = false
    }


    private inner class WaitConnectionAsync : AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                TimeUnit.SECONDS.sleep(ServerPreferences.getAutomaticSwitchingSeconds())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (!statusConnection) {
                if (currentServer != null)
                    serverRepository.setInActive(currentServer!!.ip!!)
                if (fastConnection) {
                    serverRepository.setInActive(currentServer!!.ip!!)
                    stopVpn()
                    if (numberTry==1){
                        Toast.makeText(getApplication(),getApplication<VPNApp>().getString(R.string.take_too_long),Toast.LENGTH_LONG).show()
                        return
                    }
                    val server:Servers?
                    if (ServerPreferences.getCountryPriority()){
                        server = if (!ServerPreferences.getSelectedCountry().isNullOrEmpty()){
                            serverRepository.getGoodRandomServer(ServerPreferences.getSelectedCountry())
                        }else{
                            serverRepository.getGoodRandomServer(null)
                        }
                    }else{
                        server = if (fromServerList.value!!){
                            if (countryName.value!="all")
                                serverRepository.getGoodRandomServer(countryName.value)
                            else
                                serverRepository.getGoodRandomServer(null)
                        }else{
                            serverRepository.getGoodRandomServer(null)
                        }
                    }
                    numberTry++
                    connect(server,fastConnection)
                }else{
                    serverRepository.setInActive(currentServer!!.ip!!)
                    stopVpn()

                    Toast.makeText(getApplication(),getApplication<VPNApp>().getString(R.string.take_too_long),Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

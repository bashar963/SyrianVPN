package com.sydev.vpnsyria

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.sydev.vpnsyria.data.ServerRepository
import de.blinkt.openvpn.core.PRNGFixes
import de.blinkt.openvpn.core.VpnStatus




class VPNApp : Application(), VpnStatus.StateListener {


    private var mCreated = false

    override fun onCreate() {
        super.onCreate()

        PRNGFixes.apply()
        VpnStatus.addStateListener(this)
        MobileAds.initialize(this, "ca-app-pub-9576521051407477~6635265426")
        sThis = this
    }
    override fun updateState(state: String?, logmessage: String?, localizedResId: Int, level: VpnStatus.ConnectionStatus?) {

        if(!mCreated) {
            mCreated = true
            return
        }


    }

    fun getServerRepository() = ServerRepository(this)

    companion object{
        private lateinit var sThis: VPNApp
        fun getAppContext(): VPNApp {
            return sThis
        }
    }



}
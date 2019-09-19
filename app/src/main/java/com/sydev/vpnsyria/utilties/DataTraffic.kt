package com.sydev.vpnsyria.utilties

import android.content.Context
import android.content.Intent
import de.blinkt.openvpn.core.OpenVPNService
import java.util.ArrayList

class DataTraffic {
    companion object{
        val TRAFFIC_ACTION = "traffic_action"

        val DOWNLOAD_ALL = "download_all"
        val DOWNLOAD_SESSION = "download_session"
        val UPLOAD_ALL = "upload_all"
        val UPLOAD_SESSION = "upload_session"

        var inTotal: Long = 0
        var outTotal: Long = 0


        fun calcTraffic(context: Context, `in`: Long, out: Long, diffIn: Long, diffOut: Long) {
            val totalTraffic = getTotalTraffic(diffIn, diffOut)

            val traffic = Intent()
            traffic.action = TRAFFIC_ACTION
            traffic.putExtra(DOWNLOAD_ALL, totalTraffic[0])
            traffic.putExtra(DOWNLOAD_SESSION, OpenVPNService.humanReadableByteCount(`in`, false))
            traffic.putExtra(UPLOAD_ALL, totalTraffic[1])
            traffic.putExtra(UPLOAD_SESSION, OpenVPNService.humanReadableByteCount(out, false))

            context.sendBroadcast(traffic)
        }

        fun getTotalTraffic(): List<String> {
            return getTotalTraffic(0, 0)
        }

        fun getTotalTraffic(`in`: Long, out: Long): List<String> {
            val totalTraffic = ArrayList<String>()

            if (inTotal == 0L)
                inTotal = ServerPreferences.getDownloaded()

            if (outTotal == 0L)
                outTotal = ServerPreferences.getUploaded()

            inTotal = inTotal + `in`
            outTotal = outTotal + out

            totalTraffic.add(OpenVPNService.humanReadableByteCount(inTotal, false))
            totalTraffic.add(OpenVPNService.humanReadableByteCount(outTotal, false))

            return totalTraffic
        }

        fun saveTotal() {
            if (inTotal != 0L)
                ServerPreferences.setDownloaded(inTotal)

            if (outTotal != 0L)
                ServerPreferences.setUploaded(outTotal)
        }
    }

}
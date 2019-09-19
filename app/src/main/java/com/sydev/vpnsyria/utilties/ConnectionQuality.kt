package com.sydev.vpnsyria.utilties

class ConnectionQuality {

    companion object{

        fun getConnectionQuality(speedStr: String, sessionsStr: String, pingStr: String): Int {

            val speed = Integer.parseInt(speedStr)
            val sessions = Integer.parseInt(sessionsStr)

            var ping = 0
            if (!(pingStr == "-" || pingStr == "")) {
                ping = Integer.parseInt(pingStr)
            }

            return if (speed > 10000000 && ping < 30 && sessions != 0 && sessions < 100) {
                3
            } else if (speed < 1000000 || ping > 100 || sessions == 0 || sessions > 150) {
                1
            } else {
                2
            }
        }
    }

}
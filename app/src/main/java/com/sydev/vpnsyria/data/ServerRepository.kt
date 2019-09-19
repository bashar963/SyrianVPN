package com.sydev.vpnsyria.data

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.sydev.vpnsyria.model.Server
import com.sydev.vpnsyria.model.Servers
import com.sydev.vpnsyria.utilties.ConnectionQuality
import com.sydev.vpnsyria.utilties.Constants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Bashar Qaddah on 26.06.2018
 */

class ServerRepository(application: Application) {

    private val serverDao:ServerDAO
    init {
        val serverDB = ServerDataBase.getInstance(application)
        serverDao = serverDB.serverDao()
    }


    fun setInActive(ip:String){
        serverDao.setInActive(ip)
    }
    fun clearTable(){
        serverDao.clearTable()
    }

    fun setIPInfo(response:JSONArray, serverList:List<Servers>):Boolean{
        var result=false
        for (i in 0 until response.length()){
            try {
                val  ipInfo = JSONObject(response[i].toString())
                val city = ipInfo.getString(Constants.KEY_CITY)
                val region = ipInfo.getString(Constants.KEY_REGION_NAME)
                val lat = ipInfo.getString(Constants.KEY_LAT)
                val lon = ipInfo.getString(Constants.KEY_LON)
                val ip = ipInfo.getString("query")
                 if (serverDao.setIPInfo(city,region,lat,lon,ip)>0){
                    result=true
                }
                serverList[i].city = city
            }catch (e:JSONException){
                result=false
                e.printStackTrace()
            }
        }
        return result
    }
    fun getCount():Long{return serverDao.getCount()}
    fun getCountBasic():Long{return serverDao.getCountBasic()}
    fun getCountAdditional():Long{return serverDao.getCountAdditional()}

    fun getUniqueCountries():List<Servers>{
        val serverList = serverDao.getUniqueCountries()
        val serversList= mutableListOf<Servers>()
        for (server in serverList){
            serversList.add( parseServer(server))
        }
        return serversList.toList()
    }

    fun getServersWithGPS():List<Servers>{
        val serverList = serverDao.getServersWithGPS()
        val serversList= mutableListOf<Servers>()
        for (server in serverList){
            serversList.add( parseServer(server))
        }
        return serversList.toList()
    }
    fun getServersByCountryCode(countryCode:String):List<Servers>{
        val serverList = serverDao.getServersByCountryCode(countryCode)
        val serversList= mutableListOf<Servers>()
        for (server in serverList){
            serversList.add( parseServer(server))
        }
        return serversList.toList()
    }

    fun insertLine(line:String,type:Int){
        val data=line.split(",".toRegex())
        if (data.size==15){
            val quality = ConnectionQuality.getConnectionQuality(data[4],data[7],data[3]).toString()
            serverDao.insertLine(data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11],data[12],data[13],data[14],type.toString(),quality)
        }
    }

    fun getSimilarServer(countryLong:String,ip:String): Servers {
        return parseGoodRandomServer(serverDao.getSimilarServer(countryLong,ip))!!
    }

    fun  getGoodRandomServer(countryLong:String?):Servers?{
        return if (countryLong.isNullOrBlank()){
            parseGoodRandomServer(serverDao.getGoodRandomServer())
        }else{
            parseGoodRandomServer(serverDao.getGoodRandomServer(countryLong))
        }
    }

     fun getIpInfo(server: Servers) {
        val serverList = ArrayList<Servers>()
        serverList.add(server)
        getIpInfo(serverList)
    }

    private fun getIpInfo(serverList: List<Servers>) {
        val jsonArray = JSONArray()

        for (server in serverList) {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("query", server.ip)
                jsonObject.put("lang", Locale.getDefault().language)

                jsonArray.put(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        AndroidNetworking.post("http://ip-api.com/batch")
            .addJSONArrayBody(jsonArray)
            .setTag("getIpInfo")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    setIPInfo(response, serverList)
                }

                override fun onError(error: ANError) {

                }
            })
    }
    private fun parseGoodRandomServer(server:List<Server>):Servers?{
        val serverListExcellent = ArrayList<Servers>()
        val serverListGood = ArrayList<Servers>()
        val serverListBad = ArrayList<Servers>()

        for (s in server){
            when (s.KEY_QUALITY){
                "1"->{
                    serverListBad.add(parseServer(s))
                }
                "2"->{
                    serverListGood.add(parseServer(s))
                }
                "3"->{
                    serverListExcellent.add(parseServer(s))
                }
            }
        }

        val random = Random()
        return when {
            serverListExcellent.size > 0 -> serverListExcellent[random.nextInt(serverListExcellent.size)]
            serverListGood.size > 0 -> serverListGood[random.nextInt(serverListGood.size)]
            serverListBad.size > 0 -> serverListBad[random.nextInt(serverListBad.size)]
            else -> null
        }

    }

    private fun parseServer(server: Server): Servers {
        return if (server.KEY_CITY.isNullOrEmpty()||server.KEY_TYPE.isNullOrEmpty()||server.KEY_REGION_NAME.isNullOrEmpty()||server.KEY_LAT.isNullOrEmpty()||server.KEY_LON.isNullOrEmpty()){
            Servers(server.KEY_HOST_NAME,server.KEY_IP,server.KEY_SCORE,server.KEY_PING
                ,server.KEY_SPEED,server.KEY_COUNTRY_LONG,server.KEY_COUNTRY_SHORT,server.KEY_NUM_VPN_SESSIONS,server.KEY_UPTIME,
                server.KEY_TOTAL_USERS,server.KEY_TOTAL_TRAFFIC,server.KEY_LOG_TYPE,server.KEY_OPERATOR,server.KEY_MESSAGE,server.KEY_CONFIG_DATA,
                server.KEY_QUALITY!!.toInt(),"",0,"",0.0,0.0)
        }else{
            Servers(server.KEY_HOST_NAME,server.KEY_IP,server.KEY_SCORE,server.KEY_PING
                ,server.KEY_SPEED,server.KEY_COUNTRY_LONG,server.KEY_COUNTRY_SHORT,server.KEY_NUM_VPN_SESSIONS,server.KEY_UPTIME,
                server.KEY_TOTAL_USERS,server.KEY_TOTAL_TRAFFIC,server.KEY_LOG_TYPE,server.KEY_OPERATOR,server.KEY_MESSAGE,server.KEY_CONFIG_DATA,
                server.KEY_QUALITY!!.toIntOrNull(),server.KEY_CITY,server.KEY_TYPE!!.toIntOrNull(),server.KEY_REGION_NAME,server.KEY_LAT!!.toDoubleOrNull(),server.KEY_LON!!.toDoubleOrNull())
        }

    }
}
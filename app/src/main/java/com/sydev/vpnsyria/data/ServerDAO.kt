package com.sydev.vpnsyria.data

import androidx.room.Dao
import androidx.room.Query
import com.sydev.vpnsyria.model.Server

/**
 * Created by Bashar Qaddah on 26.06.2018
 */

@Dao
interface ServerDAO {

    @Query("UPDATE Server SET quality = 0 WHERE ip  =:ip")
    fun setInActive(ip:String)

    @Query("UPDATE Server SET city = :city AND regionName = :region_name AND lat=:lat AND lon=:lon WHERE ip=:ip ")
    fun setIPInfo(city:String,region_name:String,lat:String,lon:String,ip:String):Int

    @Query("DELETE FROM Server")
    fun clearTable()

    @Query("INSERT INTO Server (hostname,ip,score,ping,speed,countryLong,countryShort,numVpnSession,uptime,totalUser,totalTraffic,logType,operator,message,configData,type,quality) VALUES(:hostname,:ip,:score,:ping,:speed,:countryLong,:countryShort,:numVpnSession,:uptime,:totalUser,:totalTraffic,:logType,:operator,:message,:configData,:type,:quality)")
    fun insertLine(hostname:String,ip:String,score:String,ping:String,speed:String,countryLong:String,countryShort:String,numVpnSession:String,uptime:String,totalUser:String,totalTraffic:String,logType:String,operator:String,message:String,configData:String,type:String,quality:String)

    @Query("SELECT COUNT(*) FROM Server")
    fun getCount():Long

    @Query("SELECT COUNT(*) FROM Server WHERE type=0")
    fun getCountBasic():Long

    @Query("SELECT COUNT(*) FROM Server WHERE type=1")
    fun getCountAdditional():Long

    @Query("SELECT * FROM Server GROUP BY countryLong HAVING MAX(quality) ")
    fun getUniqueCountries():List<Server>

    @Query("SELECT * FROM Server WHERE lat <>0")
    fun getServersWithGPS():List<Server>

    @Query("SELECT * FROM Server WHERE countryShort=:countryCode ORDER BY quality DESC")
    fun getServersByCountryCode(countryCode:String):List<Server>

    @Query("SELECT * FROM Server WHERE quality <>1 AND countryLong=:countryLong AND ip<>:ip")
    fun getSimilarServer(countryLong:String,ip:String):List<Server>

    @Query("SELECT * FROM Server WHERE quality <>0 AND countryLong=:country")
    fun getGoodRandomServer(country:String):List<Server>

    @Query("SELECT * FROM Server WHERE quality <>0")
    fun getGoodRandomServer():List<Server>
}
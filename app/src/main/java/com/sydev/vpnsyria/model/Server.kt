package com.sydev.vpnsyria.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Server")
data class Server(
      @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")  var KEY_ID:Int,
      @ColumnInfo(name = "hostname") var KEY_HOST_NAME :String?,
      @ColumnInfo(name = "ip") var KEY_IP :String?,
      @ColumnInfo(name = "score") var KEY_SCORE :String?,
      @ColumnInfo(name = "ping") var KEY_PING:String?,
      @ColumnInfo(name = "speed") var KEY_SPEED :String?,
      @ColumnInfo(name = "countryLong") var KEY_COUNTRY_LONG:String?,
      @ColumnInfo(name = "countryShort") var KEY_COUNTRY_SHORT :String?,
      @ColumnInfo(name = "numVpnSession") var KEY_NUM_VPN_SESSIONS :String?,
      @ColumnInfo(name = "uptime") var KEY_UPTIME :String?,
      @ColumnInfo(name = "totalUser") var KEY_TOTAL_USERS :String?,
      @ColumnInfo(name = "totalTraffic") var KEY_TOTAL_TRAFFIC :String?,
      @ColumnInfo(name = "logType") var KEY_LOG_TYPE:String?,
      @ColumnInfo(name = "operator") var KEY_OPERATOR :String?,
      @ColumnInfo(name = "message") var KEY_MESSAGE :String?,
      @ColumnInfo(name = "configData") var KEY_CONFIG_DATA :String?,
      @ColumnInfo(name = "type") var KEY_TYPE :String?,
      @ColumnInfo(name = "quality") var KEY_QUALITY :String?,
      @ColumnInfo(name = "city") var KEY_CITY :String?,
      @ColumnInfo(name = "regionName") var KEY_REGION_NAME :String?,
      @ColumnInfo(name = "lat") var KEY_LAT :String?,
      @ColumnInfo(name = "lon") var KEY_LON :String?
)
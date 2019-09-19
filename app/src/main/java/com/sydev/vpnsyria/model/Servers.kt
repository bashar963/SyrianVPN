package com.sydev.vpnsyria.model

import android.os.Parcel
import android.os.Parcelable


class Servers : Parcelable {

    var hostName: String? = null
    var ip: String? = null
    var score: String? = null
    var ping: String? = null
    var speed: String? = null
    var countryLong: String? = null
    var countryShort: String? = null
    var numVpnSessions: String? = null
    var uptime: String? = null
    var totalUsers: String? = null
    var totalTraffic: String? = null
    var logType: String? = null
    var operator: String? = null
    var message: String? = null
    var configData: String? = null
    var quality: Int? = 0
    var city: String? = null
    var type: Int? = 0
    var regionName: String? = null
    var lat: Double? = 0.toDouble()
    var lon: Double? = 0.toDouble()


    protected constructor(`in`: Parcel) {
        hostName = `in`.readString()
        ip = `in`.readString()
        score = `in`.readString()
        ping = `in`.readString()
        speed = `in`.readString()
        countryLong = `in`.readString()
        countryShort = `in`.readString()
        numVpnSessions = `in`.readString()
        uptime = `in`.readString()
        totalUsers = `in`.readString()
        totalTraffic = `in`.readString()
        logType = `in`.readString()
        operator = `in`.readString()
        message = `in`.readString()
        configData = `in`.readString()
        quality = `in`.readInt()
        city = `in`.readString()
        type = `in`.readInt()
        regionName = `in`.readString()
        lat = `in`.readDouble()
        lon = `in`.readDouble()
    }

    constructor
                (
        hostName: String?,
        ip: String?,
        score: String?,
        ping: String?,
        speed: String?,
        countryLong: String?,
        countryShort: String?,
        numVpnSessions: String?,
        uptime: String?,
        totalUsers: String?,
        totalTraffic: String?,
        logType: String?,
        operator: String?,
        message: String?,
        configData: String?,
        quality: Int?,
        city: String?,
        type: Int?,
        regionName: String?,
        lat: Double?,
        lon: Double?
    ){
        this.hostName = hostName
        this.ip = ip
        this.score = score
        this.ping = ping
        this.speed = speed
        this.countryLong = countryLong
        this.countryShort = countryShort
        this.numVpnSessions = numVpnSessions
        this.uptime = uptime
        this.totalUsers = totalUsers
        this.totalTraffic = totalTraffic
        this.logType = logType
        this.operator = operator
        this.message = message
        this.configData = configData
        this.quality = quality
        this.city = city
        this.type = type
        this.regionName = regionName
        this.lat = lat
        this.lon = lon
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(hostName)
        dest.writeString(ip)
        dest.writeString(score)
        dest.writeString(ping)
        dest.writeString(speed)
        dest.writeString(countryLong)
        dest.writeString(countryShort)
        dest.writeString(numVpnSessions)
        dest.writeString(uptime)
        dest.writeString(totalUsers)
        dest.writeString(totalTraffic)
        dest.writeString(logType)
        dest.writeString(operator)
        dest.writeString(message)
        dest.writeString(configData)
        if (quality == null){
            dest.writeInt(0)
        }else{
            dest.writeInt(quality!!)
        }

        dest.writeString(city)
        if (type == null){
            dest.writeInt(0)
        }else{
            dest.writeInt(type!!)
        }
        dest.writeString(regionName)
        if (lat == null){
            dest.writeDouble(0.0)
        }else{
            dest.writeDouble(lat!!)
        }
        if (lon == null){
            dest.writeDouble(0.0)
        }else{
            dest.writeDouble(lon!!)
        }

    }


    companion object CREATOR : Parcelable.Creator<Servers> {
        override fun createFromParcel(parcel: Parcel): Servers {
            return Servers(parcel)
        }

        override fun newArray(size: Int): Array<Servers?> {
            return arrayOfNulls(size)
        }
    }
}
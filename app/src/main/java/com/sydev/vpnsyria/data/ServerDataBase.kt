package com.sydev.vpnsyria.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sydev.vpnsyria.model.Server

@Database(entities = [Server::class],version = 4,exportSchema = false)
abstract class ServerDataBase:RoomDatabase() {

    abstract fun serverDao():ServerDAO
    companion object{
        private val lock = Any()
        private const val DB_NAME = "Server.db"
        private var INSTANCE: ServerDataBase? = null

        fun getInstance(application: Application): ServerDataBase{

            synchronized(lock){
                if (INSTANCE==null){
                    INSTANCE =
                        Room.databaseBuilder(application,ServerDataBase::class.java, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries().build()
                }
            }
            return INSTANCE!!
        }
    }
}
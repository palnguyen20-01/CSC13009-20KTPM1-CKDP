package com.example.csc13009_android_ckdp.Alarm.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities=[Alarm::class],version = 1, exportSchema = false)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object{
        @Volatile
        private var INSTANCE : AlarmDatabase? = null

        fun getInstance(context: Context) : AlarmDatabase {

            val tempInstance= INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance=Room.databaseBuilder(context.applicationContext,AlarmDatabase::class.java,context.packageName)
                    .build()
                INSTANCE=instance
                return instance
            }
//            if(INSTANCE !=null) {
//
//                synchronized(this) {
//                   val instance =Room.databaseBuilder(context,AlarmDatabase::class.java,context.packageName)
//                        .build()
//                    INSTANCE=instance
//
//                }
//            }
//            return INSTANCE as AlarmDatabase
            }
        }
    }
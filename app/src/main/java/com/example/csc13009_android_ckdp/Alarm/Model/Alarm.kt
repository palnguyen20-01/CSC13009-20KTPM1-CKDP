package com.example.csc13009_android_ckdp.Alarm.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    @PrimaryKey val uid:Long?,
    @ColumnInfo var hour:Int,
    @ColumnInfo var minute:Int,
    @ColumnInfo var mon: Boolean,
    @ColumnInfo var tus: Boolean,
    @ColumnInfo var wed: Boolean,
    @ColumnInfo var thu: Boolean,
    @ColumnInfo var fri: Boolean,
    @ColumnInfo var sat: Boolean,
    @ColumnInfo var sun: Boolean,
    @ColumnInfo var start:Boolean
){
    constructor(id:Long,hour:Int,minute:Int):this(id,hour,minute,false,false,false,false,false,false,false,false)
fun getTime():String{
    return "$hour:$minute"
}

    fun getRepeat():String{
        var builder=java.lang.StringBuilder()
        if(mon){
            builder.append("Mon")
        }
        if(tus){
            builder.append(", Tus")
        }
        if(wed){
            builder.append(", Wed")
        }
        if(thu){
            builder.append(", Thu")
        }
        if(fri){
            builder.append(", Fri")
        }
        if(sat){
            builder.append(", Sat")
        }
        if(sun){
            builder.append(", Sun")
        }
        return builder.toString()
    }
}

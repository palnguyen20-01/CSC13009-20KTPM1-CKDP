package com.example.csc13009_android_ckdp.Alarm.Model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.csc13009_android_ckdp.Alarm.BroadCastReceiver.AlarmBroadCastReceiver
import java.util.*

@Entity
data class Alarm(
    @PrimaryKey val uid:Long?,
    @ColumnInfo var hour:Int,
    @ColumnInfo var minute:Int,
    @ColumnInfo var mon: Boolean,
    @ColumnInfo var tue: Boolean,
    @ColumnInfo var wed: Boolean,
    @ColumnInfo var thu: Boolean,
    @ColumnInfo var fri: Boolean,
    @ColumnInfo var sat: Boolean,
    @ColumnInfo var sun: Boolean,
    @ColumnInfo var start:Boolean,
    @ColumnInfo var content: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    constructor():this(Random().nextLong(),0,0,null)
    constructor(id:Long,hour:Int,minute:Int,content: String?):this(
        id,
        hour,
        minute,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        content
    )
fun getTime():String{
    return "$hour:$minute"
}

    fun getRepeat():String{
        var builder=java.lang.StringBuilder()
        if(mon){
            builder.append(" Mon")
        }
        if(tue){
            builder.append(" Tus")
        }
        if(wed){
            builder.append(" Wed")
        }
        if(thu){
            builder.append(" Thu")
        }
        if(fri){
            builder.append(" Fri")
        }
        if(sat){
            builder.append(" Sat")
        }
        if(sun){
            builder.append(" Sun")
        }
        return builder.toString()
    }
    fun schedule(context:Context){
        val alarmManager:AlarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(context,AlarmBroadCastReceiver::class.java)
        intent.putExtra(AlarmBroadCastReceiver.MONDAY,mon)
        intent.putExtra(AlarmBroadCastReceiver.TUESDAY,tue)
        intent.putExtra(AlarmBroadCastReceiver.WEDNESDAY,wed)
        intent.putExtra(AlarmBroadCastReceiver.THURSDAY,thu)
        intent.putExtra(AlarmBroadCastReceiver.FRIDAY,fri)
        intent.putExtra(AlarmBroadCastReceiver.SATUDAY,sat)
        intent.putExtra(AlarmBroadCastReceiver.SUNDAY,sun)
        intent.putExtra(AlarmBroadCastReceiver.RECURRING,isLoop())
        intent.putExtra(AlarmBroadCastReceiver.CONTENT,content)
        val pendingIntent = PendingIntent.getBroadcast(context,uid?.toInt()!!,intent,PendingIntent.FLAG_IMMUTABLE)

        val calendar=Calendar.getInstance()
        calendar.timeInMillis=System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)

        if(calendar.timeInMillis<=System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_WEEK,calendar.get(Calendar.DAY_OF_WEEK)+1)
        }

        val oneDay:Long=24*60*60*1000
        if (!isLoop()){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
        }else{
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,oneDay,pendingIntent)
        }
start=true
    }

    fun cancel(context: Context){
        val alarmManager:AlarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent=Intent(context,AlarmBroadCastReceiver::class.java)
        val pendingIntent=PendingIntent.getBroadcast(context,uid?.toInt()!!,intent,PendingIntent.FLAG_IMMUTABLE)
alarmManager.cancel(pendingIntent)
        start=false
    }
    private fun isLoop(): Boolean {
return mon||tue||wed||thu||fri||sat||sun
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeByte(if (mon) 1 else 0)
        parcel.writeByte(if (tue) 1 else 0)
        parcel.writeByte(if (wed) 1 else 0)
        parcel.writeByte(if (thu) 1 else 0)
        parcel.writeByte(if (fri) 1 else 0)
        parcel.writeByte(if (sat) 1 else 0)
        parcel.writeByte(if (sun) 1 else 0)
        parcel.writeByte(if (start) 1 else 0)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }

}

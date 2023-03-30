package com.example.csc13009_android_ckdp.Alarm.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.csc13009_android_ckdp.Alarm.Model.Alarm
import com.example.csc13009_android_ckdp.Alarm.Model.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application){
private var repository : AlarmRepository
var list:LiveData<List<Alarm>>

init {
    repository = AlarmRepository(application)
    list=repository.list
}
    fun insert(alarm: Alarm){
        viewModelScope.launch (Dispatchers.IO){
            repository.insert(alarm)
        }
    }

    fun delete(alarm: Alarm){
        viewModelScope.launch (Dispatchers.IO){
            repository.delete(alarm)
        }
    }

    fun update(alarm: Alarm){
        viewModelScope.launch (Dispatchers.IO){
            repository.update(alarm)
        }
    }

}
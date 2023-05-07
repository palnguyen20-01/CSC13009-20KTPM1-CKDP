package com.example.csc13009_android_ckdp.HospitalMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.encodeToString

data class Hospital (
    var name : String,
    var address : String,
    var lat : Double,
    var lng : Double,
    var distanceInText : String,
    var distanceInDouble : Double
)
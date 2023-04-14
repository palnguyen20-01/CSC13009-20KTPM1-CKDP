package com.example.csc13009_android_ckdp.Models

import android.os.Parcel
import android.os.Parcelable

class Users :Parcelable {
    var email: String = ""
    var name: String = ""
    var password: String = ""
    var birthday: String = ""
    var userId: String = ""
    var lastMessage: String = ""
    var image: String = ""

    constructor(parcel: Parcel) : this() {
        email = parcel.readString().toString()
        name = parcel.readString().toString()
        password = parcel.readString().toString()
        birthday = parcel.readString().toString()
        userId = parcel.readString().toString()
        lastMessage = parcel.readString().toString()
        image = parcel.readString().toString()
    }

    constructor(
        email: String,
        name: String,
        password: String,
        userId: String,
        lastMessage: String,
        image: String
    ) {
        this.email = email
        this.name = name
        this.password = password
        this.userId = userId
        this.lastMessage = lastMessage
        this.image = image
    }

    constructor(
        name: String,
        birthday: String
        ) {
        this.birthday = birthday
        this.name = name

    }

    constructor()
    constructor(
        email: String,
        name: String,
        image: String
        ) {
        this.email = email
        this.name = name
        this.image = image
    }
    constructor(
        email: String,
        name: String,
        image: String,
        uid: String
    ) {
        this.email = email
        this.name = name
        this.image = image
        this.userId = uid
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(password)
        parcel.writeString(birthday)
        parcel.writeString(userId)
        parcel.writeString(lastMessage)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}
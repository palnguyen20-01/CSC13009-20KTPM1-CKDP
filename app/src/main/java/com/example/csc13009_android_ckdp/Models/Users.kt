package com.example.csc13009_android_ckdp.Models

class Users {
    var email: String = ""
    var name: String = ""
    var password: String = ""
    var birthday: String = ""
    var userId: String = ""
    var lastMessage: String = ""
    var image: String = ""

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
}
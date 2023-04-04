package com.example.csc13009_android_ckdp.Models

class Users {
    var email: String = ""
    var name: String = ""
    var password: String = ""
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

    constructor()
    constructor(
        email: String,
        name: String,
        password: String,

    ) {
        this.email = email
        this.name = name
        this.password = password
    }

    constructor(
        email: String,
        name: String
        ) {
        this.email = email
        this.name = name

    }
}
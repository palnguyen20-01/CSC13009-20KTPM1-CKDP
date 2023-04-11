package com.example.csc13009_android_ckdp.HealthAdvice

class MessageModel {
    companion object {
        var SENT_BY_USER:String = "user"
        var SENT_BY_BOT:String ="assistant"
    }
    var message:String=""
    var sentBy:String=""
    constructor(message: String, sentBy: String) {
        this.message = message
        this.sentBy = sentBy
    }

}
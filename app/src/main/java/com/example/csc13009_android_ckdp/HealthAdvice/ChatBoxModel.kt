package com.example.csc13009_android_ckdp.HealthAdvice

class ChatBoxModel {
    var title:String=""
    var messages: ArrayList<MessageModel> = ArrayList<MessageModel>()
    constructor(title: String, Messages:ArrayList<MessageModel>) {
        this.title = title
        this.messages = Messages
    }
    constructor(title: String) {
        this.title = title
    }
//    fun setMessages(messages: ArrayList<MessageModel>) {
//        this.messages = messages
//    }
}
package com.example.csc13009_android_ckdp.Message

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String?, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}
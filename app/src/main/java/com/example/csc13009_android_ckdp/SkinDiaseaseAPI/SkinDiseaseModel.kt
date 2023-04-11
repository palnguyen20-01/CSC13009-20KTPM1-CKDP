package com.example.csc13009_android_ckdp.SkinDiaseaseAPI

import android.net.Uri
import androidx.appcompat.app.AlertDialog

class SkinDiseaseModel {
    var imageBytes: ByteArray = ByteArray(0)
    var diagnose : String=""
    var title: String=""
    constructor(title: String, imageBytes: ByteArray, diagnose: String) {
        this.imageBytes = imageBytes
        this.diagnose = diagnose
        this.title=title
    }
    constructor(title: String) {
        this.title = title
    }
    fun copy(): SkinDiseaseModel {
        return SkinDiseaseModel(title, imageBytes.clone(), diagnose)
    }
}
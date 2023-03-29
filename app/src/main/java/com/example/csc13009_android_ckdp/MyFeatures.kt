package com.example.csc13009_android_ckdp

class MyFeatures {
    private var featureName: String? = null
    private var featureImageId = 0

    fun getFeatureName(): String? {
        return featureName
    }

    fun setNewspaperName(newspaperName: String?) {
        this.featureName = newspaperName
    }

    fun getImageId(): Int {
        return featureImageId
    }

    fun setImageId(imageid: Int) {
        featureImageId = imageid
    }
}
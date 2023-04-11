package com.example.csc13009_android_ckdp.Models

class MyFeatures {
    private var featureName: String? = null
    private var featureImageId = 0

    fun getFeatureName(): String? {
        return featureName
    }

    fun setFeatureName(featureName: String?) {
        this.featureName = featureName
    }

    fun getImageId(): Int {
        return featureImageId
    }

    fun setImageId(imageid: Int) {
        featureImageId = imageid
    }
}
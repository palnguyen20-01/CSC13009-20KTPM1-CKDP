package com.example.csc13009_android_ckdp.DrugInfo


class DrugModel {
    var title: String="No Title"
    var interactions: String="No infomation"
    var indications: String="No infomation"
    var dosage: String="No infomation"
    var Name: String="Unknow Name"
    constructor(title: String,interactions: String,indications: String,dosage: String,Name: String ) {
        this.title = title
        this.interactions = interactions
        this.indications=indications
        this.dosage=dosage
        this.Name=Name
    }
    constructor(title: String) {
        this.title = title
    }

}
package com.husnain.admincarbuddy.data

data class ModelVendorProfile(
    var vendorImage: String = "",
    val fullName: String = "",
    val contactNumber: String = "",
    val whatsappNumber: String = "",
    val speciality: String = "",
    val yearsOfExperience: String = "",
    val city: String = "",
    val shopName: String = "",
    val availability: String = "",
    val locationNameInCity: String = "",
    val addressFromMap: String = "",
    val vendorUid: String = ""
)

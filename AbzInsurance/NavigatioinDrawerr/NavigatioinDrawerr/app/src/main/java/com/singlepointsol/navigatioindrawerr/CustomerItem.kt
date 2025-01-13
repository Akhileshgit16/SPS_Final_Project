package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class CustomerItem(
    @SerializedName("customerID") var customerID:String?=null,
    @SerializedName("customerName") var customerName:String?=null,
    @SerializedName("customerPhone") var customerPhone:String?=null,
    @SerializedName("customerEmail") var customerEmail:String?=null,
    @SerializedName("customerAddress") var customerAddress:String?=null
)

package com.singlepointsol.ABZ_Final_Project.CustomerQuery

import com.google.gson.annotations.SerializedName

data class CustomerQueryItem(
    @SerializedName("queryID") var  queryID:String,
    @SerializedName("customerID") var customerID:String?=null,
    @SerializedName("description") var description:String?=null,
    @SerializedName("queryDate") var queryDate:String?=null,
    @SerializedName("status") var status:String?=null
)

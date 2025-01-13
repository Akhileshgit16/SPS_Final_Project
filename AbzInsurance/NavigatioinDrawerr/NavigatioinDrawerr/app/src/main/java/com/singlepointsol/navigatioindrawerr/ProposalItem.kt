package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class ProposalItem(
    @SerializedName("proposalNo") var proposalNo:String,
    @SerializedName("regNo") var regNo:String?=null,
    @SerializedName("productID") var productID: String?=null,
    @SerializedName("customerID") var customerID:String?=null,
    @SerializedName("fromDate") var fromDate:String?=null,
    @SerializedName("toDate") var toDate:String?=null,
    @SerializedName("idv") var idv:String?=null,
    @SerializedName("agentID") var agentID:String?=null,
    @SerializedName("basicAmount") var basicAmount:String?=null,
    @SerializedName("totalAmount") var totalAmount:String?=null
)


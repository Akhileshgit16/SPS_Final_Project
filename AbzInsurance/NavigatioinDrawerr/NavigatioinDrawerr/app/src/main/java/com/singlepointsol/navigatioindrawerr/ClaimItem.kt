package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class ClaimItem(
    @SerializedName("claimNo") var claimNo:String?=null,
    @SerializedName("claimDate") var claimDate:String?=null,
    @SerializedName("policyNo") var policyNo:String?=null,
    @SerializedName("incidentDate") var incidentDate:String?=null,
    @SerializedName("incidentLocaion") var incidentLocaion:String?=null,
    @SerializedName("incidentLDescription") var incidentLDescription:String?=null,
    @SerializedName("claimAmount") var claimAmount:String?=null,

    )

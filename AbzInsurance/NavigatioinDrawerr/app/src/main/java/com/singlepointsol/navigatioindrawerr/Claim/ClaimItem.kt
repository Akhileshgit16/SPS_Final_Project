package com.singlepointsol.navigatioindrawerr.Claim

import com.google.gson.annotations.SerializedName

data class ClaimItem(
    @SerializedName("claimNo") var claimNo:String?=null,
    @SerializedName("claimDate") var claimDate:String?=null,
    @SerializedName("policyNo") var policyNo:String?=null,
    @SerializedName("incidentDate") var incidentDate:String?=null,
    @SerializedName("incidentLocation") var incidentLocation:String?=null,
    @SerializedName("incidentLDescription") var incidentDescription:String?=null,
    @SerializedName("claimAmount") var claimAmount:String?=null,
    @SerializedName("surveyorName") var surveyorName:String?=null,
    @SerializedName("surveyorPhone") var surveyorPhone:String?=null,
    @SerializedName("surveyDate") var surveyDate:String?=null,
    @SerializedName("surveyDescription") var surveyDescription:String?=null,
    @SerializedName("claimStatus") var claimStatus:String?=null,
    )

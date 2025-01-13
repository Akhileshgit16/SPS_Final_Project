package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class AgentItem(
    @SerializedName("agentID") var agentID: String?=null,
    @SerializedName("agentName") var agentName: String?=null,
    @SerializedName("agentPhone") var agentPhone: String?=null,
    @SerializedName("agentEmail") var agentEmail: String?=null,
    @SerializedName("licenseCode") var licenseCode: String?=null
)
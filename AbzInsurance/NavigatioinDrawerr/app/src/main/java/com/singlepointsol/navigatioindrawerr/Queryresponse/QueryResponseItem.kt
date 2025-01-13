package com.singlepointsol.navigatioindrawerr.Queryresponse

import com.google.gson.annotations.SerializedName

data class QueryResponseItem(
    @SerializedName("queryID") var queryID:String?=null,
    @SerializedName("srNo") var srNo:String?=null,
    @SerializedName("agentID") var agentID:String?=null,
    @SerializedName("description") var description:String?=null,
    @SerializedName("responseDate") var responseDate:String?=null
)

package com.singlepointsol.navigatioindrawerr.PolicyAddon

import com.google.gson.annotations.SerializedName

data class PolicyAddonItem(
    @SerializedName("addonID") var addonID:String,
    @SerializedName("policyNo") var policyNo:String,
    @SerializedName("amount") var amount:String
)

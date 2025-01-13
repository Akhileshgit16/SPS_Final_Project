package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class ProductAddonItem(
    @SerializedName("productID") var productID : String?=null,
    @SerializedName("productAddonID") var productAddonID: String?=null,
    @SerializedName("addonDescription") var addonDescription: String?=null
)
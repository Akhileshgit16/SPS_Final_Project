package com.singlepointsol.navigatioindrawerr.ProductAddon

import com.google.gson.annotations.SerializedName

data class ProductAddonItem(
    @SerializedName("productID") var productID : String,
    @SerializedName("addonID") var addonID: String,
    @SerializedName("addonTitle") var addonTitle: String,
    @SerializedName("addonDescription") var addonDescription: String
)
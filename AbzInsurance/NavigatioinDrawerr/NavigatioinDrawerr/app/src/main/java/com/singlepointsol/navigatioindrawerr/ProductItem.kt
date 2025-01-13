package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class ProductItem(
@SerializedName("productID") var productID : String,
@SerializedName("productName") var productName: String?=null,
@SerializedName("productDescription") var productDescription: String?=null,
@SerializedName("productUIN") var productUIN: String?=null,
@SerializedName("insuredInterests") var insuredInterests:String,
@SerializedName("policyCoverage") var policyCoverage: String?=null


)

package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class VehicleItem(
    @SerializedName("regNo") var regNo:String?=null,
    @SerializedName("regAuthority") var regAuthority:String?=null,
    @SerializedName("make") var make: String?=null,
    @SerializedName("model") var model:String?=null,
    @SerializedName("fuelType") var fuelType:String?=null,
    @SerializedName("variant") var variant:String?=null,
    @SerializedName("engineNo") var engineNo:String?=null,
    @SerializedName("chassisNo") var chassisNo:String?=null,
    @SerializedName("engineCapacity") var engineCapacity:String?=null,
    @SerializedName("seatingCapacity") var seatingCapacity:String?=null,
    @SerializedName("mfgYear") var mfgYear:String?=null,
    @SerializedName("regDate") var regDate:String?=null,
    @SerializedName("bodyType") var bodyType: String?=null,
    @SerializedName("leasedBy") var leasedBy:String?=null,
    @SerializedName("ownerId") var ownerId:String?=null,
    @SerializedName("owner") var owner:String?=null
)

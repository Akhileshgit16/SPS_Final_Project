package com.singlepointsol.navigatioindrawerr

import com.google.gson.annotations.SerializedName

data class PolicyItem(
    @SerializedName("policyNo") val policyNo: String? = null,
    @SerializedName("proposalNo") val proposalNo: String? = null,
    @SerializedName("noClaimBonus") val noClaimBonus: String? = null,
    @SerializedName("receiptNo") val receiptNo: String? = null,
    @SerializedName("receiptDate") val receiptDate: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("paymentMode") val paymentMode: String? = null
)
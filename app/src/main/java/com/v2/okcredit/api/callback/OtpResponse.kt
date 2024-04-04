package com.v2.okcredit.api.callback

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("UserId")
    val userId: String,
    @SerializedName("ValidOTP")
    val validOTP: Boolean
)
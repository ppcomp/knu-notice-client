package com.ppcomp.knu

import com.google.gson.annotations.SerializedName

data class KakaoUserInfo (
    @SerializedName("id") val id: String?,
//    @SerializedName("email") val email: String?,
    @SerializedName("device_id") val device_id: String?
)
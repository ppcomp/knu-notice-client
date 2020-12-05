package com.ppcomp.knu.`object`

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("id") val id: String?,
//    @SerializedName("email") val email: String?,
    @SerializedName("device") val device: String?
)
package com.ppcomp.knu.dto.device

import com.google.gson.annotations.SerializedName

open class BaseDeviceInfo(
    @SerializedName("id") val id: String,
    @SerializedName("id_method") val id_method: String
)
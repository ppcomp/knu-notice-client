package com.ppcomp.knu.`object`

import com.google.gson.annotations.SerializedName

data class Version (
    @SerializedName("latest") val latest: String?,
    @SerializedName("available_version_code") val availableVersionCode: Int
)
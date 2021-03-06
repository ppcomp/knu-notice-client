package com.ppcomp.knu.`object`

import com.google.gson.annotations.SerializedName

data class DeviceInfo (
    @SerializedName("id") val id: String?,
    @SerializedName("id_method") val id_method: String?,
    @SerializedName("keywords") val keywords: String?,
    @SerializedName("subscriptions") val subscriptions: String?,
    @SerializedName("alarm_switch_sub") val alarmSwitchSub: Boolean?,
    @SerializedName("alarm_switch_key") val alarmSwitchKey: Boolean?
)


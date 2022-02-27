package com.ppcomp.knu.`object`

import com.google.gson.annotations.SerializedName

data class DeviceInfo (
    @SerializedName("id") val id: String? = null,
    @SerializedName("id_method") val id_method: String? = null,
    val keywords: String? = null,
    val subscriptions: String? = null,
    @SerializedName("alarm_switch_sub") val alarmSwitchSub: Boolean? = null,
    @SerializedName("alarm_switch_key") val alarmSwitchKey: Boolean? = null
) {
    @SerializedName("keywords") val keywordList: List<String>? = keywords?.split("+")
    @SerializedName("subscriptions") val subscriptionList: List<String>? = subscriptions?.split("+")
}


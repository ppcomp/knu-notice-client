package com.ppcomp.knu.dto.device

import com.google.gson.annotations.SerializedName
import com.ppcomp.knu.`object`.DeviceInfo

class DeviceInfoResponse(
    id: String,
    id_method: String,
    @SerializedName("alarm_switch_sub") val alarmSwitchSub: Boolean,
    @SerializedName("alarm_switch_key") val alarmSwitchKey: Boolean,
    @SerializedName("keywords") val keywords: List<String>,
    @SerializedName("subscriptions") val subscriptions: List<String>
): BaseDeviceInfo(id, id_method) {
    constructor(deviceInfo: DeviceInfo) : this(
        deviceInfo.id!!,
        deviceInfo.id_method!!,
        deviceInfo.alarmSwitchSub!!,
        deviceInfo.alarmSwitchKey!!,
        deviceInfo.keywords?.split("+")?: emptyList(),
        deviceInfo.subscriptions?.split("+")?: emptyList()
    )
}
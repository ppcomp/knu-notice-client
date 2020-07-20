package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("id") val id: String?,
    @SerializedName("id_method") val id_method: String?,
    @SerializedName("keywords") val keywords: String?,
    @SerializedName("subscriptions") val subscriptions: String?
)


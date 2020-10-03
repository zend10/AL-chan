package com.zen.alchan.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MangaSerialization(
    @SerializedName("name")
    @Expose
    val name: String
)
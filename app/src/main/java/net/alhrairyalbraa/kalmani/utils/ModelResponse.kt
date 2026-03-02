package net.alhrairyalbraa.kalmani.utils

import com.google.gson.annotations.SerializedName

data class ModelResponse(
    @SerializedName("accuracy"       ) var accuracy    : Double?  = null,
    @SerializedName("predicted_class") var word        : String?  = null
)

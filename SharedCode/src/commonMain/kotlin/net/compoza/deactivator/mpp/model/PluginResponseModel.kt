package net.compoza.deactivator.mpp.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginResponseModel(
    val success: Boolean,
    val data: List<PluginModel>
)

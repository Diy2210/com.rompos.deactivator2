package net.compoza.deactivator.mpp.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginModel(
    val title: String,
    val plugin: String,
    val status: Boolean
)
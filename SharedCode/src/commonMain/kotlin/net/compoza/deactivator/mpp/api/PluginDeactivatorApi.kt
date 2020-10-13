package net.compoza.deactivator.mpp.api

import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import net.compoza.deactivator.mpp.model.PluginModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import net.compoza.deactivator.db.Server

class PluginDeactivatorApi {
    private val client = HttpClient()

    suspend fun getPluginsList(server: Server): String {
        return client.get("${server.url}/wp-json/deactivator/v1/list?token=${server.token}")
    }

    suspend fun updatePluginStatus(server: Server, pluginModel: PluginModel, state: Boolean): String {
        var status = "deactivate"
        if (state) {
            status = "activate"
        }
        val params = Parameters.build {
            append("token", server.token.toString())
            append("id", pluginModel.plugin)
            append("status", status)
        }

        println(params.toString())
        return client.submitForm("${server.url}/wp-json/deactivator/v1/update", params)
    }
}
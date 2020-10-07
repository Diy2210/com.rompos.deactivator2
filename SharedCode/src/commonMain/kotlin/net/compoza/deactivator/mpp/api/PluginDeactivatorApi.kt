package net.compoza.deactivator.mpp.api

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.model.PluginModel

//class PluginDeactivatorApi(server: Server) {
class PluginDeactivatorApi() {
//    companion object {
//        suspend fun getPlugins(host: String, token: String): String {
//            val client = HttpClient()
////            return client.get("${host}${path}?token=${token}") {
//            return client.get("${host}${"/wp-json/deactivator/v1/list"}?token=${token}") {
//            }
//        }
//
//        suspend fun updateStatus(host: String, path: String, token: String, id: String, state: Boolean) : String  {
//            val client = HttpClient()
//            var status: String = "deactivate"
//            if (state) {
//                status = "activate"
//            }
//            val params = Parameters.build {
//                append("token", token)
//                append("id", id)
//                append("status", status)
//            }
//            return client.submitForm("${host}${path}", params)
//        }
//    }

//    private val currentServer = server
    private val client = HttpClient()

//    suspend fun getPluginsList(): String {
//        return client.get("${currentServer.url}/wp-json/deactivator/v1/list?token=${currentServer.token}")
//    }
    suspend fun getPluginsList(url: String, token: String): String {
        return client.get("${url}/wp-json/deactivator/v1/list?token=${token}")
    }


//    suspend fun updatePluginStatus(pluginModel: PluginModel, state: Boolean): String {
//        var status: String = "deactivate"
//        if (state) {
//            status = "activate"
//        }
//        val params = Parameters.build {
//            append("token", currentServer.token.toString())
//            append("id", pluginModel.plugin)
//            append("status", status)
//        }
//
//        return client.submitForm("${currentServer.url}/wp-json/deactivator/v1/update", params)
//    }
}
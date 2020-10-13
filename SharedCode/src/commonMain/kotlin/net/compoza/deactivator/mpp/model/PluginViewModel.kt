package net.compoza.deactivator.mpp.model

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.api.PluginDeactivatorApi

class PluginViewModel : ViewModel() {

    private val client = PluginDeactivatorApi()
    lateinit var resp: PluginResponseModel

    var list: List<PluginModel> = emptyList()
    val _plugins: MutableLiveData<List<PluginModel>> = MutableLiveData(list)

    var status: String = "Loading"
    val _status: MutableLiveData<String> = MutableLiveData(status)

    private val _pluginsMutableLiveData: MutableLiveData<List<PluginModel>> =
        MutableLiveData(initialValue = List(1) {
            PluginModel(
                "Loading",
                "Loading",
                false
            )
        })

    val pluginList: MutableLiveData<List<PluginModel>> = _pluginsMutableLiveData

    fun getInitList(): MutableLiveData<List<PluginModel>> {
        _plugins.value = list
        return _plugins
    }

    fun launchAsyncRequest(server: Server) {
        viewModelScope.launch {
            try {
                client.getPluginsList(server)
                    .also { response ->
                        resp = Json.decodeFromString(PluginResponseModel.serializer(), response)
                        if (resp.success) {
                            pluginList.value = resp.data
                            _status.value = "Success"
                        } else {
                            _status.value = "Error"
                        }
                    }
            } catch (e: Exception) {
                println("Server Error: $e")
                _status.value = "Error"
            }
        }
    }

    fun getStatus(): MutableLiveData<String> {
        return _status
    }

    fun setStatus(server: Server, model: PluginModel) {
        viewModelScope.launch {
            try {
                client.updatePluginStatus(server, model, model.status.not())
            } catch (e: Exception) {
                println("Server Error: $e")
            }
        }
    }
}
package net.compoza.deactivator.mpp.model

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.api.PluginDeactivatorApi

class PluginViewModel : ViewModel() {
    lateinit var url: String
    lateinit var token: String

    private val client = PluginDeactivatorApi()
    lateinit var resp: PluginResponseModel

    var list: List<PluginModel> = emptyList()
    private val _plugins: MutableLiveData<List<PluginModel>> = MutableLiveData(list)

    private val _pluginsMutableLiveData: MutableLiveData<List<PluginModel>> =
        MutableLiveData(initialValue = List(1) {
            PluginModel(
                "Loading",
                "Loading",
                true
            )
        })

    val pluginList: MutableLiveData<List<PluginModel>> = _pluginsMutableLiveData

    fun getInitList(): MutableLiveData<List<PluginModel>> {
        _plugins.value = list
        return _plugins
    }

    fun launchAsyncRequest(url: String, token: String) {
        viewModelScope.launch {
            try {
                client.getPluginsList(url, token)
                    .also { response ->
                        resp = Json.decodeFromString(PluginResponseModel.serializer(), response)
                        if (resp.success) {
                            pluginList.value = resp.data
                        } else {
                            print("Error")
                        }
                    }
            } catch (e: Exception) {
                println("Server Error$e")
            }
        }
    }
}
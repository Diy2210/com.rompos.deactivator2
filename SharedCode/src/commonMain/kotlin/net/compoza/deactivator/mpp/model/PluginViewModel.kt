package net.compoza.deactivator.mpp.model

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.compoza.deactivator.mpp.api.PluginDeactivatorApi

class PluginViewModel : ViewModel() {

    private val client = PluginDeactivatorApi()
    lateinit var resp: PluginResponseModel

    var list: List<PluginModel> = emptyList()
    private val _plugins: MutableLiveData<List<PluginModel>> = MutableLiveData(list)

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

    fun launchAsyncRequest(url: String, token: String) {
        viewModelScope.launch {
            try {
                client.getPluginsList(url, token)
                    .also { response ->
                        resp = Json.decodeFromString(PluginResponseModel.serializer(), response)
                        if (resp.success) {
                            pluginList.value = resp.data
                            _status.value = "Success"
                        } else {
                            print("Error")
                            _status.value = "Error"
                        }
                    }
            } catch (e: Exception) {
                println("Server Error$e")
                _status.value = "Error"
            }
        }
    }

    fun getStatus(): MutableLiveData<String> {
        return _status
    }
}
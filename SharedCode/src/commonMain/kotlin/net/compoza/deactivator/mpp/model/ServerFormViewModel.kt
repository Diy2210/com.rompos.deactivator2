package net.compoza.deactivator.mpp.model

import net.compoza.deactivator.db.Server

class ServerFormViewModel {
    private var serverModel = ServerViewModel()

    fun setForm(server: Server) {
        serverModel.setModel(server)
    }

    fun setForm(viewModel: ServerViewModel) {
        serverModel = viewModel
    }

    fun getTitle() : String {
        return serverModel.title ?: ""
    }

    fun setTitle(value: String) {
        serverModel.title = value
    }

    fun getUrl() : String {
        return serverModel.url ?: ""
    }

    fun setUrl(value: String) {
        serverModel.url = value
    }

    fun getToken() : String {
        return serverModel.token ?: ""
    }

    fun setToken(value: String) {
        serverModel.token = value
    }

    fun isFormValid() : Boolean {
        return !serverModel.title.isNullOrEmpty() and !serverModel.url.isNullOrEmpty() and !serverModel.token.isNullOrEmpty()
    }

    fun getModel(id: Long?) : Server {
        return Server(id!!, serverModel.title, serverModel.url, serverModel.token)
    }

    fun getModel() : ServerViewModel {
        return serverModel
    }
}
package net.compoza.deactivator.mpp.model

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import net.compoza.deactivator.db.Server

data class ServerViewModel(
    var title: String? = null,
    var url: String? = null,
    var token: String? = null
): ViewModel() {

    fun setModel(server: Server) : ServerViewModel {
        title = server.title.toString()
        url = server.url.toString()
        token = server.token.toString()

        return this
    }
}
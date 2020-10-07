package net.compoza.deactivator.mpp.model

//import androidx.databinding.BaseObservable
//import androidx.databinding.Bindable
import net.compoza.deactivator.db.Server


//class ServerFormViewModel : BaseObservable() {
class ServerFormViewModel() {
    private var serverModel = ServerViewModel()

    fun setForm(server: Server) {
        serverModel.setModel(server)
    }

//    @Bindable
    fun getTitle() : String {
        return serverModel.title ?: ""
    }

    fun setTitle(value: String) {
        serverModel.title = value
//        notifyChange()
    }

//    @Bindable
    fun getUrl() : String {
        return serverModel.url ?: ""
    }

    fun setUrl(value: String) {
        serverModel.url = value
//        notifyChange()
    }

//    @Bindable
    fun getToken() : String {
        return serverModel.token ?: ""
    }

    fun setToken(value: String) {
        serverModel.token = value
//        notifyChange()
    }

    fun isFormValid() : Boolean {
        return !serverModel.title.isNullOrEmpty() and !serverModel.url.isNullOrEmpty() and !serverModel.token.isNullOrEmpty()
    }

    fun getModel(id: Long?) : Server {
        return Server(id!!, serverModel.title, serverModel.url, serverModel.token)
    }
}